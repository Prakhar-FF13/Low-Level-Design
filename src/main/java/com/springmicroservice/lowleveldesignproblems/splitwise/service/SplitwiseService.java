package com.springmicroservice.lowleveldesignproblems.splitwise.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springmicroservice.lowleveldesignproblems.splitwise.domain.SplitType;
import com.springmicroservice.lowleveldesignproblems.splitwise.exception.SplitwiseException;
import com.springmicroservice.lowleveldesignproblems.splitwise.persistence.entity.ExpenseEntity;
import com.springmicroservice.lowleveldesignproblems.splitwise.persistence.entity.ExpensePayerEntity;
import com.springmicroservice.lowleveldesignproblems.splitwise.persistence.entity.ExpenseSplitEntity;
import com.springmicroservice.lowleveldesignproblems.splitwise.persistence.entity.GroupEntity;
import com.springmicroservice.lowleveldesignproblems.splitwise.persistence.entity.UserEntity;
import com.springmicroservice.lowleveldesignproblems.splitwise.persistence.repository.SplitwiseExpenseRepository;
import com.springmicroservice.lowleveldesignproblems.splitwise.persistence.repository.SplitwiseGroupRepository;
import com.springmicroservice.lowleveldesignproblems.splitwise.persistence.repository.SplitwiseUserRepository;

/**
 * Single façade for Splitwise: users, groups, expenses, balances, settlement suggestions.
 * Money/split/settlement math kept as private methods at the bottom (easy to explain in interview).
 */
@Service
public class SplitwiseService {

    private static final int MONEY_SCALE = 2;
    private static final RoundingMode ROUND = RoundingMode.HALF_UP;
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final SplitwiseUserRepository users;
    private final SplitwiseGroupRepository groups;
    private final SplitwiseExpenseRepository expenses;

    public SplitwiseService(SplitwiseUserRepository users, SplitwiseGroupRepository groups, SplitwiseExpenseRepository expenses) {
        this.users = users;
        this.groups = groups;
        this.expenses = expenses;
    }

    // --- users ---

    @Transactional
    public UserEntity createUser(String name, String email) {
        if (email == null || email.isBlank()) {
            throw SplitwiseException.badRequest("Email is required");
        }
        String e = email.trim();
        if (users.existsByEmail(e)) {
            throw SplitwiseException.badRequest("Email already registered");
        }
        return users.save(UserEntity.builder().name(name).email(e).build());
    }

    @Transactional(readOnly = true)
    public UserEntity getUser(Long id) {
        return users.findById(id).orElseThrow(() -> SplitwiseException.notFound("User not found"));
    }

    // --- groups ---

    @Transactional
    public GroupEntity createGroup(String name, Long creatorId, List<Long> otherMemberIds) {
        UserEntity creator = users.findById(creatorId).orElseThrow(() -> SplitwiseException.notFound("User not found"));
        Set<UserEntity> members = new HashSet<>();
        members.add(creator);
        if (otherMemberIds != null) {
            for (Long uid : otherMemberIds) {
                if (uid.equals(creatorId)) {
                    continue;
                }
                members.add(users.findById(uid).orElseThrow(() -> SplitwiseException.notFound("User not found")));
            }
        }
        return groups.save(GroupEntity.builder().name(name).createdBy(creator).members(members).build());
    }

    @Transactional(readOnly = true)
    public GroupEntity getGroup(Long groupId) {
        return groups.findByIdWithMembers(groupId).orElseThrow(() -> SplitwiseException.notFound("Group not found"));
    }

    @Transactional
    public void addMember(Long groupId, Long userId) {
        GroupEntity g = getGroup(groupId);
        g.getMembers().add(users.findById(userId).orElseThrow(() -> SplitwiseException.notFound("User not found")));
    }

    @Transactional
    public void removeMember(Long groupId, Long userId) {
        GroupEntity g = getGroup(groupId);
        if (g.getCreatedBy().getId().equals(userId)) {
            throw SplitwiseException.badRequest("Cannot remove creator");
        }
        if (g.getMembers().stream().noneMatch(m -> m.getId().equals(userId))) {
            throw SplitwiseException.badRequest("Not a member");
        }
        if (expenses.existsExpenseWithPayerInGroup(groupId, userId) || expenses.existsExpenseWithSplitInGroup(groupId, userId)) {
            throw SplitwiseException.badRequest("User has expenses in this group");
        }
        g.getMembers().remove(users.getReferenceById(userId));
    }

    // --- expenses ---

    public record PayerLine(Long userId, BigDecimal paidAmount) {}

    public record PercentLine(Long userId, BigDecimal percent) {}

    @Transactional
    public ExpenseEntity addExpense(
            Long groupId,
            BigDecimal amount,
            String description,
            SplitType splitType,
            List<PayerLine> payers,
            List<Long> equalParticipantUserIds,
            List<PercentLine> percentLines) {
        GroupEntity group = groups.findByIdWithMembers(groupId).orElseThrow(() -> SplitwiseException.notFound("Group not found"));
        Set<Long> memberIds = group.getMembers().stream().map(UserEntity::getId).collect(Collectors.toSet());

        if (payers == null || payers.isEmpty()) {
            throw SplitwiseException.badRequest("At least one payer");
        }
        if (payers.stream().map(PayerLine::userId).distinct().count() != payers.size()) {
            throw SplitwiseException.badRequest("Duplicate payer");
        }
        BigDecimal total = scale(amount);
        BigDecimal payerSum = payers.stream().map(PayerLine::paidAmount).map(SplitwiseService::scale).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (payerSum.compareTo(total) != 0) {
            throw SplitwiseException.badRequest("Payer amounts must sum to expense total");
        }
        for (PayerLine p : payers) {
            if (!memberIds.contains(p.userId())) {
                throw SplitwiseException.badRequest("Payer must be in group");
            }
        }

        Map<Long, BigDecimal> owed;
        if (splitType == SplitType.EQUAL) {
            if (equalParticipantUserIds == null || equalParticipantUserIds.isEmpty()) {
                throw SplitwiseException.badRequest("equalParticipantUserIds required for EQUAL");
            }
            for (Long uid : equalParticipantUserIds) {
                if (!memberIds.contains(uid)) {
                    throw SplitwiseException.badRequest("Participant must be in group");
                }
            }
            owed = equalShares(amount, equalParticipantUserIds);
        } else {
            if (percentLines == null || percentLines.isEmpty()) {
                throw SplitwiseException.badRequest("percentLines required for PERCENT");
            }
            for (PercentLine line : percentLines) {
                if (!memberIds.contains(line.userId())) {
                    throw SplitwiseException.badRequest("Participant must be in group");
                }
            }
            owed = percentShares(amount, percentLines);
        }

        ExpenseEntity e =
                ExpenseEntity.builder()
                        .group(group)
                        .amount(total)
                        .description(description)
                        .splitType(splitType)
                        .createdAt(Instant.now())
                        .build();

        for (PayerLine p : payers) {
            e.getPayers()
                    .add(
                            ExpensePayerEntity.builder()
                                    .expense(e)
                                    .user(users.getReferenceById(p.userId()))
                                    .paidAmount(scale(p.paidAmount()))
                                    .build());
        }

        Map<Long, BigDecimal> pctMap =
                splitType == SplitType.PERCENT
                        ? percentLines.stream().collect(Collectors.toMap(PercentLine::userId, PercentLine::percent))
                        : Map.of();

        for (Map.Entry<Long, BigDecimal> en : owed.entrySet()) {
            var b =
                    ExpenseSplitEntity.builder()
                            .expense(e)
                            .user(users.getReferenceById(en.getKey()))
                            .owedAmount(scale(en.getValue()));
            if (splitType == SplitType.PERCENT) {
                b.percent(scale(pctMap.get(en.getKey())));
            }
            e.getSplits().add(b.build());
        }

        return expenses.save(e);
    }

    @Transactional(readOnly = true)
    public List<ExpenseEntity> listGroupExpenses(Long groupId) {
        if (!groups.existsById(groupId)) {
            throw SplitwiseException.notFound("Group not found");
        }
        return expenses.findByGroupIdOrderByCreatedAtDesc(groupId);
    }

    @Transactional(readOnly = true)
    public List<ExpenseEntity> expensesForUser(Long userId) {
        if (!users.existsById(userId)) {
            throw SplitwiseException.notFound("User not found");
        }
        List<ExpenseEntity> a = expenses.findExpensesWhereUserOwes(userId);
        List<ExpenseEntity> b = expenses.findExpensesPaidByUser(userId);
        Set<Long> seen = new HashSet<>();
        return Stream.concat(a.stream(), b.stream())
                .filter(x -> seen.add(x.getId()))
                .sorted((x, y) -> y.getCreatedAt().compareTo(x.getCreatedAt()))
                .toList();
    }

    // --- balances & who pays whom ---

    public record SettlementEdge(Long fromUserId, Long toUserId, BigDecimal amount) {}

    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> netBalances(Long groupId) {
        GroupEntity group = groups.findByIdWithMembers(groupId).orElseThrow(() -> SplitwiseException.notFound("Group not found"));
        Map<Long, BigDecimal> net = new HashMap<>();
        for (UserEntity m : group.getMembers()) {
            net.put(m.getId(), BigDecimal.ZERO);
        }
        for (ExpenseEntity e : expenses.findByGroupIdOrderByCreatedAtDesc(groupId)) {
            for (ExpensePayerEntity p : e.getPayers()) {
                Long uid = p.getUser().getId();
                net.merge(uid, scale(p.getPaidAmount()), BigDecimal::add);
            }
            for (ExpenseSplitEntity s : e.getSplits()) {
                Long uid = s.getUser().getId();
                net.merge(uid, scale(s.getOwedAmount()).negate(), BigDecimal::add);
            }
        }
        net.replaceAll((k, v) -> scale(v));
        return net;
    }

    @Transactional(readOnly = true)
    public List<SettlementEdge> settlementSuggestions(Long groupId) {
        return simplify(netBalances(groupId));
    }

    // --- private: money + splits + settlement ---

    private static BigDecimal scale(BigDecimal v) {
        return v.setScale(MONEY_SCALE, ROUND);
    }

    private static Map<Long, BigDecimal> equalShares(BigDecimal total, List<Long> participantUserIds) {
        if (participantUserIds == null || participantUserIds.isEmpty()) {
            throw SplitwiseException.badRequest("Equal split needs participants");
        }
        if (participantUserIds.stream().distinct().count() != participantUserIds.size()) {
            throw SplitwiseException.badRequest("Duplicate participant in equal split");
        }
        BigDecimal scaledTotal = scale(total);
        if (scaledTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw SplitwiseException.badRequest("Amount must be positive");
        }
        long totalCents = scaledTotal.movePointRight(MONEY_SCALE).longValueExact();
        int n = participantUserIds.size();
        long each = totalCents / n;
        long rem = totalCents % n;
        Map<Long, BigDecimal> out = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            long cents = each + (i < rem ? 1 : 0);
            out.put(participantUserIds.get(i), BigDecimal.valueOf(cents, MONEY_SCALE));
        }
        return out;
    }

    private static Map<Long, BigDecimal> percentShares(BigDecimal total, List<PercentLine> lines) {
        if (lines.stream().map(PercentLine::userId).distinct().count() != lines.size()) {
            throw SplitwiseException.badRequest("Duplicate user in percent split");
        }
        BigDecimal scaledTotal = scale(total);
        if (scaledTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw SplitwiseException.badRequest("Amount must be positive");
        }
        BigDecimal sumPct = lines.stream().map(PercentLine::percent).map(SplitwiseService::scale).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sumPct.compareTo(HUNDRED) != 0) {
            throw SplitwiseException.badRequest("Percents must sum to 100");
        }
        Map<Long, BigDecimal> out = new LinkedHashMap<>();
        BigDecimal allocated = BigDecimal.ZERO;
        for (int i = 0; i < lines.size(); i++) {
            PercentLine line = lines.get(i);
            Long uid = line.userId();
            BigDecimal pct = scale(line.percent());
            if (i < lines.size() - 1) {
                BigDecimal share = scale(scaledTotal.multiply(pct).divide(HUNDRED, MONEY_SCALE, ROUND));
                out.put(uid, share);
                allocated = allocated.add(share);
            } else {
                out.put(uid, scale(scaledTotal.subtract(allocated)));
            }
        }
        return out;
    }

    /** Greedy: match largest debtor to largest creditor until settled. */
    private static List<SettlementEdge> simplify(Map<Long, BigDecimal> netBalanceByUserId) {
        Map<Long, BigDecimal> work = new HashMap<>();
        for (var e : netBalanceByUserId.entrySet()) {
            BigDecimal v = scale(e.getValue());
            if (v.compareTo(BigDecimal.ZERO) != 0) {
                work.put(e.getKey(), v);
            }
        }
        PriorityQueue<Map.Entry<Long, BigDecimal>> creditors =
                new PriorityQueue<>(Comparator.comparing((Map.Entry<Long, BigDecimal> x) -> x.getValue()).reversed());
        PriorityQueue<Map.Entry<Long, BigDecimal>> debtors =
                new PriorityQueue<>(Comparator.comparing(Map.Entry<Long, BigDecimal>::getValue));

        for (var e : work.entrySet()) {
            if (e.getValue().compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(Map.entry(e.getKey(), e.getValue()));
            } else if (e.getValue().compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(Map.entry(e.getKey(), e.getValue()));
            }
        }

        List<SettlementEdge> out = new ArrayList<>();
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            Map.Entry<Long, BigDecimal> c = creditors.peek();
            Map.Entry<Long, BigDecimal> d = debtors.peek();
            BigDecimal pay = scale(c.getValue().min(d.getValue().negate()));
            if (pay.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            out.add(new SettlementEdge(d.getKey(), c.getKey(), pay));

            BigDecimal newC = scale(c.getValue().subtract(pay));
            BigDecimal newD = scale(d.getValue().add(pay));
            creditors.poll();
            debtors.poll();
            if (newC.compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(Map.entry(c.getKey(), newC));
            }
            if (newD.compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(Map.entry(d.getKey(), newD));
            }
        }
        return out;
    }
}

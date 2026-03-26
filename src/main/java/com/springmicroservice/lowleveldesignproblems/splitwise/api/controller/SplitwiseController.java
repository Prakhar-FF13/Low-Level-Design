package com.springmicroservice.lowleveldesignproblems.splitwise.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springmicroservice.lowleveldesignproblems.splitwise.api.SplitwiseApiDtos.AddMemberRequest;
import com.springmicroservice.lowleveldesignproblems.splitwise.api.SplitwiseApiDtos.BalanceLineResponse;
import com.springmicroservice.lowleveldesignproblems.splitwise.api.SplitwiseApiDtos.CreateExpenseRequest;
import com.springmicroservice.lowleveldesignproblems.splitwise.api.SplitwiseApiDtos.CreateGroupRequest;
import com.springmicroservice.lowleveldesignproblems.splitwise.api.SplitwiseApiDtos.CreateUserRequest;
import com.springmicroservice.lowleveldesignproblems.splitwise.api.SplitwiseApiDtos.ExpensePayerResponse;
import com.springmicroservice.lowleveldesignproblems.splitwise.api.SplitwiseApiDtos.ExpenseResponse;
import com.springmicroservice.lowleveldesignproblems.splitwise.api.SplitwiseApiDtos.ExpenseSplitResponse;
import com.springmicroservice.lowleveldesignproblems.splitwise.api.SplitwiseApiDtos.GroupResponse;
import com.springmicroservice.lowleveldesignproblems.splitwise.api.SplitwiseApiDtos.SettlementSuggestionResponse;
import com.springmicroservice.lowleveldesignproblems.splitwise.api.SplitwiseApiDtos.UserResponse;
import com.springmicroservice.lowleveldesignproblems.splitwise.domain.SplitType;
import com.springmicroservice.lowleveldesignproblems.splitwise.exception.SplitwiseException;
import com.springmicroservice.lowleveldesignproblems.splitwise.persistence.entity.ExpenseEntity;
import com.springmicroservice.lowleveldesignproblems.splitwise.persistence.entity.GroupEntity;
import com.springmicroservice.lowleveldesignproblems.splitwise.persistence.entity.UserEntity;
import com.springmicroservice.lowleveldesignproblems.splitwise.service.SplitwiseService;
import com.springmicroservice.lowleveldesignproblems.splitwise.service.SplitwiseService.PayerLine;
import com.springmicroservice.lowleveldesignproblems.splitwise.service.SplitwiseService.PercentLine;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/splitwise")
@Validated
public class SplitwiseController {

    private final SplitwiseService splitwise;

    public SplitwiseController(SplitwiseService splitwise) {
        this.splitwise = splitwise;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest req) {
        UserEntity u = splitwise.createUser(req.name(), req.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(toUser(u));
    }

    @GetMapping("/users/{userId}")
    public UserResponse getUser(@PathVariable Long userId) {
        return toUser(splitwise.getUser(userId));
    }

    @GetMapping("/users/{userId}/expenses")
    @Transactional(readOnly = true)
    public List<ExpenseResponse> userExpenses(@PathVariable Long userId) {
        return splitwise.expensesForUser(userId).stream().map(this::toExpense).toList();
    }

    @PostMapping("/groups")
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody CreateGroupRequest req) {
        GroupEntity g = splitwise.createGroup(req.name(), req.creatorUserId(), req.memberUserIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(toGroup(g));
    }

    @GetMapping("/groups/{groupId}")
    public GroupResponse getGroup(@PathVariable Long groupId) {
        return toGroup(splitwise.getGroup(groupId));
    }

    @PostMapping("/groups/{groupId}/members")
    public GroupResponse addMember(@PathVariable Long groupId, @Valid @RequestBody AddMemberRequest req) {
        splitwise.addMember(groupId, req.userId());
        return toGroup(splitwise.getGroup(groupId));
    }

    @DeleteMapping("/groups/{groupId}/members/{userId}")
    public GroupResponse removeMember(@PathVariable Long groupId, @PathVariable Long userId) {
        splitwise.removeMember(groupId, userId);
        return toGroup(splitwise.getGroup(groupId));
    }

    @PostMapping("/groups/{groupId}/expenses")
    @Transactional
    public ResponseEntity<ExpenseResponse> addExpense(@PathVariable Long groupId, @Valid @RequestBody CreateExpenseRequest req) {
        List<PayerLine> payers = req.payers().stream().map(p -> new PayerLine(p.userId(), p.paidAmount())).collect(Collectors.toList());
        List<Long> equalIds = req.equalParticipantUserIds();
        List<PercentLine> pct =
                req.percentLines() == null
                        ? List.of()
                        : req.percentLines().stream().map(p -> new PercentLine(p.userId(), p.percent())).collect(Collectors.toList());
        if (req.splitType() == SplitType.EQUAL && (equalIds == null || equalIds.isEmpty())) {
            throw SplitwiseException.badRequest("equalParticipantUserIds required for EQUAL");
        }
        if (req.splitType() == SplitType.PERCENT && pct.isEmpty()) {
            throw SplitwiseException.badRequest("percentLines required for PERCENT");
        }
        ExpenseEntity e =
                splitwise.addExpense(
                        groupId, req.amount(), req.description(), req.splitType(), payers, equalIds, pct);
        return ResponseEntity.status(HttpStatus.CREATED).body(toExpense(e));
    }

    @GetMapping("/groups/{groupId}/expenses")
    @Transactional(readOnly = true)
    public List<ExpenseResponse> listExpenses(@PathVariable Long groupId) {
        return splitwise.listGroupExpenses(groupId).stream().map(this::toExpense).toList();
    }

    @GetMapping("/groups/{groupId}/balances")
    public List<BalanceLineResponse> balances(@PathVariable Long groupId) {
        var net = splitwise.netBalances(groupId);
        List<BalanceLineResponse> out = new ArrayList<>();
        for (var e : net.entrySet()) {
            out.add(new BalanceLineResponse(e.getKey(), e.getValue()));
        }
        return out;
    }

    @GetMapping("/groups/{groupId}/settlement-suggestions")
    public List<SettlementSuggestionResponse> suggestions(@PathVariable Long groupId) {
        return splitwise.settlementSuggestions(groupId).stream()
                .map(s -> new SettlementSuggestionResponse(s.fromUserId(), s.toUserId(), s.amount()))
                .toList();
    }

    private static UserResponse toUser(UserEntity u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail());
    }

    private static GroupResponse toGroup(GroupEntity g) {
        return new GroupResponse(
                g.getId(),
                g.getName(),
                g.getCreatedBy().getId(),
                g.getMembers().stream().map(UserEntity::getId).collect(Collectors.toSet()));
    }

    private ExpenseResponse toExpense(ExpenseEntity e) {
        return new ExpenseResponse(
                e.getId(),
                e.getGroup().getId(),
                e.getAmount(),
                e.getDescription(),
                e.getSplitType(),
                e.getCreatedAt(),
                e.getPayers().stream().map(p -> new ExpensePayerResponse(p.getUser().getId(), p.getPaidAmount())).toList(),
                e.getSplits().stream()
                        .map(s -> new ExpenseSplitResponse(s.getUser().getId(), s.getOwedAmount(), s.getPercent()))
                        .toList());
    }
}

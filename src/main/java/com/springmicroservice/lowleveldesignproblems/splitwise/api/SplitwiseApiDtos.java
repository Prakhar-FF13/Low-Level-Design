package com.springmicroservice.lowleveldesignproblems.splitwise.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import com.springmicroservice.lowleveldesignproblems.splitwise.domain.SplitType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/** All request/response shapes in one place (interview-friendly). */
public final class SplitwiseApiDtos {

    private SplitwiseApiDtos() {}

    public record CreateUserRequest(@NotBlank String name, @NotBlank @Email String email) {}

    public record UserResponse(Long id, String name, String email) {}

    public record CreateGroupRequest(@NotBlank String name, @NotNull Long creatorUserId, List<Long> memberUserIds) {}

    public record GroupResponse(Long id, String name, Long createdByUserId, Set<Long> memberUserIds) {}

    public record AddMemberRequest(@NotNull Long userId) {}

    public record PayerLineRequest(@NotNull Long userId, @NotNull BigDecimal paidAmount) {}

    public record PercentLineRequest(@NotNull Long userId, @NotNull BigDecimal percent) {}

    public record CreateExpenseRequest(
            @NotNull BigDecimal amount,
            String description,
            @NotNull SplitType splitType,
            @NotEmpty @Valid List<PayerLineRequest> payers,
            List<Long> equalParticipantUserIds,
            List<@Valid PercentLineRequest> percentLines) {}

    public record ExpensePayerResponse(Long userId, BigDecimal paidAmount) {}

    public record ExpenseSplitResponse(Long userId, BigDecimal owedAmount, BigDecimal percent) {}

    public record ExpenseResponse(
            Long id,
            Long groupId,
            BigDecimal amount,
            String description,
            SplitType splitType,
            Instant createdAt,
            List<ExpensePayerResponse> payers,
            List<ExpenseSplitResponse> splits) {}

    public record BalanceLineResponse(Long userId, BigDecimal netAmount) {}

    public record SettlementSuggestionResponse(Long fromUserId, Long toUserId, BigDecimal amount) {}
}

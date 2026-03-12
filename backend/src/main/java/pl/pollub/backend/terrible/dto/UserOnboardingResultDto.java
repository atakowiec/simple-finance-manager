package pl.pollub.backend.terrible.dto;

import lombok.Data;

/**
 * DTO returned after onboarding: user, group and first expense summary.
 */
@Data
public class UserOnboardingResultDto {
    private Long userId;
    private String username;
    private String email;

    private Long groupId;
    private String groupName;
    private Double expenseLimit;

    private Long expenseId;
    private String expenseName;
    private Double amount;
    private Long categoryId;

    private UserOnboardingResultDto(Builder builder) {
        this.userId = builder.userId;
        this.username = builder.username;
        this.email = builder.email;
        this.groupId = builder.groupId;
        this.groupName = builder.groupName;
        this.expenseLimit = builder.expenseLimit;
        this.expenseId = builder.expenseId;
        this.expenseName = builder.expenseName;
        this.amount = builder.amount;
        this.categoryId = builder.categoryId;
    }

    public static class Builder {
        private Long userId;
        private String username;
        private String email;
        private Long groupId;
        private String groupName;
        private Double expenseLimit;
        private Long expenseId;
        private String expenseName;
        private Double amount;
        private Long categoryId;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder groupId(Long groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder groupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public Builder expenseLimit(Double expenseLimit) {
            this.expenseLimit = expenseLimit;
            return this;
        }

        public Builder expenseId(Long expenseId) {
            this.expenseId = expenseId;
            return this;
        }

        public Builder expenseName(String expenseName) {
            this.expenseName = expenseName;
            return this;
        }

        public Builder amount(Double amount) {
            this.amount = amount;
            return this;
        }

        public Builder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public UserOnboardingResultDto build() {
            return new UserOnboardingResultDto(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}

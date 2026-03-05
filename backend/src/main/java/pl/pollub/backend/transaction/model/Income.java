package pl.pollub.backend.transaction.model;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

/**
 * Entity representing income. It holds information about income.
 */
@Entity
@NoArgsConstructor
public class Income extends Transaction {
    @Override
    public Income clone() {
        try {
            return (Income) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cloning failed", e);
        }
    }
}
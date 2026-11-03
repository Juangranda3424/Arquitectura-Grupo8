package ec.edu.monster.banquito.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movements", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"number"}, name = "uc_movements_number")
})
@EntityListeners(AuditingEntityListener.class)
public class Movement {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false, columnDefinition = "NUMERIC(10, 2)")
    private BigDecimal amount;

    @SequenceGenerator(name = "movement_number_seq", sequenceName = "movement_number_seq", allocationSize = 1)
    @Column(nullable = false, columnDefinition = "SERIAL", unique = true, insertable = false, updatable = false)
    private Long number;

    @Column(unique = true, updatable = false, insertable = false)
    private String reference;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "source_bank_account_id")
    @Nullable
    private BankAccount sourceAccount;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "target_bank_account_id", nullable = false)
    private BankAccount targetAccount;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private User sender;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (type == MovementType.TRANSFER && sourceAccount == null) {
            throw new IllegalArgumentException("La cuenta de origen es requerida para un movimiento de tipo transferencia");
        }
    }
}

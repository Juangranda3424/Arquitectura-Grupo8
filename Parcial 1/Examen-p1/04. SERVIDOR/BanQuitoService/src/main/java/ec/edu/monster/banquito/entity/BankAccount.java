package ec.edu.monster.banquito.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "bank_accounts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "type"}, name = "uc_bank_accounts_user_id_type")
})
@EntityListeners(AuditingEntityListener.class)
public class BankAccount {
    @Id
    @GeneratedValue
    private UUID id;

    @SequenceGenerator(name = "bank_account_number_seq", sequenceName = "bank_account_number_seq", allocationSize = 1)
    @Column(nullable = false, columnDefinition = "SERIAL", unique = true, insertable = false, updatable = false)
    private Long number;

    @Column(unique = true, updatable = false, insertable = false)
    private String reference;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BankAccountType type;

    @Column(nullable = false, columnDefinition = "NUMERIC(10, 2)")
    private BigDecimal balance;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private User user;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

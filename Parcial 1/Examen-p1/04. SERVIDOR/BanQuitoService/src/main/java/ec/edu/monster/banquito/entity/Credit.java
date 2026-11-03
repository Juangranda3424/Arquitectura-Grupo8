package ec.edu.monster.banquito.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "credits")
@EntityListeners(AuditingEntityListener.class)
public class Credit {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "NUMERIC(10, 2)")
    private BigDecimal amount;

    @Column(nullable = false)
    private int totalInstallments;

    @Column(nullable = false, columnDefinition = "NUMERIC(4, 2)")
    private BigDecimal interestRate;

    @Column(nullable = false, columnDefinition = "NUMERIC(10, 2)")
    private BigDecimal installmentAmount;

    @Column(nullable = false)
    private boolean paid;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

package dev.tanmay.contactmanagementsystem.model;

import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_contact_id", columnList = "contact_id" ),
                @Index(name = "idx_audit_changed_at", columnList = "changed_at")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("JpaDataSourceORMInspection")

public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "contact_id", nullable = false, updatable = false)
    private UUID contactId;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", nullable = false, length = 20, updatable = false)
    private MessageStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20, updatable = false)
    private MessageStatus newStatus;

    @Column(name = "changed_by", length = 100, updatable = false)
    private String changedBy;

    @Column(length = 500, updatable = false)
    private String note;

    @CreatedDate
    @Column(name = "changed_at", updatable = false, nullable = false)
    private Instant changedAt;

    // -- Factory method -- clean creation --
    public static AuditLog of( UUID contactId,
                               MessageStatus from,
                               MessageStatus to,
                               String actor,
                               String note){
        return AuditLog.builder()
                .contactId(contactId)
                .oldStatus(from)
                .newStatus(to)
                .changedBy(actor)
                .note(note)
                .build();
    }

}

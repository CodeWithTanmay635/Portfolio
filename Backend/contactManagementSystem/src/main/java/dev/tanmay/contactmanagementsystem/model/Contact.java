package dev.tanmay.contactmanagementsystem.model;


import dev.tanmay.contactmanagementsystem.model.enums.MessagePriority;
import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "contacts",
        indexes = {
                @Index(name = "idx_contacts_status", columnList = "status"),
                @Index(name = "idx_contacts_priority", columnList = "priority"),
                @Index(name = "idx_contacts_email", columnList = "email"),
                @Index(name = "idx_contacts_created", columnList = "created_at")
        }
)

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"message"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SuppressWarnings("JpaDataSourceORMInspection")
public class Contact {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @EqualsAndHashCode.Include
        @Column(nullable = false, unique = true)
        private UUID id;

        @NotBlank
        @Size(max = 100)
        @Column(updatable = false, nullable = false)
        private String name;

        @Email
        @Size(max = 100)
        @Column(updatable = false, nullable = false)
        private String email;

        @NotBlank
        @Size(max = 2000)
        @Column(updatable = false, nullable = false, length = 2000)
        private String message;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        @Builder.Default
        private MessageStatus status = MessageStatus.NEW;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        @Builder.Default
        private MessagePriority priority = MessagePriority.LOW;

        @Column(name = "priority_score")
        private int priorityScore;

        @Column(name = "ip_address", length = 45)
        private String ipAddress;

        @Column(name = "user_agent" , length = 500)
        private String userAgent;

        @Version
        @Column(nullable = false)
        private long version;

        @CreatedDate
        @Column(name = "created_at", updatable = false, nullable = false)
        private Instant createdAt;

        @LastModifiedDate
        @Column(name = "updated_at")
        private Instant updatedAt;

        @Column(name = "replied_at")
        private Instant repliedAt;

        @Column(name = "admin_note", length = 1000)
        private String adminNote;

        public void markReplied() {
                this.status = MessageStatus.REPLIED;
                this.repliedAt = Instant.now();
        }
}

package dev.tanmay.contactmanagementsystem.repository;

import dev.tanmay.contactmanagementsystem.model.AuditLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {


    List<AuditLog> findByContactIdOrderByChangedAtDesc(UUID contactId);

    Page<AuditLog> findByContactId(UUID contactId, Pageable pageable);

    Page<AuditLog> findByChangeBy(String changedBy, Pageable pageable);

    List<AuditLog> findByContactIdAndNewStatus(
            UUID contactId,
            String newStatus
    );

    Page<AuditLog> findByChangedAtBetween(
            Instant from,
            Instant to,
            Pageable pageable
    );

    Page<AuditLog> findByContactId(UUID contact);

    long countByContactId(UUID contact);
}

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

 List<AuditLog> findAllByContactIdOrderedByChangedAtAsc(UUID ContactId);
 List<AuditLog> findAllByContactIdOrderedByChangedAtDesc(UUID ContactId);

    List<AuditLog> findByChangedAtBetween(
            Instant from,
            Instant to
    );
}

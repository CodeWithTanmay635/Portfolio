package dev.tanmay.contactmanagementsystem.repository;

import dev.tanmay.contactmanagementsystem.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<Contact,Long> {

}

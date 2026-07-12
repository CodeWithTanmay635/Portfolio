package dev.tanmay.contactmanagementsystem.repository;

import dev.tanmay.contactmanagementsystem.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<Contact,Long> {

}

package dev.tanmay.contactmanagementsystem.repository;

import dev.tanmay.contactmanagementsystem.model.Contact;
import dev.tanmay.contactmanagementsystem.model.enums.MessagePriority;
import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID>, JpaSpecificationExecutor<Contact> {
    //Spring reads these methods and builds
    //SELECT * FROM contact_message WHERE status = ?
//    List<Contact> findByStatus(MessageStatus status);
//
//    //SELECT * FROM contact_message ORDER BY created_at DESC
//    List<Contact> findAllByOrderByCreatedAtDesc();



        // Admin filters

        Page<Contact> findByStatus(
                MessageStatus status,
                Pageable pageable
        );

        Page<Contact> findByPriority(
                MessagePriority priority,
                Pageable pageable
        );

        Page<Contact> findByStatusAndPriority(
                MessageStatus status,
                MessagePriority priority,
                Pageable pageable
        );

        // Search

        List<Contact> findByEmail(String email);

        List<Contact> findByNameContainingIgnoreCase(String name);

        Optional<Contact> findByReferenceId(String referenceId);

        // Dashboard statistics

        long countByStatus(MessageStatus status);

        long countByPriority(MessagePriority priority);

        // Date filtering

        Page<Contact> findByCreatedAtBetween(
                Instant from,
                Instant to,
                Pageable pageable
        );

        boolean existsByEmailAndCreatedAtAfter(
                @NotBlank(message = "Email required")
                @Email(message = "Enter Valid Message")
                @Size(max = 255)
                String email, Instant minus);
}










//    //SELECT * FROM contact_message WHERE email = ?
//    List<ContactMessage> findByEmail(String email);

//    Spring Data JPA Repository
//    What it is: An interface that, when extended from JpaRepository<Entity, IdType>,
//    gives you full CRUD operations and custom query methods without writing any implementation or SQL.
//    Problem it solves: Eliminates the repetitive boilerplate of manually
//    writing SQL/JDBC code for every entity's basic database operations.
//    How it's implemented: Spring generates a real implementation class at runtime via dynamic proxies,
//    registers it as a Bean, and injects it wherever needed — same DI mechanism you already understand.
//    Bonus power — Query Derivation: Method names following the pattern findBy<FieldName> are parsed
//    by Spring to auto-generate the corresponding SQL query.
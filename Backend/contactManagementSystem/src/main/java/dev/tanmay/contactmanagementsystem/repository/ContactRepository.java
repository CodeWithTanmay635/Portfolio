package dev.tanmay.contactmanagementsystem.repository;

import dev.tanmay.contactmanagementsystem.model.Contact;
import dev.tanmay.contactmanagementsystem.model.enums.MessagePriority;
import dev.tanmay.contactmanagementsystem.model.enums.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
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

    Optional<Contact> findByIdAndClientId(UUID uuid, String clientId);

    Page<Contact> findByClientId(
            String clientId,
            Pageable pageable
    );

    Page<Contact> findByClientIdAndPriority(
            String clientId,
            MessagePriority priority,
            Pageable pageable
    );

    Page<Contact> findByClientIdStatusAndPriority(
            String clientId,
            MessageStatus status,
            MessagePriority priority,
            Pageable pageable
    );

    Page<Contact> findByClientIdAndCreatedAtBetween(
            String clientId,
            Instant from,
            Instant to,
            Pageable pageable
    );

    List<Contact> findByClientIdAndEmail(
            String clientId,
            String email
    );

    long countByClientId(String clientId);

    long countByClientIdAndStatus(
            String clientId,
            MessageStatus status
    );

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
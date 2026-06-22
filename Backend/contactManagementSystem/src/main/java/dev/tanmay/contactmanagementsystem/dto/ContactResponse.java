package dev.tanmay.contactmanagementsystem.dto;

import dev.tanmay.contactmanagementsystem.model.MessageStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContactResponse {
    private long id;
    private String message;
    private MessageStatus status;
    private LocalDateTime createdAt;
}

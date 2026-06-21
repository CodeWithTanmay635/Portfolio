package dev.tanmay.contactmanagementsystem.dto;

import dev.tanmay.contactmanagementsystem.model.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponse {
    private String id;
    private String name;
    private String message;
    private MessageStatus status;
    private LocalDateTime createdAt;
}

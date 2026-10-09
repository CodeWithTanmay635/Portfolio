package dev.tanmay.contactmanagementsystem.model.enums;

import java.util.Set;

public enum MessageStatus {
    NEW,
    READ,
    PENDING,
    REPLIED,
    ARCHIVED;



    public boolean canTransitionTo(MessageStatus next) {
        return switch (this){
            case NEW -> next == READ || next == PENDING;
            case READ -> next == PENDING || next == ARCHIVED;
            case PENDING -> next == REPLIED || next == ARCHIVED ;
            case REPLIED -> next == ARCHIVED ;
            case ARCHIVED -> false;
        };
    }

    public static final Set<MessageStatus> REPLYABLE_STATUSES = Set.of(
            MessageStatus.NEW,
            MessageStatus.READ,
            MessageStatus.PENDING
    );
}
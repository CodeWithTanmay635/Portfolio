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
            case PENDING -> next == ARCHIVED || next == REPLIED;
            case REPLIED -> next == ARCHIVED;
            case ARCHIVED -> false;
        };
    }

    public static final Set<MessageStatus> REPLYABLE_STATUSES = Set.of(
            MessageStatus.NEW,
            MessageStatus.READ,
            MessageStatus.PENDING
    );
}
/**
this class is for message lifecycle which states weather the message is in NEW, READ,
PENDING, REPLIED, & ARCHIVED
Why do we need this class?
because if we used String to manage message Lifecycle it would give vulnerable bugs,
data entry typos
 if it was done like this String status = "NEW" this may crash the system
 because this can cause spelling mistake if we used String it may create typos in business layers
 like sometimes it may NEW, new, New so enums make it sure for Type safety so that in DB
 it won't be messed up with New, new, and NEW
 Databases will gladly save whatever text string you throw at them, raw strings ruin your tracking loops.
 An enum forces type safety.
 **/
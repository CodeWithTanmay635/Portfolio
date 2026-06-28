package dev.tanmay.contactmanagementsystem.model.enums;

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
}

package dev.tanmay.contactmanagementsystem.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessagePriority{
    CRITICAL(80, " Immediate attention required"),
    HIGH(60, "Respond withing 24 hours"),
    MEDIUM(35, "Respond within 3 days"),
    LOW(0, "Respond when available");

    private final int minScore;
    private final String sal;

    public static MessagePriority fromScore(int score){
        if(score >= CRITICAL.minScore) return CRITICAL;
        if(score >= HIGH.minScore) return HIGH;
        if(score >= MEDIUM.minScore) return MEDIUM;
        return LOW;
    }
}
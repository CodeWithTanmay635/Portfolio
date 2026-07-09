package dev.tanmay.contactmanagementsystem.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,

        String message,

        T data,

        Instant timeStamp,

        String traceId
) {
}

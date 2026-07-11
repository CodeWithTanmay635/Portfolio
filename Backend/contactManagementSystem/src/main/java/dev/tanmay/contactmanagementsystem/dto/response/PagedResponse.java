package dev.tanmay.contactmanagementsystem.dto.response;

import java.util.List;

public record PagedResponse<T>(
        List<T> context,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
}

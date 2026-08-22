package dev.tanmay.contactmanagementsystem.dto.response;

import org.springframework.data.domain.Page;
import java.util.List;

public record PagedResponse<T>(

        List<T> content,

        int page,

        int size,

        long totalElement,

        int totalPage,

        boolean first,

        boolean last
){
    public  static <T> PagedResponse<T> from(Page<T> page){
        return new  PagedResponse<T>(

                page.getContent(),

                page.getNumber(),

                page.getSize(),

                page.getTotalElements(),

                page.getTotalPages(),

                page.isFirst(),

                page.isLast()
        );
    }
}

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

}

package sk.stuba.fei.uim.vsa.pr2.bonus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    private List<T> content;
    private PagedResponse.Pageable page;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pageable {
        private Integer number;
        private Integer size;
        private Integer totalElements;
        private Integer totalPages;
    }

}

package sk.stuba.fei.uim.vsa.pr2.bonus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchStudentsRequest {

    private String name;
    private Integer year;

}

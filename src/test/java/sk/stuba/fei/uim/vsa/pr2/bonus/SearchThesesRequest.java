package sk.stuba.fei.uim.vsa.pr2.bonus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.ThesisResponse;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchThesesRequest {

    private Long studentId;
    private Long teacherId;
    private String department;
    private LocalDate publishedOn;
    private ThesisResponse.Type type;
    private ThesisResponse.Status status;

}

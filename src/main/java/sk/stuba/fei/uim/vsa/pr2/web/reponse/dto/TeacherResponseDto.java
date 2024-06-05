package sk.stuba.fei.uim.vsa.pr2.web.reponse.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import sk.stuba.fei.uim.vsa.pr2.domain.Thesis;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TeacherResponseDto extends Dto{
    private Long id;
    private Long aisId;
    private String name;
    private String email;
    private String institute;
    private String department;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ThesisResponseDto> theses;
}

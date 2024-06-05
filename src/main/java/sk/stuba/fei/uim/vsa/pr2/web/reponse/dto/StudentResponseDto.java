package sk.stuba.fei.uim.vsa.pr2.web.reponse.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import sk.stuba.fei.uim.vsa.pr2.domain.Thesis;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StudentResponseDto extends Dto{
    private Long id;
    private Long aisId;
    private String name;
    private String email;
    private Long year;
    private Long term;
    private String programme;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ThesisResponseDto thesis;
}

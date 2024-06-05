package sk.stuba.fei.uim.vsa.pr2.web.reponse.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ThesisRequestDtoTeacher extends Dto{
    private Long teacherId;

}

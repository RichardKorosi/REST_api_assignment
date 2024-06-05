package sk.stuba.fei.uim.vsa.pr2.model.dto.response.student;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.ThesisResponse;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentWithThesisResponse extends StudentResponse {

    private ThesisResponse thesis;

    public StudentWithThesisResponse() {
    }

    public StudentWithThesisResponse(Long id, Long aisId, String email) {
        super(id, aisId, email);
    }
}

package sk.stuba.fei.uim.vsa.pr2.model.dto.response.teacher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.ThesisResponse;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherWithThesesResponse extends TeacherResponse {

    private List<ThesisResponse> theses;

    public TeacherWithThesesResponse() {
        theses = new ArrayList<>();
    }

    public TeacherWithThesesResponse(Long id, Long aisId, String email) {
        super(id, aisId, email);
        theses = new ArrayList<>();
    }
}

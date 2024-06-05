package sk.stuba.fei.uim.vsa.pr2.model.dto.response.teacher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherAltResponse extends TeacherResponse {

    private List<Long> theses;

    public TeacherAltResponse() {
    }

    public TeacherAltResponse(Long id, Long aisId, String email) {
        super(id, aisId, email);
    }
}

package sk.stuba.fei.uim.vsa.pr2.model.dto.response.teacher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class TeacherResponse {

    private Long id;
    private Long aisId;
    private String name;
    private String email;
    private String institute;
    private String department;

    public TeacherResponse() {
    }

    public TeacherResponse(Long id, Long aisId, String email) {
        this.id = id;
        this.aisId = aisId;
        this.email = email;
    }
}

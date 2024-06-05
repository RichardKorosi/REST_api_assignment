package sk.stuba.fei.uim.vsa.pr2.model.dto.response.student;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class StudentResponse {

    private Long id;
    private Long aisId;
    private String name;
    private String email;
    private Integer year;
    private Integer term;
    private String programme;

    public StudentResponse() {
    }

    public StudentResponse(Long id, Long aisId, String email) {
        this.id = id;
        this.aisId = aisId;
        this.email = email;
    }
}

package sk.stuba.fei.uim.vsa.pr2.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateStudentRequest {

    private Long aisId;
    private String name;
    private String email;
    private String password;
    private Integer year;
    private Integer term;
    private String programme;

    public CreateStudentRequest() {
    }

    public CreateStudentRequest(Long aisId, String name, String email, String password) {
        this.aisId = aisId;
        this.name = name;
        this.email = email;
        this.password = password;
    }
}

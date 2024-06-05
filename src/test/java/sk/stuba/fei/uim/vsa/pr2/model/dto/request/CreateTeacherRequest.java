package sk.stuba.fei.uim.vsa.pr2.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateTeacherRequest {

    private Long aisId;
    private String name;
    private String email;
    private String password;
    private String institute;
    private String department;

    public CreateTeacherRequest() {
    }

    public CreateTeacherRequest(Long aisId, String name, String email, String password) {
        this.aisId = aisId;
        this.name = name;
        this.email = email;
        this.password = password;
    }
}

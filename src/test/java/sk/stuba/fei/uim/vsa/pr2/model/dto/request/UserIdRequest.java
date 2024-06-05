package sk.stuba.fei.uim.vsa.pr2.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserIdRequest {

    private Long studentId;
    private Long teacherId;

    public UserIdRequest() {
    }


}

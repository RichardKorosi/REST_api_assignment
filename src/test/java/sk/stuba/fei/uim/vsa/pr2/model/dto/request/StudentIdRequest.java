package sk.stuba.fei.uim.vsa.pr2.model.dto.request;

import lombok.Data;

@Data
public class StudentIdRequest {

    private Long studentId;

    public StudentIdRequest() {
    }

    public StudentIdRequest(Long studentId) {
        this.studentId = studentId;
    }
}

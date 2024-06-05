package sk.stuba.fei.uim.vsa.pr2.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateThesisRequest {

    private String registrationNumber;
    private String title;
    private String description;
    private String type;

    public CreateThesisRequest() {
    }

    public CreateThesisRequest(String registrationNumber, String title, String type) {
        this.registrationNumber = registrationNumber;
        this.title = title;
        this.type = type;
    }
}

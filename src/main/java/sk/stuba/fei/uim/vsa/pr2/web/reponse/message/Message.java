package sk.stuba.fei.uim.vsa.pr2.web.reponse.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.ws.rs.core.Response;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private Integer code;
    private String message;
    private Error error;
}

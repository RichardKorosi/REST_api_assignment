package sk.stuba.fei.uim.vsa.pr2.web.reponse.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Error {
    private String type;
    private String trace;
}

package sk.stuba.fei.uim.vsa.pr2.web.reponse.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ThesisResponseDto extends Dto{
    private Long id;
    private String registrationNumber;
    private String title;
    private String description;
    private String department;
    private TeacherAltResponseDto supervisor;
    private StudentAltResponseDto author;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date publishedOn;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deadline;
    private String type;
    private String status;

}

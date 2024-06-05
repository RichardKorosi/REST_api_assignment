package sk.stuba.fei.uim.vsa.pr2.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.student.StudentAltResponse;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.teacher.TeacherAltResponse;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThesisResponse {

    private Long id;
    private String registrationNumber;
    private String title;
    private String description;
    private String department;
    private TeacherAltResponse supervisor;
    private StudentAltResponse author;
    private LocalDate publishedOn;
    private LocalDate deadline;
    private Type type;
    private Status status;

    public ThesisResponse() {
    }

    public ThesisResponse(Long id, String registrationNumber, String title, String department, TeacherAltResponse supervisor, Type type) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.title = title;
        this.department = department;
        this.supervisor = supervisor;
        this.type = type;
    }


    public static enum Status {
        FREE_TO_TAKE,
        IN_PROGRESS,
        SUBMITTED
    }

    public static enum Type {
        BACHELOR,
        MASTER,
        DISSERTATION
    }

}

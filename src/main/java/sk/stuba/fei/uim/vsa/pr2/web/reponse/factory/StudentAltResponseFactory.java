package sk.stuba.fei.uim.vsa.pr2.web.reponse.factory;

import sk.stuba.fei.uim.vsa.pr2.domain.Student;
import sk.stuba.fei.uim.vsa.pr2.domain.Thesis;
import sk.stuba.fei.uim.vsa.pr2.service.ApplicationService;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.StudentAltResponseDto;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.StudentResponseDto;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.ThesisResponseDto;

public class StudentAltResponseFactory implements ResponseFactory<Student, StudentAltResponseDto>{
    @Override
    public StudentAltResponseDto transformToDto(Student entity) {
        StudentAltResponseDto dto = new StudentAltResponseDto();
        ApplicationService applicationService = new ApplicationService();
        Student entityStudent = applicationService.getStudent(entity.getAisId());
        Thesis thesis =  applicationService.getThesisByStudent(entity.getAisId());
        dto.setId(entityStudent.getAisId());
        dto.setAisId(entityStudent.getAisId());
        dto.setName(entityStudent.getName());
        dto.setEmail(entityStudent.getEmail());
        dto.setYear(entityStudent.getYear());
        dto.setTerm(entityStudent.getTerm());
        dto.setProgramme(entity.getStudyProgramme());
        if(thesis != null){
            dto.setThesis(thesis.getId());
        } else{
            dto.setThesis(null);
        }
        return dto;
    }

    @Override
    public Student transformToEntity(StudentAltResponseDto dto) {
        return null;
    }
}

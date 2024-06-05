package sk.stuba.fei.uim.vsa.pr2.web.reponse.factory;

import sk.stuba.fei.uim.vsa.pr2.domain.Student;
import sk.stuba.fei.uim.vsa.pr2.domain.Thesis;
import sk.stuba.fei.uim.vsa.pr2.service.ApplicationService;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.StudentResponseDto;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.ThesisResponseDto;

public class StudentResponseFactory implements ResponseFactory<Student, StudentResponseDto> {
    @Override
    public StudentResponseDto transformToDto(Student entity) {
        StudentResponseDto dto = new StudentResponseDto();
        ApplicationService applicationService = new ApplicationService();
        Student enitityStudent = applicationService.getStudent(entity.getAisId());
        Thesis thesis =  applicationService.getThesisByStudent(entity.getAisId());
        ThesisResponseFactory thesisResponseFactory = new ThesisResponseFactory();
        dto.setId(enitityStudent.getAisId());
        dto.setAisId(enitityStudent.getAisId());
        dto.setName(enitityStudent.getName());
        dto.setEmail(enitityStudent.getEmail());
        dto.setYear(enitityStudent.getYear());
        dto.setTerm(enitityStudent.getTerm());
        dto.setProgramme(enitityStudent.getStudyProgramme());
        if(thesis != null){
            ThesisResponseDto thesisResponseDto = thesisResponseFactory.transformToDto(thesis);
            dto.setThesis(thesisResponseDto);
        }else{
            dto.setThesis(null);
        }
        return dto;
    }

    @Override
    public Student transformToEntity(StudentResponseDto dto) {
        return null;
    }
}

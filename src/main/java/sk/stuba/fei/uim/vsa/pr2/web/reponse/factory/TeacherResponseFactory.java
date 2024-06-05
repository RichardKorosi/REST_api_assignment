package sk.stuba.fei.uim.vsa.pr2.web.reponse.factory;

import sk.stuba.fei.uim.vsa.pr2.domain.Teacher;
import sk.stuba.fei.uim.vsa.pr2.domain.Thesis;
import sk.stuba.fei.uim.vsa.pr2.service.ApplicationService;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.TeacherResponseDto;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.ThesisResponseDto;

import java.util.ArrayList;
import java.util.List;

public class TeacherResponseFactory implements ResponseFactory<Teacher, TeacherResponseDto>{
    @Override
    public TeacherResponseDto transformToDto(Teacher entity) {
        TeacherResponseDto dto = new TeacherResponseDto();
        ApplicationService applicationService = new ApplicationService();
        Teacher entityTeacher = applicationService.getTeacher(entity.getAisId());
        ThesisResponseFactory thesisResponseFactory = new ThesisResponseFactory();
        List<Thesis> theses;
        List<ThesisResponseDto> thesisResponseDtos = new ArrayList<>();
        theses = applicationService.getThesesByTeacher(entity.getAisId());
        dto.setId(entityTeacher.getAisId());
        dto.setAisId(entityTeacher.getAisId());
        dto.setName(entityTeacher.getName());
        dto.setEmail(entityTeacher.getEmail());
        dto.setInstitute(entityTeacher.getDepartment());
        dto.setDepartment(entityTeacher.getDepartment());
        for (Thesis thesis : theses){
            thesisResponseDtos.add(thesisResponseFactory.transformToDto(thesis));
        }
        dto.setTheses(thesisResponseDtos);
        return dto;
    }

    @Override
    public Teacher transformToEntity(TeacherResponseDto dto) {
        return null;
    }
}

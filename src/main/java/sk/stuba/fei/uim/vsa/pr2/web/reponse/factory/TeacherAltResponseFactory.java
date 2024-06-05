package sk.stuba.fei.uim.vsa.pr2.web.reponse.factory;

import sk.stuba.fei.uim.vsa.pr2.domain.Teacher;
import sk.stuba.fei.uim.vsa.pr2.domain.Thesis;
import sk.stuba.fei.uim.vsa.pr2.service.ApplicationService;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.TeacherAltResponseDto;

import java.util.ArrayList;
import java.util.List;

public class TeacherAltResponseFactory implements ResponseFactory <Teacher, TeacherAltResponseDto>{
    @Override
    public TeacherAltResponseDto transformToDto(Teacher entity) {
        TeacherAltResponseDto dto = new TeacherAltResponseDto();
        ApplicationService applicationService = new ApplicationService();
        Teacher entityTeacher = applicationService.getTeacher(entity.getAisId());
        List<Thesis> thesisList = applicationService.getThesesByTeacher(entity.getAisId());
        List<Long> idList = new ArrayList<>();
        dto.setId(entityTeacher.getAisId());
        dto.setAisId(entityTeacher.getAisId());
        dto.setName(entityTeacher.getName());
        dto.setEmail(entityTeacher.getEmail());
        dto.setInstitute(entityTeacher.getDepartment());
        dto.setDepartment(entityTeacher.getDepartment());
        for (Thesis thesis : thesisList){
            idList.add(thesis.getId());
        }
        dto.setTheses(idList);
        return dto;

    }

    @Override
    public Teacher transformToEntity(TeacherAltResponseDto dto) {
        return null;
    }
}

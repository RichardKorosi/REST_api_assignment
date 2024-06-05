package sk.stuba.fei.uim.vsa.pr2.web.reponse.factory;

import sk.stuba.fei.uim.vsa.pr2.domain.Thesis;
import sk.stuba.fei.uim.vsa.pr2.service.ApplicationService;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.ThesisResponseDto;

public class ThesisResponseFactory implements ResponseFactory <Thesis, ThesisResponseDto> {
    @Override
    public ThesisResponseDto transformToDto(Thesis entity) {
        ThesisResponseDto dto = new ThesisResponseDto();
        ApplicationService applicationService = new ApplicationService();
        Thesis entityThesis = applicationService.getThesis(entity.getId());
        TeacherAltResponseFactory teacherAltResponseFactory = new TeacherAltResponseFactory();
        StudentAltResponseFactory studentAltResponseFactory = new StudentAltResponseFactory();
        dto.setId(entityThesis.getId());
        dto.setRegistrationNumber(entityThesis.getRegistrationNumber());
        dto.setTitle(entityThesis.getTitle());
        dto.setDescription(entityThesis.getDescription());
        dto.setDepartment(entityThesis.getDepartment());
        dto.setSupervisor(teacherAltResponseFactory.transformToDto(entityThesis.getSupervisor()));
        if(entityThesis.getAuthor() != null)
            dto.setAuthor(studentAltResponseFactory.transformToDto(entityThesis.getAuthor()));
        else
            dto.setAuthor(null);
        dto.setPublishedOn(entityThesis.getPublishedOn());
        dto.setDeadline(entityThesis.getDeadline());
        dto.setType(entityThesis.getType().name());
        dto.setStatus(entityThesis.getStatus().name());
        return dto;
    }

    @Override
    public Thesis transformToEntity(ThesisResponseDto dto) {
        return null;
    }
}

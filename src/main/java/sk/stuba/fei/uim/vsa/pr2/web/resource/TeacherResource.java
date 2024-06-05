package sk.stuba.fei.uim.vsa.pr2.web.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;
import sk.stuba.fei.uim.vsa.pr2.auth.Permission;
import sk.stuba.fei.uim.vsa.pr2.auth.Secured;
import sk.stuba.fei.uim.vsa.pr2.domain.Teacher;
import sk.stuba.fei.uim.vsa.pr2.service.ApplicationService;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.TeacherResponseDto;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.factory.TeacherResponseFactory;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.message.Error;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.message.Message;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
@Path("/teachers")
public class TeacherResource {

    @Context
    UriInfo uriInfo;
    private final ApplicationService appService = new ApplicationService();
    private final ObjectMapper json = new ObjectMapper();
    private final TeacherResponseFactory factory = new TeacherResponseFactory();
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body) {
        try {
            Teacher teacher = json.readValue(body, Teacher.class);
            teacher = appService.createTeacher(teacher.getAisId(), teacher.getName(),
                    teacher.getEmail(), teacher.getPassword(), teacher.getDepartment());
            TeacherResponseDto teacherResponseDto = factory.transformToDto(teacher);
            return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(teacherResponseDto)).build();
        } catch (Exception e) {
            try {
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(json.writeValueAsString(new Message(500, e.getMessage(), new Error(e.getClass().getName(), Arrays.toString(e.getStackTrace())))))
                        .build();
            } catch(Exception ex){
                return null;
            }
        }
    }

    @GET
    @Secured({Permission.PERM_STUDENT, Permission.PERM_TEACHER})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context SecurityContext securityContext) {
        try {
            List<Teacher> teachers = appService.getTeachers();
            List<TeacherResponseDto> teacherResponseDtos = teachers.stream().map(factory::transformToDto).collect(Collectors.toList());
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(teacherResponseDtos)).build();
        } catch (Exception e) {
            try {
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(json.writeValueAsString(new Message(500, e.getMessage(), new Error(e.getClass().getName(), Arrays.toString(e.getStackTrace())))))
                        .build();
            } catch(Exception ex){
                return null;
            }
        }
    }

    @GET
    @Secured({Permission.PERM_STUDENT, Permission.PERM_TEACHER})
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        try {
            Teacher teacher = appService.getTeacher(id);
            if(teacher == null){
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(json.writeValueAsString(new Message(404, "Object not Found", new Error("ErrorType","ErrorTrace"))))
                        .build();
            }
            TeacherResponseDto teacherResponseDto = factory.transformToDto(teacher);
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(teacherResponseDto)).build();
        } catch (Exception e) {
            try {
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(json.writeValueAsString(new Message(500, e.getMessage(), new Error(e.getClass().getName(), Arrays.toString(e.getStackTrace())))))
                        .build();
            } catch(Exception ex){
                return null;
            }
        }
    }

    @DELETE
    @Secured({Permission.PERM_DELETE_TEACHER})
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        try {
            Teacher teacher = appService.getTeacher(id);
            if(teacher == null){
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(json.writeValueAsString(new Message(404, "Object not Found", new Error("ErrorType","ErrorTrace"))))
                        .build();
            }
            TeacherResponseDto teacherResponseDto = factory.transformToDto(teacher);
            teacher = appService.deleteTeacher(id);
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(teacherResponseDto)).build();
        } catch (Exception e) {
            try {
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(json.writeValueAsString(new Message(500, e.getMessage(), new Error(e.getClass().getName(), Arrays.toString(e.getStackTrace())))))
                        .build();
            } catch(Exception ex){
                return null;
            }
        }
    }
}

package sk.stuba.fei.uim.vsa.pr2.web.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;
import sk.stuba.fei.uim.vsa.pr2.auth.Permission;
import sk.stuba.fei.uim.vsa.pr2.auth.Secured;
import sk.stuba.fei.uim.vsa.pr2.domain.Student;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.StudentResponseDto;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.factory.StudentResponseFactory;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.message.Error;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.message.Message;
import sk.stuba.fei.uim.vsa.pr2.service.ApplicationService;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/students")
public class StudentResource {
    private final ApplicationService appService = new ApplicationService();
    private final ObjectMapper json = new ObjectMapper();
    private final StudentResponseFactory factory = new StudentResponseFactory();

    @Context
    UriInfo uriInfo;
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body) {
        try {
            Student student = json.readValue(body, Student.class);
            student = appService.createStudent(student.getAisId(), student.getName(),
                    student.getEmail(), student.getPassword(),
                    student.getYear(), student.getTerm(), student.getStudyProgramme());
            StudentResponseDto studentResponseDto = factory.transformToDto(student);
            return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(studentResponseDto)).build();
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
            List<Student> students = appService.getStudents();
            List<StudentResponseDto> studentResponseDtos = students.stream().map(factory::transformToDto).collect(Collectors.toList());
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(studentResponseDtos)).build();
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
            Student student = appService.getStudent(id);
            if(student == null){
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(json.writeValueAsString(new Message(404, "Object not Found", new Error("ErrorType","ErrorTrace"))))
                        .build();
            }
            StudentResponseDto studentResponseDto = factory.transformToDto(student);
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(studentResponseDto)).build();
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
    @Secured({Permission.PERM_DELETE_STUDENT, Permission.PERM_TEACHER})
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        try {
            Student student = appService.getStudent(id);
            if (student == null)
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(json.writeValueAsString(new Message(404, "Object not Found", new Error("ErrorType", "ErrorTrace"))))
                        .build();
            StudentResponseDto studentResponseDto = factory.transformToDto(student);
            student = appService.deleteStudent(id);
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(studentResponseDto)).build();
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

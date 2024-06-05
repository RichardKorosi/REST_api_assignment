package sk.stuba.fei.uim.vsa.pr2.web.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;
import sk.stuba.fei.uim.vsa.pr2.auth.Permission;
import sk.stuba.fei.uim.vsa.pr2.auth.Secured;
import sk.stuba.fei.uim.vsa.pr2.domain.Student;
import sk.stuba.fei.uim.vsa.pr2.domain.Teacher;
import sk.stuba.fei.uim.vsa.pr2.domain.Thesis;
import sk.stuba.fei.uim.vsa.pr2.domain.Thesis;
import sk.stuba.fei.uim.vsa.pr2.service.ApplicationService;
import sk.stuba.fei.uim.vsa.pr2.user.User;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.*;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.ThesisResponseDto;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.factory.ThesisResponseFactory;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.factory.ThesisResponseFactory;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.message.Error;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.message.Message;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/")
public class ThesisResource {
    private final ApplicationService appService = new ApplicationService();
    private final ObjectMapper json = new ObjectMapper();
    private final ThesisResponseFactory factory = new ThesisResponseFactory();

    @Context
    UriInfo uriInfo;
    @Context
    SecurityContext securityContext;

    @POST
    @Path("theses/")
    @Secured({Permission.PERM_TEACHER})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body) {
        try {
            User user = (User) securityContext.getUserPrincipal();
            Thesis thesis = json.readValue(body, Thesis.class);
            thesis = appService.makeThesisAssignment(user.getId(), thesis.getTitle(), thesis.getType().name(), thesis.getDescription(), thesis.getRegistrationNumber());
            ThesisResponseDto thesisResponseDto = factory.transformToDto(thesis);
            return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(thesisResponseDto)).build();
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
    @Path("theses/")
    @Secured({Permission.PERM_STUDENT, Permission.PERM_TEACHER})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context SecurityContext securityContext) {
        try {
            List<Thesis> theses = appService.getTheses();
            List<ThesisResponseDto> thesisResponseDtos = theses.stream().map(factory::transformToDto).collect(Collectors.toList());
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(thesisResponseDtos)).build();
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
    @Path("theses/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        try {
            Thesis Thesis = appService.getThesis(id);
            if (Thesis == null) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(json.writeValueAsString(new Message(404, "Object not Found", new Error("ErrorType", "ErrorTrace"))))
                        .build();
            }
            ThesisResponseDto thesisResponseDto = factory.transformToDto(Thesis);
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(thesisResponseDto)).build();
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
    @Secured({Permission.PERM_DELETE_THESIS})
    @Path("theses/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        try {
            Long idd = id;
            Thesis thesis = appService.getThesis(id);
            if(thesis == null){
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(json.writeValueAsString(new Message(404, "Object not Found", new Error("ErrorType","ErrorTrace"))))
                        .build();
            }
            ThesisResponseDto thesisResponseDto = factory.transformToDto(thesis);
            thesis = appService.deleteThesis(id);
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(thesisResponseDto)).build();
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

    @POST
    @Secured({Permission.PERM_STUDENT_ASSIGN, Permission.PERM_TEACHER})
    @Path("theses/{id}/assign")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assign(@PathParam("id") Long id, String body, @Context SecurityContext securityContext) {
        try {
            User user = (User) securityContext.getUserPrincipal();
            Thesis thesis = appService.getThesis(id);
            if(thesis == null){
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(json.writeValueAsString(new Message(404, "Object not Found", new Error("ErrorType","ErrorTrace"))))
                        .build();
            }
            if (user.getPermissions().contains(Permission.PERM_STUDENT_ASSIGN)) {
                appService.assignThesis(thesis.getId(), user.getId());
                ThesisResponseDto thesisResponseDto = factory.transformToDto(thesis);
                return Response.status(Response.Status.OK).entity(json.writeValueAsString(thesisResponseDto)).build();
            }
            else {
                ThesisRequestDto thesisRequestDto = json.readValue(body, ThesisRequestDto.class);
                Long studentId = thesisRequestDto.getStudentId();
                appService.assignThesis(thesis.getId(), studentId);
                ThesisResponseDto thesisResponseDto = factory.transformToDto(thesis);
                return Response.status(Response.Status.OK).entity(json.writeValueAsString(thesisResponseDto)).build();
            }
            }catch (Exception e) {
                try {
                    return Response
                            .status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(json.writeValueAsString(new Message(500, e.getMessage(), new Error(e.getClass().getName(), Arrays.toString(e.getStackTrace())))))
                            .build();
                } catch (Exception ex) {
                    return null;
                }
        }
    }

    @POST
    @Secured({Permission.PERM_STUDENT_SUBMIT, Permission.PERM_TEACHER})
    @Path("theses/{id}/submit")
    @Produces(MediaType.APPLICATION_JSON)
    public Response submit(@PathParam("id") Long id, String body, @Context SecurityContext securityContext) {
        try {
            User user = (User) securityContext.getUserPrincipal();
            Thesis thesis = appService.getThesis(id);
            //403 pre teachera
            if (user.getPermissions().contains(Permission.PERM_TEACHER)) {
                ThesisRequestDto thesisRequestDto = json.readValue(body, ThesisRequestDto.class);
                Long studentId = thesisRequestDto.getStudentId();
                if (!studentId.equals(thesis.getAuthor().getAisId())) {
                    return Response
                            .status(Response.Status.FORBIDDEN)
                            .entity(json.writeValueAsString(new Message(403, "You don't have permission!", new Error("ErrorType", "ErrorTrace"))))
                            .build();
                }
            }
            //404
            if (thesis == null){
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(json.writeValueAsString(new Message(404, "Searched object not found!", new Error("Client Error", "Not Available"))))
                        .build();
            }
            //SAMOTNE SPRACOVANIE
            appService.submitThesis(thesis.getId());
            ThesisResponseDto thesisResponseDto = factory.transformToDto(thesis);
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(thesisResponseDto)).build();
        } catch (Exception e) {
            try {
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(json.writeValueAsString(new Message(500, e.getMessage(), new Error(e.getClass().getName(), Arrays.toString(e.getStackTrace())))))
                        .build();
            } catch (Exception ex) {
                return null;
            }
        }
    }

    @POST
    @Secured({Permission.PERM_STUDENT, Permission.PERM_TEACHER})
    @Path("search/theses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search( String body, @Context SecurityContext securityContext) {
        try{
            ThesisRequestDto thesisRequestDto;
            ThesisRequestDtoTeacher thesisRequestDtoTeacher;
            Long objectId;
            List<Thesis> thesisList = new ArrayList<>();
            try{
                thesisRequestDto = json.readValue(body, ThesisRequestDto.class);
            } catch (Exception ex1){
                thesisRequestDto = null;
            }
            try{
                thesisRequestDtoTeacher = json.readValue(body, ThesisRequestDtoTeacher.class);
            } catch (Exception ex2){
                thesisRequestDtoTeacher = null;
            }
            if(thesisRequestDto == null && thesisRequestDtoTeacher == null){
                try {
                    return Response
                            .status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(json.writeValueAsString(new Message(500, "Can not recognize JSON input!", new Error("Server Error", "No trace"))))
                            .build();
                } catch (Exception ex) {
                    return null;
                }
            }
            if(thesisRequestDto != null){
                objectId = thesisRequestDto.getStudentId();
                if (objectId != null && appService.getThesisByStudent(objectId) != null)
                    thesisList.add(appService.getThesisByStudent(objectId));
            } else if (thesisRequestDtoTeacher != null) {
                objectId = thesisRequestDtoTeacher.getTeacherId();
                if (objectId != null)
                    thesisList = appService.getThesesByTeacher(objectId);
            }
            List<ThesisResponseDto> thesisResponseDtos = thesisList.stream().map(factory::transformToDto).collect(Collectors.toList());
            return Response
                    .status(Response.Status.OK)
                    .entity(json.writeValueAsString(thesisResponseDtos))
                    .build();
        }catch (Exception e) {
            try {
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(json.writeValueAsString(new Message(500, e.getMessage(), new Error(e.getClass().getName(), Arrays.toString(e.getStackTrace())))))
                        .build();
            } catch (Exception ex) {
                return null;
            }
        }
    }

}

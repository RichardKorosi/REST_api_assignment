package sk.stuba.fei.uim.vsa.pr2.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;
import sk.stuba.fei.uim.vsa.pr2.domain.Student;
import sk.stuba.fei.uim.vsa.pr2.domain.Teacher;
import sk.stuba.fei.uim.vsa.pr2.service.ApplicationService;
import sk.stuba.fei.uim.vsa.pr2.user.User;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.message.Error;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.message.Message;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private final ObjectMapper json = new ObjectMapper();

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        ApplicationService appService = new ApplicationService();
        String authHeader = request.getHeaderString(HttpHeaders.AUTHORIZATION);
        if(authHeader == null || !authHeader.contains("Basic")){
            reject(request);
            return;
        }
        String[] credentials = extractFromAuthHeader(authHeader);
        Optional<Teacher> teacher = appService.getTeacherByEmail(credentials[0]);
        Optional<Student> student = appService.getStudentByEmail(credentials[0]);
        User user = new User();
        if(teacher.isPresent()){
            user.setId(teacher.get().getAisId());
            user.setUsername(teacher.get().getEmail());
            user.setPassword(teacher.get().getPassword());
            user.addPermission(Permission.PERM_TEACHER);
        } else if (student.isPresent()) {
            user.setId(student.get().getAisId());
            user.setUsername(student.get().getEmail());
            user.setPassword(student.get().getPassword());
            user.addPermission(Permission.PERM_STUDENT);
        } else {
            reject(request);
            return;
        }
        if(!BCryptService.verify(credentials[1], user.getPassword())){
            reject(request);
            return;
        }
        final SecurityContext securityContext = request.getSecurityContext();
        SecurityBasicContext context = new SecurityBasicContext(user);
        context.setSecure(securityContext.isSecure());
        request.setSecurityContext(context);

    }

    private void reject(ContainerRequestContext requestContext){
        try {
            requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(json.writeValueAsString(new Message(401, "You are not authorized!", new Error("Client Error","Not Available"))))
                    .build());
        }catch (Exception e){
            return;
        }
    }

    private String[] extractFromAuthHeader(String authHeader){
        return new String(Base64.getDecoder()
                .decode(authHeader
                        .replace("Basic","")
                        .trim()))
                .split(":");
    }
}

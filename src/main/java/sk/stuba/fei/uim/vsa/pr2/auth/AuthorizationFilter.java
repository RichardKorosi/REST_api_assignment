package sk.stuba.fei.uim.vsa.pr2.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import sk.stuba.fei.uim.vsa.pr2.domain.Thesis;
import sk.stuba.fei.uim.vsa.pr2.service.ApplicationService;
import sk.stuba.fei.uim.vsa.pr2.user.User;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.ThesisRequestDto;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.ThesisResponseDto;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.message.Error;
import sk.stuba.fei.uim.vsa.pr2.web.reponse.message.Message;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {
    @Context
    private ResourceInfo resourceInfo;
    @Context
    private UriInfo uriInfo;

    private final ObjectMapper json = new ObjectMapper();
    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        User user = (User) request.getSecurityContext().getUserPrincipal();
        //GETING INFO FROM URI
        //ID:
        Long uriId;
        try {
            uriId = Long.parseLong(uriInfo.getPathParameters().getFirst("id"));
        } catch (NumberFormatException e) {
            uriId = null;
        }
        //PATH:
        List<PathSegment> pathSegments = uriInfo.getPathSegments();
        String segmentPath = pathSegments.get(0).getPath();

        if (uriId != null) {
            if (segmentPath.equals("students")) {
                //DELETE STUDENT (by that student)
                if (user.getId().equals(uriId) && user.getPermissions().contains(Permission.PERM_STUDENT)) {
                    user.addPermission(Permission.PERM_DELETE_STUDENT);
                }
            }

            if (segmentPath.equals("teachers")) {
                //DELETE TEACHER (by that teacher only)
                if (user.getId().equals(uriId) && user.getPermissions().contains(Permission.PERM_TEACHER)) {
                    user.addPermission(Permission.PERM_DELETE_TEACHER);
                }
            }

            if (segmentPath.equals("theses")) {
                ApplicationService applicationService = new ApplicationService();
                Thesis thesis = applicationService.getThesis(uriId);
                if (thesis == null){
                    notFound(request);
                    return;
                }
                //DELETE THESIS (by that teacher only)
                if (user.getId().equals(thesis.getSupervisor().getAisId()) && user.getPermissions().contains(Permission.PERM_TEACHER)) {
                    user.addPermission(Permission.PERM_DELETE_THESIS);
                }
                //ASSIGN THESIS FOR STUDENT
                if (user.getPermissions().contains(Permission.PERM_STUDENT)){
                    user.addPermission(Permission.PERM_STUDENT_ASSIGN);
                }
                //SUBMIT THESIS FOR STUDENT
                if(thesis.getAuthor() != null){
                    if (user.getPermissions().contains(Permission.PERM_STUDENT) && thesis.getAuthor().getAisId().equals(user.getId())){
                        user.addPermission(Permission.PERM_STUDENT_SUBMIT);
                    }
                }
            }
        }
        //CHECKING PERMISSIONS
        Method resourceMethod = resourceInfo.getResourceMethod();
        Set<Permission> permissions = extractPermissionsFromMethod(resourceMethod);
        if (user.getPermissions().stream().noneMatch(permissions::contains)) {
            reject(request);
            return;
        }
    }

    private Set<Permission> extractPermissionsFromMethod(Method method){
        if(method == null)
            return new HashSet<>();
        Secured secured = method.getAnnotation(Secured.class);
        if(secured == null)
            return new HashSet<>();
        return new HashSet<>(Arrays.asList(secured.value()));
    }

    private void reject(ContainerRequestContext requestContext){
        try {
            requestContext.abortWith(Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(json.writeValueAsString(new Message(403, "You don't have permission!", new Error("Client Error","Not Available"))))
                    .build());
        }catch (Exception e){
            return;
        }
    }

    private void notFound(ContainerRequestContext requestContext){
        try {
            requestContext.abortWith(Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(json.writeValueAsString(new Message(404, "Object not found!", new Error("Client Error","Not Available"))))
                    .build());
        }catch (Exception e){
            return;
        }
    }
}

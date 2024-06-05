package sk.stuba.fei.uim.vsa.pr2;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr2.model.dto.request.CreateStudentRequest;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.student.StudentWithThesisResponse;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.teacher.TeacherWithThesesResponse;
import sk.stuba.fei.uim.vsa.pr2.utils.ResourceTest;
import sk.stuba.fei.uim.vsa.pr2.utils.TestData;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static sk.stuba.fei.uim.vsa.pr2.TeacherResourceTest.createTeacher;
import static sk.stuba.fei.uim.vsa.pr2.utils.TestData.ARRAY_CONTENT_LENGTH;
import static sk.stuba.fei.uim.vsa.pr2.utils.TestData.OBJECT_CONTENT_LENGTH;

@Slf4j
public class StudentResourceTest extends ResourceTest {

    public static final String STUDENTS_PATH = "students";

    @Test
    public void shouldCreateStudent() {
        try (Response response = createStudent(TestData.S01)) {
            response.bufferEntity();
            log.info("Received response " + response);
            String json = response.readEntity(String.class);
            assertFalse(json.contains("password"));
            StudentWithThesisResponse body = readObject(json, StudentWithThesisResponse.class);
            assertNotNull(body);
            assertEquals(TestData.S01.aisId, body.getAisId());
            assertEquals(TestData.S01.email, body.getEmail());
            assertEquals(TestData.S01.name, body.getName());
            assertTrue(body.getThesis() == null || body.getThesis().getId() == null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldCallWithoutBodyAndReturnError() {
        try (Response response = request(STUDENTS_PATH)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(CreateStudentRequest.builder().build(),
                        MediaType.APPLICATION_JSON))) {
            testErrorMessage(response, Response.Status.INTERNAL_SERVER_ERROR, Response.Status.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldCreateStudentWithOnlyRequiredProperties() {
        try (Response response = createResource(STUDENTS_PATH, () ->
                        CreateStudentRequest.builder()
                                .aisId(TestData.S01.aisId)
                                .name(TestData.S01.name)
                                .email(TestData.S01.email)
                                .password(Base64.getEncoder().encodeToString(TestData.S01.password.getBytes()))
                                .build(),
                null)) {
            response.bufferEntity();
            log.info("Received response " + response);
            String json = response.readEntity(String.class);
            assertFalse(json.contains("password"));
            StudentWithThesisResponse body = readObject(json, StudentWithThesisResponse.class);
            assertNotNull(body);
            assertEquals(TestData.S01.aisId, body.getAisId());
            assertEquals(TestData.S01.email, body.getEmail());
            assertEquals(TestData.S01.name, body.getName());
            assertTrue(body.getThesis() == null || body.getThesis().getId() == null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldGetAllStudents() {
        try {
            Long s01Id = getIdFromEntity(createStudent(TestData.S01), StudentWithThesisResponse.class);
            Long s02Id = getIdFromEntity(createStudent(TestData.S02), StudentWithThesisResponse.class);

            Response response = request(STUDENTS_PATH, TestData.S01).get();
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > ARRAY_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            List<StudentWithThesisResponse> body = readObject(response, new TypeReference<List<StudentWithThesisResponse>>() {
            });
            assertNotNull(body);
            assertEquals(2, body.size());
            assertTrue(body.stream().anyMatch(s -> Objects.equals(s.getId(), s01Id)));
            assertTrue(body.stream().anyMatch(s -> Objects.equals(s.getId(), s02Id)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldReturnUnauthorized() {
        try (Response response = request(STUDENTS_PATH).get()) {
            testErrorMessage(response, Response.Status.UNAUTHORIZED);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldGetEmptyList() {
        try {
            Long t01Id = getIdFromEntity(createTeacher(TestData.T01), TeacherWithThesesResponse.class);
            Response response = request(STUDENTS_PATH, TestData.T01).get();
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            List<StudentWithThesisResponse> body = readObject(response, new TypeReference<List<StudentWithThesisResponse>>() {
            });
            assertNotNull(body);
            assertTrue(body.isEmpty());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldGetStudent() {
        Long s01Id = getIdFromEntity(createStudent(TestData.S01),StudentWithThesisResponse.class);
        try (Response response = request(STUDENTS_PATH + "/" + s01Id, TestData.S01).get()) {
            response.bufferEntity();
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            String json = response.readEntity(String.class);
            assertFalse(json.contains("password"));
            StudentWithThesisResponse body = readObject(json, StudentWithThesisResponse.class);
            assertNotNull(body);
            assertEquals(TestData.S01.aisId, body.getAisId());
            assertEquals(TestData.S01.email, body.getEmail());
            assertEquals(TestData.S01.name, body.getName());
            assertTrue(body.getThesis() == null || body.getThesis().getId() == null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldGetNotFoundForNonExistingStudent() {
        Long s01Id = getIdFromEntity(createStudent(TestData.S01),StudentWithThesisResponse.class);
        try (Response response = request(STUDENTS_PATH + "/99", TestData.S01).get()) {
            testErrorMessage(response, Response.Status.NOT_FOUND);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldDeleteStudent() {
        try {
            Long s01Id = getIdFromEntity(createStudent(TestData.S01),StudentWithThesisResponse.class);
            String path = STUDENTS_PATH + "/" + s01Id;

            Response response = request(path, TestData.S01).delete();
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            StudentWithThesisResponse body = readObject(response, StudentWithThesisResponse.class);
            assertNotNull(body);
            assertEquals(TestData.S01.aisId, body.getAisId());
            assertEquals(TestData.S01.email, body.getEmail());
            assertEquals(TestData.S01.name, body.getName());
            assertTrue(body.getThesis() == null || body.getThesis().getId() == null);

            Response nonExisting = request(path, TestData.S01).get();
            assertNotNull(nonExisting);
            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), nonExisting.getStatus());
            nonExisting.close();

            Long s02Id = getIdFromEntity(createStudent(TestData.S02), StudentWithThesisResponse.class);
            Response deleteCheckResponse = request(path, TestData.S02).get();
            testErrorMessage(deleteCheckResponse, Response.Status.NOT_FOUND);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldReturnForbiddenToDeleteWithWrongStudent() {
        try {
            Long s01Id = getIdFromEntity(createStudent(TestData.S01), StudentWithThesisResponse.class);
            Long s02Id = getIdFromEntity(createStudent(TestData.S02), StudentWithThesisResponse.class);

            Response response = request(STUDENTS_PATH + "/" + s01Id, TestData.S02).delete();
            testErrorMessage(response, Response.Status.FORBIDDEN);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    public static Response createStudent(TestData.Student student) {
        return createResource(STUDENTS_PATH, () ->
                        CreateStudentRequest.builder()
                                .aisId(student.aisId)
                                .name(student.name)
                                .email(student.email)
                                .password(Base64.getEncoder().encodeToString(student.password.getBytes()))
                                .year(student.year)
                                .term(student.term)
                                .programme(student.programme).build(),
                null);
    }

}

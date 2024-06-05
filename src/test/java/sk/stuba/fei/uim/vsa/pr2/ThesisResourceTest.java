package sk.stuba.fei.uim.vsa.pr2;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr2.model.dto.request.CreateThesisRequest;
import sk.stuba.fei.uim.vsa.pr2.model.dto.request.StudentIdRequest;
import sk.stuba.fei.uim.vsa.pr2.model.dto.request.UserIdRequest;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.ThesisResponse;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.student.StudentWithThesisResponse;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.teacher.TeacherWithThesesResponse;
import sk.stuba.fei.uim.vsa.pr2.utils.ResourceTest;
import sk.stuba.fei.uim.vsa.pr2.utils.TestData;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static sk.stuba.fei.uim.vsa.pr2.StudentResourceTest.createStudent;
import static sk.stuba.fei.uim.vsa.pr2.TeacherResourceTest.createTeacher;
import static sk.stuba.fei.uim.vsa.pr2.utils.TestData.*;

@Slf4j
public class ThesisResourceTest extends ResourceTest {

    public static final String THESIS_PATH = "theses";

    @Test
    public void shouldCreateThesis() {
        try (Response response = createThesis(TestData.TH01, TestData.T01, true)) {
            assertNotNull(response);
            response.bufferEntity();
            log.info("Received response " + response);
            ThesisResponse body = readObject(response, ThesisResponse.class);
            assertNotNull(body);
            assertEquals(TestData.TH01.registrationNumber, body.getRegistrationNumber());
            assertEquals(TestData.TH01.title, body.getTitle());
            assertEquals(TestData.TH01.type, body.getType().toString());
            assertEquals(TestData.TH01.description, body.getDescription());
            assertNotNull(body.getId());
            assertNotNull(body.getStatus());
            assertEquals(ThesisResponse.Status.FREE_TO_TAKE, body.getStatus());
            assertNotNull(body.getPublishedOn());
            assertTrue(body.getPublishedOn().isEqual(LocalDate.now()));
            assertNotNull(body.getDeadline());
            assertTrue(body.getDeadline().isAfter(LocalDate.now()));
            assertNotNull(body.getSupervisor());
            assertEquals(TestData.T01.aisId, body.getSupervisor().getAisId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldCreateThesisWithOnlyRequiredProperties() {
        try (Response teacherResponse = createTeacher(TestData.T01)) {
            assertNotNull(teacherResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
        try (Response response = createResource(THESIS_PATH, () ->
                        CreateThesisRequest.builder()
                                .registrationNumber(TestData.TH01.registrationNumber)
                                .title(TH01.title)
                                .type(TH01.type)
                                .build(),
                TestData.T01)) {
            assertNotNull(response);
            response.bufferEntity();
            log.info("Received response " + response);
            ThesisResponse body = readObject(response, ThesisResponse.class);
            assertNotNull(body);
            assertEquals(TestData.TH01.registrationNumber, body.getRegistrationNumber());
            assertEquals(TestData.TH01.title, body.getTitle());
            assertEquals(TestData.TH01.type, body.getType().toString());
            assertNull(body.getDescription());
            assertNotNull(body.getId());
            assertNotNull(body.getStatus());
            assertEquals(ThesisResponse.Status.FREE_TO_TAKE, body.getStatus());
            assertNotNull(body.getPublishedOn());
            assertTrue(body.getPublishedOn().isEqual(LocalDate.now()));
            assertNotNull(body.getDeadline());
            assertTrue(body.getDeadline().isAfter(LocalDate.now()));
            assertNotNull(body.getSupervisor());
            assertEquals(TestData.T01.aisId, body.getSupervisor().getAisId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldCallWithoutBodyAndReturnError() {
        try (Response teacherResponse = createTeacher(TestData.T01)) {
            assertNotNull(teacherResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }

        try (Response response = request(THESIS_PATH, T01)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(CreateThesisRequest.builder().build(),
                        MediaType.APPLICATION_JSON))) {
            testErrorMessage(response, Response.Status.INTERNAL_SERVER_ERROR, Response.Status.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldCallCreateThesisWithLoggedStudent() {
        Long s01Id = getIdFromEntity(createStudent(TestData.S01), StudentWithThesisResponse.class);
        try (Response response = request(THESIS_PATH, S01)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(CreateThesisRequest.builder()
                                .registrationNumber(TestData.TH01.registrationNumber)
                                .title(TH01.title)
                                .type(TH01.type)
                                .build(),
                        MediaType.APPLICATION_JSON))) {
            testErrorMessage(response, Response.Status.FORBIDDEN);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldGetAllTheses() {
        try {
            Long th01Id = getIdFromEntity(createThesis(TestData.TH01, TestData.T01, true), ThesisResponse.class);
            assertNotNull(th01Id);
            Long th02Id = getIdFromEntity(createThesis(TestData.TH02, TestData.T01, false), ThesisResponse.class);
            assertNotNull(th02Id);

            Response response = request(THESIS_PATH, TestData.T01).get();
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > ARRAY_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            List<ThesisResponse> body = readObject(response, new TypeReference<List<ThesisResponse>>() {
            });
            assertNotNull(body);
            assertEquals(2, body.size());
            assertTrue(body.stream().anyMatch(th -> Objects.equals(th.getId(), th01Id)));
            assertTrue(body.stream().anyMatch(th -> Objects.equals(th.getId(), th02Id)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldReturnUnauthorized() {
        try (Response response = request(THESIS_PATH).get()) {
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
            Response response = request(THESIS_PATH, TestData.T01).get();
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            List<ThesisResponse> body = readObject(response, new TypeReference<List<ThesisResponse>>() {
            });
            assertNotNull(body);
            assertTrue(body.isEmpty());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldGetThesis() {
        Long th01Id = getIdFromEntity(createThesis(TestData.TH01, TestData.T01, true), ThesisResponse.class);
        assertNotNull(th01Id);
        try (Response response = request(THESIS_PATH + '/' + th01Id, TestData.T01).get()) {
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            ThesisResponse body = readObject(response, ThesisResponse.class);
            assertNotNull(body);
            assertEquals(TestData.TH01.registrationNumber, body.getRegistrationNumber());
            assertEquals(TestData.TH01.title, body.getTitle());
            assertEquals(TestData.TH01.type, body.getType().toString());
            assertEquals(TestData.TH01.description, body.getDescription());
            assertNotNull(body.getId());
            assertEquals(th01Id, body.getId());
            assertNotNull(body.getStatus());
            assertEquals(ThesisResponse.Status.FREE_TO_TAKE, body.getStatus());
            assertNotNull(body.getPublishedOn());
            assertTrue(body.getPublishedOn().isEqual(LocalDate.now()));
            assertNotNull(body.getDeadline());
            assertTrue(body.getDeadline().isAfter(LocalDate.now()));
            assertNotNull(body.getSupervisor());
            assertEquals(TestData.T01.aisId, body.getSupervisor().getAisId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldGetNotFoundForNonExistingThesis() {
        Long th01Id = getIdFromEntity(createThesis(TestData.TH01, TestData.T01, true), ThesisResponse.class);
        assertNotNull(th01Id);
        try (Response response = request(THESIS_PATH + "/99", TestData.T01).get()) {
            testErrorMessage(response, Response.Status.NOT_FOUND);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldDeleteThesis() {
        Long th01Id = getIdFromEntity(createThesis(TestData.TH01, TestData.T01, true), ThesisResponse.class);
        assertNotNull(th01Id);
        try (Response response = request(THESIS_PATH + '/' + th01Id, TestData.T01).delete()) {
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            ThesisResponse body = readObject(response, ThesisResponse.class);
            assertNotNull(body);
            assertEquals(TestData.TH01.registrationNumber, body.getRegistrationNumber());
            assertNotNull(th01Id);
            assertEquals(th01Id, body.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldCallDeleteThesisWithAnotherTeacher() {
        Long th01Id = getIdFromEntity(createThesis(TestData.TH01, TestData.T01, true), ThesisResponse.class);
        assertNotNull(th01Id);
        Long t02Id = getIdFromEntity(createTeacher(T02), TeacherWithThesesResponse.class);
        try (Response response = request(THESIS_PATH + '/' + th01Id, TestData.T02).delete()) {
            testErrorMessage(response, Response.Status.FORBIDDEN);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldCallDeleteThesisWithStudent() {
        Long th01Id = getIdFromEntity(createThesis(TestData.TH01, TestData.T01, true), ThesisResponse.class);
        assertNotNull(th01Id);
        Long s01Id = getIdFromEntity(createStudent(S01), StudentWithThesisResponse.class);
        try (Response response = request(THESIS_PATH + '/' + th01Id, TestData.S01).delete()) {
            testErrorMessage(response, Response.Status.FORBIDDEN);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldAssignThesisWithTeacher() {
        Long th01Id = getIdFromEntity(createThesis(TestData.TH01, TestData.T01, true), ThesisResponse.class);
        assertNotNull(th01Id);
        Long st01Id = getIdFromEntity(StudentResourceTest.createStudent(TestData.S01), StudentWithThesisResponse.class);
        assertNotNull(st01Id);
        try (Response response = request(THESIS_PATH + '/' + th01Id + "/assign", TestData.T01)
                .post(Entity.entity(new UserIdRequest(st01Id, null), MediaType.APPLICATION_JSON_TYPE))) {
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            ThesisResponse body = readObject(response, ThesisResponse.class);
            assertNotNull(body);
            assertEquals(TestData.TH01.registrationNumber, body.getRegistrationNumber());
            assertEquals(TestData.TH01.title, body.getTitle());
            assertEquals(TestData.TH01.type, body.getType().toString());
            assertEquals(TestData.TH01.description, body.getDescription());
            assertNotNull(body.getId());
            assertEquals(th01Id, body.getId());
            assertNotNull(body.getStatus());
            assertEquals(ThesisResponse.Status.IN_PROGRESS, body.getStatus());
            assertNotNull(body.getPublishedOn());
            assertTrue(body.getPublishedOn().isEqual(LocalDate.now()));
            assertNotNull(body.getDeadline());
            assertTrue(body.getDeadline().isAfter(LocalDate.now()));
            assertNotNull(body.getSupervisor());
            assertEquals(TestData.T01.aisId, body.getSupervisor().getAisId());
            assertNotNull(body.getAuthor());
            assertEquals(st01Id, body.getAuthor().getId());
            assertEquals(TestData.S01.aisId, body.getAuthor().getAisId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldAssignThesisWithStudent() {
        Long th01Id = getIdFromEntity(createThesis(TestData.TH01, TestData.T01, true), ThesisResponse.class);
        assertNotNull(th01Id);
        Long st01Id = getIdFromEntity(StudentResourceTest.createStudent(TestData.S01), StudentWithThesisResponse.class);
        assertNotNull(st01Id);
        Long st02Id = getIdFromEntity(StudentResourceTest.createStudent(TestData.S02), StudentWithThesisResponse.class);
        assertNotNull(st02Id);
        try (Response response = request(THESIS_PATH + '/' + th01Id + "/assign", TestData.S01)
                .post(Entity.entity(new UserIdRequest(st02Id, null), MediaType.APPLICATION_JSON_TYPE))) {
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            ThesisResponse body = readObject(response, ThesisResponse.class);
            assertNotNull(body);
            assertEquals(TestData.TH01.registrationNumber, body.getRegistrationNumber());
            assertEquals(TestData.TH01.title, body.getTitle());
            assertEquals(TestData.TH01.type, body.getType().toString());
            assertEquals(TestData.TH01.description, body.getDescription());
            assertNotNull(body.getId());
            assertEquals(th01Id, body.getId());
            assertNotNull(body.getStatus());
            assertEquals(ThesisResponse.Status.IN_PROGRESS, body.getStatus());
            assertNotNull(body.getPublishedOn());
            assertTrue(body.getPublishedOn().isEqual(LocalDate.now()));
            assertNotNull(body.getDeadline());
            assertTrue(body.getDeadline().isAfter(LocalDate.now()));
            assertNotNull(body.getSupervisor());
            assertEquals(TestData.T01.aisId, body.getSupervisor().getAisId());
            assertNotNull(body.getAuthor());
            assertEquals(st01Id, body.getAuthor().getId());
            assertEquals(TestData.S01.aisId, body.getAuthor().getAisId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldSubmitThesisWithTeacher() {
        Long th01Id = getIdFromEntity(createThesis(TestData.TH01, TestData.T01, true), ThesisResponse.class);
        assertNotNull(th01Id);
        Long st01Id = getIdFromEntity(StudentResourceTest.createStudent(TestData.S01), StudentWithThesisResponse.class);
        assertNotNull(st01Id);
        ThesisResponse assignedThesis = readObject(request(THESIS_PATH + '/' + th01Id + "/assign", TestData.T01)
                .post(Entity.entity(new UserIdRequest(st01Id, null), MediaType.APPLICATION_JSON_TYPE)), ThesisResponse.class);
        assertNotNull(assignedThesis);
        assertEquals(th01Id, assignedThesis.getId());
        assertEquals(ThesisResponse.Status.IN_PROGRESS, assignedThesis.getStatus());
        assertNotNull(assignedThesis.getAuthor());
        assertEquals(st01Id, assignedThesis.getAuthor().getId());
        try (Response response = request(THESIS_PATH + '/' + th01Id + "/submit", TestData.T01)
                .post(Entity.entity(new UserIdRequest(st01Id, null), MediaType.APPLICATION_JSON_TYPE))) {
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            ThesisResponse body = readObject(response, ThesisResponse.class);
            assertNotNull(body);
            assertEquals(TestData.TH01.registrationNumber, body.getRegistrationNumber());
            assertEquals(TestData.TH01.title, body.getTitle());
            assertEquals(TestData.TH01.type, body.getType().toString());
            assertEquals(TestData.TH01.description, body.getDescription());
            assertNotNull(body.getId());
            assertEquals(th01Id, body.getId());
            assertNotNull(body.getStatus());
            assertEquals(ThesisResponse.Status.SUBMITTED, body.getStatus());
            assertNotNull(body.getPublishedOn());
            assertTrue(body.getPublishedOn().isEqual(LocalDate.now()));
            assertNotNull(body.getDeadline());
            assertTrue(body.getDeadline().isAfter(LocalDate.now()));
            assertNotNull(body.getSupervisor());
            assertEquals(TestData.T01.aisId, body.getSupervisor().getAisId());
            assertNotNull(body.getAuthor());
            assertEquals(st01Id, body.getAuthor().getId());
            assertEquals(TestData.S01.aisId, body.getAuthor().getAisId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldSubmitThesisWithStudent() {
        Long th01Id = getIdFromEntity(createThesis(TestData.TH01, TestData.T01, true), ThesisResponse.class);
        assertNotNull(th01Id);
        Long st01Id = getIdFromEntity(StudentResourceTest.createStudent(TestData.S01), StudentWithThesisResponse.class);
        assertNotNull(st01Id);
        Long st02Id = getIdFromEntity(StudentResourceTest.createStudent(TestData.S02), StudentWithThesisResponse.class);
        assertNotNull(st02Id);
        ThesisResponse assignedThesis = readObject(request(THESIS_PATH + '/' + th01Id + "/assign", TestData.T01)
                .post(Entity.entity(new UserIdRequest(st01Id, null), MediaType.APPLICATION_JSON_TYPE)), ThesisResponse.class);
        assertNotNull(assignedThesis);
        assertEquals(th01Id, assignedThesis.getId());
        assertEquals(ThesisResponse.Status.IN_PROGRESS, assignedThesis.getStatus());
        assertNotNull(assignedThesis.getAuthor());
        assertEquals(st01Id, assignedThesis.getAuthor().getId());
        try (Response response = request(THESIS_PATH + '/' + th01Id + "/submit", TestData.S01)
                .post(Entity.entity(new UserIdRequest(st02Id, null), MediaType.APPLICATION_JSON_TYPE))) {
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            ThesisResponse body = readObject(response, ThesisResponse.class);
            assertNotNull(body);
            assertEquals(TestData.TH01.registrationNumber, body.getRegistrationNumber());
            assertEquals(TestData.TH01.title, body.getTitle());
            assertEquals(TestData.TH01.type, body.getType().toString());
            assertEquals(TestData.TH01.description, body.getDescription());
            assertNotNull(body.getId());
            assertEquals(th01Id, body.getId());
            assertNotNull(body.getStatus());
            assertEquals(ThesisResponse.Status.SUBMITTED, body.getStatus());
            assertNotNull(body.getPublishedOn());
            assertTrue(body.getPublishedOn().isEqual(LocalDate.now()));
            assertNotNull(body.getDeadline());
            assertTrue(body.getDeadline().isAfter(LocalDate.now()));
            assertNotNull(body.getSupervisor());
            assertEquals(TestData.T01.aisId, body.getSupervisor().getAisId());
            assertNotNull(body.getAuthor());
            assertEquals(st01Id, body.getAuthor().getId());
            assertEquals(TestData.S01.aisId, body.getAuthor().getAisId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldSubmitThesisWithTeacherAndWithWrongStudent() {
        Long th01Id = getIdFromEntity(createThesis(TestData.TH01, TestData.T01, true), ThesisResponse.class);
        assertNotNull(th01Id);
        Long st01Id = getIdFromEntity(StudentResourceTest.createStudent(TestData.S01), StudentWithThesisResponse.class);
        assertNotNull(st01Id);
        ThesisResponse assignedThesis = readObject(request(THESIS_PATH + '/' + th01Id + "/assign", TestData.T01)
                .post(Entity.entity(new UserIdRequest(st01Id, null), MediaType.APPLICATION_JSON_TYPE)), ThesisResponse.class);
        assertNotNull(assignedThesis);
        assertEquals(th01Id, assignedThesis.getId());
        assertEquals(ThesisResponse.Status.IN_PROGRESS, assignedThesis.getStatus());
        assertNotNull(assignedThesis.getAuthor());
        assertEquals(st01Id, assignedThesis.getAuthor().getId());

        Long st02Id = getIdFromEntity(StudentResourceTest.createStudent(TestData.S02), StudentWithThesisResponse.class);
        assertNotNull(st02Id);

        try (Response response = request(THESIS_PATH + '/' + th01Id + "/submit", TestData.T01)
                .post(Entity.entity(new UserIdRequest(st02Id, null), MediaType.APPLICATION_JSON_TYPE))) {
            testErrorMessage(response, Response.Status.INTERNAL_SERVER_ERROR, Response.Status.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldFindThesisByTeacher() {
        assumeFalse(isBonusImplemented(), "Implementation of bonus endpoint was detected. This test is irrelevant and it's skipped.");

        ThesisResponse thesis = readObject(createThesis(TestData.TH01, TestData.T01, true), ThesisResponse.class);
        assertNotNull(thesis);
        assertNotNull(thesis.getId());
        assertNotNull(thesis.getSupervisor());
        assertNotNull(thesis.getSupervisor().getId());
        try (Response response = request("search/theses", TestData.T01).
                post(Entity.entity(new UserIdRequest(null, thesis.getSupervisor().getId()), MediaType.APPLICATION_JSON_TYPE))) {
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > ARRAY_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            List<ThesisResponse> body = readObject(response, new TypeReference<List<ThesisResponse>>() {
            });
            assertNotNull(body);
            assertEquals(1, body.size());
            assertNotNull(body.get(0));
            ThesisResponse thBody = body.get(0);
            assertEquals(TestData.TH01.registrationNumber, thBody.getRegistrationNumber());
            assertEquals(TestData.TH01.title, thBody.getTitle());
            assertEquals(TestData.TH01.type, thBody.getType().toString());
            assertEquals(TestData.TH01.description, thBody.getDescription());
            assertNotNull(thBody.getId());
            assertEquals(thesis.getId(), thBody.getId());
            assertNotNull(thBody.getStatus());
            assertEquals(ThesisResponse.Status.FREE_TO_TAKE, thBody.getStatus());
            assertNotNull(thBody.getPublishedOn());
            assertTrue(thBody.getPublishedOn().isEqual(LocalDate.now()));
            assertNotNull(thBody.getDeadline());
            assertTrue(thBody.getDeadline().isAfter(LocalDate.now()));
            assertNotNull(thBody.getSupervisor());
            assertEquals(TestData.T01.aisId, thBody.getSupervisor().getAisId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldFindThesisByStudent() {
        assumeFalse(isBonusImplemented(), "Implementation of bonus endpoint was detected. This test is irrelevant and it's skipped.");

        ThesisResponse thesis = readObject(createThesis(TestData.TH01, TestData.T01, true), ThesisResponse.class);
        assertNotNull(thesis);
        assertNotNull(thesis.getId());
        Long st01Id = getIdFromEntity(StudentResourceTest.createStudent(TestData.S01), StudentWithThesisResponse.class);
        assertNotNull(st01Id);
        thesis = readObject(request(THESIS_PATH + '/' + thesis.getId() + "/assign", TestData.S01).post(
                Entity.entity(new StudentIdRequest(st01Id), MediaType.APPLICATION_JSON_TYPE)), ThesisResponse.class);
        assertNotNull(thesis);
        assertNotNull(thesis.getAuthor());
        assertEquals(st01Id, thesis.getAuthor().getId());
        try (Response response = request("search/theses", TestData.T01).
                post(Entity.entity(new UserIdRequest(st01Id, null), MediaType.APPLICATION_JSON_TYPE))) {
            assertNotNull(response);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > ARRAY_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            List<ThesisResponse> body = readObject(response, new TypeReference<List<ThesisResponse>>() {
            });
            assertNotNull(body);
            assertEquals(1, body.size());
            assertNotNull(body.get(0));
            ThesisResponse thBody = body.get(0);
            assertEquals(TestData.TH01.registrationNumber, thBody.getRegistrationNumber());
            assertEquals(TestData.TH01.title, thBody.getTitle());
            assertEquals(TestData.TH01.type, thBody.getType().toString());
            assertEquals(TestData.TH01.description, thBody.getDescription());
            assertNotNull(thBody.getId());
            assertEquals(thesis.getId(), thBody.getId());
            assertNotNull(thBody.getStatus());
            assertEquals(ThesisResponse.Status.IN_PROGRESS, thBody.getStatus());
            assertNotNull(thBody.getPublishedOn());
            assertTrue(thBody.getPublishedOn().isEqual(LocalDate.now()));
            assertNotNull(thBody.getDeadline());
            assertTrue(thBody.getDeadline().isAfter(LocalDate.now()));
            assertNotNull(thBody.getSupervisor());
            assertEquals(TestData.T01.aisId, thBody.getSupervisor().getAisId());
            assertNotNull(thBody.getAuthor());
            assertEquals(st01Id, thBody.getAuthor().getId());
            assertEquals(TestData.S01.aisId, thBody.getAuthor().getAisId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldCallFindThesesWithEmptyCriteria() {
        assumeFalse(isBonusImplemented(), "Implementation of bonus endpoint was detected. This test is irrelevant and it's skipped.");

        ThesisResponse thesis = readObject(createThesis(TestData.TH01, TestData.T01, true), ThesisResponse.class);
        assertNotNull(thesis);
        assertNotNull(thesis.getId());
        try (Response response = request("search/theses", T01)
                .post(Entity.entity(new UserIdRequest(null, null), MediaType.APPLICATION_JSON_TYPE))) {
            assertNotNull(response);
            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                testErrorMessage(response, Response.Status.BAD_REQUEST, Response.Status.INTERNAL_SERVER_ERROR, Response.Status.NOT_FOUND);
            } else {
                assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
                assertTrue(response.getLength() > ARRAY_CONTENT_LENGTH);
                assertTrue(response.hasEntity());
                assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
                List<ThesisResponse> body = readObject(response, new TypeReference<List<ThesisResponse>>() {
                });
                assertNotNull(body);
                assertEquals(1, body.size());
                assertNotNull(body.get(0));
                ThesisResponse thBody = body.get(0);
                assertEquals(TestData.TH01.registrationNumber, thBody.getRegistrationNumber());
                assertEquals(TestData.TH01.title, thBody.getTitle());
                assertEquals(TestData.TH01.type, thBody.getType().toString());
                assertEquals(TestData.TH01.description, thBody.getDescription());
                assertNotNull(thBody.getId());
                assertEquals(thesis.getId(), thBody.getId());
                assertNotNull(thBody.getStatus());
                assertEquals(ThesisResponse.Status.FREE_TO_TAKE, thBody.getStatus());
                assertNotNull(thBody.getPublishedOn());
                assertTrue(thBody.getPublishedOn().isEqual(LocalDate.now()));
                assertNotNull(thBody.getDeadline());
                assertTrue(thBody.getDeadline().isAfter(LocalDate.now()));
                assertNotNull(thBody.getSupervisor());
                assertEquals(TestData.T01.aisId, thBody.getSupervisor().getAisId());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldCallFindThesesWithWrongCriteria() {
        assumeFalse(isBonusImplemented(), "Implementation of bonus endpoint was detected. This test is irrelevant and it's skipped.");

        ThesisResponse thesis = readObject(createThesis(TestData.TH01, TestData.T01, true), ThesisResponse.class);
        assertNotNull(thesis);
        assertNotNull(thesis.getId());
        try (Response response = request("search/theses", T01)
                .post(Entity.entity(new UserIdRequest(1L, 1L), MediaType.APPLICATION_JSON_TYPE))) {
            testErrorMessage(response, Response.Status.INTERNAL_SERVER_ERROR, Response.Status.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }


    public static Response createThesis(TestData.Thesis thesis, TestData.Teacher teacher, boolean createTeacher) {
        if (createTeacher) {
            try (Response response = createTeacher(teacher)) {
                assertNotNull(response);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                fail(e);
                return Response.serverError().build();
            }
        }
        return createResource(THESIS_PATH, () -> CreateThesisRequest.builder()
                        .title(thesis.title)
                        .description(thesis.description)
                        .type(thesis.type)
                        .registrationNumber(thesis.registrationNumber)
                        .build(),
                teacher);
    }


}

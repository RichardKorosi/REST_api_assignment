package sk.stuba.fei.uim.vsa.pr2.bonus;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.vsa.pr2.model.dto.request.CreateStudentRequest;
import sk.stuba.fei.uim.vsa.pr2.model.dto.request.CreateTeacherRequest;
import sk.stuba.fei.uim.vsa.pr2.model.dto.request.CreateThesisRequest;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.ThesisResponse;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.student.StudentWithThesisResponse;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.teacher.TeacherWithThesesResponse;
import sk.stuba.fei.uim.vsa.pr2.utils.ResourceTest;
import sk.stuba.fei.uim.vsa.pr2.utils.TestData;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static sk.stuba.fei.uim.vsa.pr2.utils.TestData.OBJECT_CONTENT_LENGTH;

@Slf4j
public class BonusSearchResourceTest extends ResourceTest {

    public static final String SEARCH_PATH = "/search";
    public static final String SEARCH_THESES_PATH = "/search/theses";
    public static final String SEARCH_TEACHER_PATH = "/search/teachers";
    public static final String SEARCH_STUDENT_PATH = "/search/students";

    private static final Map<String, Object> pageParams = new HashMap<>();

    @Test
    public void shouldFindThesesWithPageOf5() {
        assumeTrue(isBonusImplemented(), "Skipping test. Bonus endpoints were not implemented");

        Long t01Id = getIdFromEntity(createTeacher(TestData.T01), TeacherWithThesesResponse.class);
        Set<Long> thIds = new HashSet<>();
        final int numOfThesis = 3;
        for (int i = 0; i < numOfThesis; i++) {
            try (Response newThesis = createThesis(new TestData.Thesis(
                    "FEI-666" + i,
                    TestData.TH01.title,
                    TestData.TH01.description,
                    i % 2 == 0 ? ThesisResponse.Type.BACHELOR.toString() : ThesisResponse.Type.MASTER.toString()
            ), TestData.T01, false)) {
                assertNotNull(newThesis);
                assertTrue(newThesis.hasEntity());
                ThesisResponse body = readObject(newThesis, ThesisResponse.class);
                assertNotNull(body);
                assertNotNull(body.getId());
                thIds.add(body.getId());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                fail(e);
            }
        }
        assertEquals(numOfThesis, thIds.size());

        pageParams.put("page", 0);
        pageParams.put("size", 1);
        try (Response response = request(SEARCH_THESES_PATH, TestData.T01, pageParams)
                .post(Entity.entity(
                        SearchThesesRequest.builder()
                                .teacherId(t01Id)
                                .type(ThesisResponse.Type.BACHELOR)
                                .status(ThesisResponse.Status.FREE_TO_TAKE)
                                .build(),
                        MediaType.APPLICATION_JSON))) {
            assertNotNull(response);
            assertTrue(response.hasEntity());
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            PagedResponse<ThesisResponse> page = readObject(response, new TypeReference<PagedResponse<ThesisResponse>>() {
            });
            assertNotNull(page);
            assertNotNull(page.getPage());
            assertEquals(0, page.getPage().getNumber());
            assertEquals(1, page.getPage().getSize());
            assertEquals(2, page.getPage().getTotalPages());
            assertEquals(2, page.getPage().getTotalElements());
            assertNotNull(page.getContent());
            assertEquals(1, page.getContent().size());
            assertTrue(page.getContent().stream().allMatch(th -> th.getType().toString().equalsIgnoreCase(TestData.TH01.type)));
            assertTrue(page.getContent().stream().allMatch(th -> th.getStatus() == ThesisResponse.Status.FREE_TO_TAKE));
            assertTrue(page.getContent().stream().allMatch(th -> Objects.nonNull(th.getSupervisor())));
            assertTrue(page.getContent().stream().allMatch(th -> Objects.equals(th.getSupervisor().getAisId(), t01Id)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldFindTeachers() {
        assumeTrue(isBonusImplemented(), "Skipping test. Bonus endpoints were not implemented");

        Long t01Id = getIdFromEntity(createTeacher(TestData.T01), TeacherWithThesesResponse.class);
        final int numTeachers = 4;
        Set<Long> tIds = new HashSet<>();
        for (int i = 0; i < numTeachers; i++) {
            try (Response newTeacher = createTeacher(new TestData.Teacher(
                    Integer.valueOf(i + 1).longValue(),
                    TestData.T01.name + i,
                    "teacher" + i + "@stuba.sk",
                    i % 2 == 0 ? TestData.T01.institute + " generated" : TestData.T02.institute,
                    TestData.T01.department + " generated",
                    TestData.T01.password))) {
                assertNotNull(newTeacher);
                assertTrue(newTeacher.hasEntity());
                TeacherWithThesesResponse body = readObject(newTeacher, TeacherWithThesesResponse.class);
                assertNotNull(body);
                assertNotNull(body.getId());
                tIds.add(body.getId());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                fail(e);
            }
        }
        assertEquals(numTeachers, tIds.size());

        pageParams.put("page", 0);
        pageParams.put("size", 2);
        try (Response response = request(SEARCH_TEACHER_PATH, TestData.T01, pageParams)
                .post(Entity.entity(SearchTeachersRequest.builder()
                                .institute(TestData.T02.institute)
                                .build(),
                        MediaType.APPLICATION_JSON_TYPE))) {
            assertNotNull(response);
            assertTrue(response.hasEntity());
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            PagedResponse<TeacherWithThesesResponse> page = readObject(response, new TypeReference<PagedResponse<TeacherWithThesesResponse>>() {
            });
            assertNotNull(page);
            assertNotNull(page.getPage());
            assertEquals(0, page.getPage().getNumber());
            assertEquals(2, page.getPage().getSize());
            assertEquals(1, page.getPage().getTotalPages());
            assertEquals(2, page.getPage().getTotalElements());
            assertNotNull(page.getContent());
            assertEquals(2, page.getContent().size());
            assertTrue(page.getContent().stream().allMatch(t -> t.getInstitute().equals(TestData.T02.institute)));
            assertTrue(page.getContent().stream().allMatch(t -> t.getAisId() <= numTeachers));
            assertTrue(page.getContent().stream().allMatch(t -> t.getName().contains(TestData.T01.name)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    @Test
    public void shouldFindStudents() {
        assumeTrue(isBonusImplemented(), "Skipping test. Bonus endpoints were not implemented");

        Long s01Id = getIdFromEntity(createStudent(TestData.S01), StudentWithThesisResponse.class);
        final int numOfStudents = 5;
        Set<Long> stIds = new HashSet<>();
        for (int i = 0; i < numOfStudents; i++) {
            try (Response newStudent = createStudent(new TestData.Student(
                    Integer.valueOf(i + 1).longValue(),
                    TestData.S01.name + i,
                    "xstudent" + i + "@stuba.sk",
                    TestData.S01.programme + " generated",
                    i % 2 == 0 ? TestData.S01.year : TestData.S02.year,
                    TestData.S01.term,
                    TestData.S01.password))) {
                assertNotNull(newStudent);
                assertTrue(newStudent.hasEntity());
                StudentWithThesisResponse body = readObject(newStudent, StudentWithThesisResponse.class);
                assertNotNull(body);
                assertNotNull(body.getId());
                stIds.add(body.getId());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                fail(e);
            }
        }
        assertEquals(numOfStudents, stIds.size());

        pageParams.put("page", 1);
        pageParams.put("size", 2);
        try (Response response = request(SEARCH_STUDENT_PATH, TestData.S01, pageParams)
                .post(Entity.entity(SearchStudentsRequest.builder()
                                .year(TestData.S01.year)
                                .build(),
                        MediaType.APPLICATION_JSON_TYPE))) {
            assertNotNull(response);
            assertTrue(response.hasEntity());
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertTrue(response.getLength() > OBJECT_CONTENT_LENGTH);
            assertTrue(response.hasEntity());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            PagedResponse<StudentWithThesisResponse> page = readObject(response, new TypeReference<PagedResponse<StudentWithThesisResponse>>() {
            });
            assertNotNull(page);
            assertNotNull(page.getPage());
            assertEquals(1, page.getPage().getNumber());
            assertEquals(2, page.getPage().getSize());
            assertEquals(2, page.getPage().getTotalPages());
            assertEquals(4, page.getPage().getTotalElements());
            assertNotNull(page.getContent());
            assertEquals(2, page.getContent().size());
            assertTrue(page.getContent().stream().allMatch(st -> st.getYear() == TestData.S01.year));
            assertTrue(page.getContent().stream().allMatch(st -> st.getAisId() <= numOfStudents));
            assertTrue(page.getContent().stream().allMatch(st -> st.getName().contains(TestData.S01.name)));
            assertTrue(page.getContent().stream().allMatch(st -> st.getProgramme().contains(TestData.S01.programme)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e);
        }
    }

    public static Response createStudent(TestData.Student student) {
        return createResource("students", () ->
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

    public static Response createTeacher(TestData.Teacher teacher) {
        return createResource("teachers", () ->
                        CreateTeacherRequest.builder()
                                .aisId(teacher.aisId)
                                .name(teacher.name)
                                .email(teacher.email)
                                .password(Base64.getEncoder().encodeToString(teacher.password.getBytes()))
                                .department(teacher.department)
                                .institute(teacher.institute)
                                .build()
                , null);
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
        return createResource("theses", () -> CreateThesisRequest.builder()
                        .title(thesis.title)
                        .description(thesis.description)
                        .type(thesis.type)
                        .registrationNumber(thesis.registrationNumber)
                        .build(),
                teacher);
    }


}

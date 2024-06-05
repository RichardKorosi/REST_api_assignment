package sk.stuba.fei.uim.vsa.pr2.service;

import sk.stuba.fei.uim.vsa.pr2.BCryptService;
import sk.stuba.fei.uim.vsa.pr2.domain.*;


import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

import static sk.stuba.fei.uim.vsa.pr2.domain.ThesisServiceUtils.asDate;
import static sk.stuba.fei.uim.vsa.pr2.domain.ThesisServiceUtils.asLocalDate;

public class ApplicationService extends AbstractApplicationService<Student, Teacher, Thesis> {

    private final ThesisServiceUtils utils;

    public ApplicationService() {
        super();
        utils = new ThesisServiceUtils(this.emf);
    }

    @Override
    public Student createStudent(Long aisId, String name, String email, String password, Long year, Long term, String programme) {
        log.info("Creating student with AIS ID " + aisId);
        return utils.create(() -> Student.builder()
                .aisId(aisId)
                .name(name)
                .email(email)
                .password(BCryptService.hash(new String(Base64.getDecoder().decode(password))))
                .year(year)
                .term(term)
                .studyProgramme(programme)
                .build());
    }

    @Override
    public Student getStudent(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Provided id must not be null");
        return utils.findOne(id, Student.class);
    }

    @Override
    public Student updateStudent(Student student) {
        if (student == null)
            throw new IllegalArgumentException("Provided student must not be null");
        if (student.getAisId() == null)
            throw new IllegalArgumentException("Provided student.aisId must not be null");
        return utils.update(student);
    }

    @Override
    public List<Student> getStudents() {
        return utils.findByNamedQuery(Student.FIND_ALL_QUERY, Student.class, Collections.emptyMap());
    }

    @Override
    public Student deleteStudent(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Provided id must not be null");
        try {
            Student student = utils.delete(id, Student.class);
            Thesis thesis = this.getThesisByStudent(student.getAisId());
            if (thesis != null) {
                thesis.setAuthor(null);
                thesis.setStatus(ThesisStatus.FREE_TO_TAKE);
                this.updateThesis(thesis);
            }
            return student;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Teacher createTeacher(Long aisId, String name, String email, String password, String department) {
        log.info("Creating teacher with AIS ID " + aisId);
        return utils.create(() -> Teacher.builder()
                .aisId(aisId)
                .name(name)
                .email(email)
                .department(department)
                .institute(department)
                .password(BCryptService.hash(new String(Base64.getDecoder().decode(password))))
                .build());
    }

    @Override
    public Teacher getTeacher(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Provided id must not be null");
        return utils.findOne(id, Teacher.class);
    }

    public Optional<Teacher> getTeacherByEmail(String email) {
        if (email == null)
            throw new IllegalArgumentException("Provided id must not be null");
        EntityManager em = this.emf.createEntityManager();
        TypedQuery<Teacher> q = em.createQuery("select t from Teacher t where t.email = :email", Teacher.class);
        q.setParameter("email", email);
        Optional<Teacher> top = q.getResultStream().findFirst();
        em.close();
        return top;
    }

    public Optional<Student> getStudentByEmail(String email) {
        if (email == null)
            throw new IllegalArgumentException("Provided id must not be null");
        EntityManager em = this.emf.createEntityManager();
        TypedQuery<Student> q = em.createQuery("select s from Student s where s.email = :email", Student.class);
        q.setParameter("email", email);
        Optional<Student> sop = q.getResultStream().findFirst();
        em.close();
        return sop;
    }


    @Override
    public Teacher updateTeacher(Teacher teacher) {
        if (teacher == null)
            throw new IllegalArgumentException("Provided teacher must not be null");
        if (teacher.getAisId() == null)
            throw new IllegalArgumentException("Provided teacher.aisId must not be null");
        return utils.update(teacher);
    }

    @Override
    public List<Teacher> getTeachers() {
        return utils.findByNamedQuery(Teacher.FIND_ALL_QUERY, Teacher.class, Collections.emptyMap());
    }

    @Override
    public Teacher deleteTeacher(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Provided id must not be null");
        utils.execute("delete from Thesis t where t.supervisor.aisId = :teacherId", Collections.singletonMap("teacherId", id), true);
        return utils.delete(id, Teacher.class);
    }

    @Override
    public Thesis makeThesisAssignment(Long supervisor, String title, String type, String description, String registrationNumber) {
        if (supervisor == null)
            throw new IllegalArgumentException("Supervisor id must not be null");
        return utils.create(() -> {
            Teacher teacher = this.getTeacher(supervisor);
            if (teacher == null)
                throw new IllegalArgumentException("Provided supervisor teacher with id '" + supervisor + "' has not been found!");
            LocalDate now = LocalDate.now();
            return Thesis.builder()
                    .registrationNumber(registrationNumber)
                    .title(title)
                    .description(description)
                    .type(ThesisType.valueOf(type.toUpperCase()))
                    .status(ThesisStatus.FREE_TO_TAKE)
                    .supervisor(teacher)
                    .department(teacher.getDepartment())
                    .publishedOn(asDate(now))
                    .deadline(asDate(now.plusMonths(3L)))
                    .build();
        });
    }

    @Override
    public Thesis assignThesis(Long thesisId, Long studentId) {
        if (thesisId == null)
            throw new IllegalArgumentException("Thesis Id must not be null");
        if (studentId == null)
            throw new IllegalArgumentException("Student id must not be null");
        Thesis thesis = getThesis(thesisId);
        if (thesis == null)
            throw new IllegalArgumentException("Thesis with id '" + thesisId + "' has not been found!");
        if (thesis.getStatus() != ThesisStatus.FREE_TO_TAKE)
            throw new IllegalStateException("Thesis is not in the state to be assigned to a student");
        if (LocalDate.now().isAfter(asLocalDate(thesis.getDeadline())))
            throw new IllegalStateException("Thesis cannot be assigned to a student after the deadline on " + thesis.getDeadline().toString());
        Student student = getStudent(studentId);
        if (student == null)
            throw new IllegalArgumentException("Student with id '" + studentId + "' has not been found!");
        Thesis oldThesis = getThesisByStudent(student.getAisId());
        if (oldThesis != null) {
            oldThesis.setAuthor(null);
            oldThesis.setStatus(ThesisStatus.FREE_TO_TAKE);
            utils.update(oldThesis);
        }
        thesis.setAuthor(student);
        thesis.setStatus(ThesisStatus.IN_PROGRESS);
        return utils.update(thesis);
    }

    @Override
    public Thesis submitThesis(Long thesisId) {
        if (thesisId == null)
            throw new IllegalArgumentException("Thesis id must not be null");
        Thesis thesis = getThesis(thesisId);
        if (thesis == null)
            throw new IllegalArgumentException("Thesis with id '" + thesisId + "' has not been found");
        if (LocalDate.now().isAfter(asLocalDate(thesis.getDeadline())))
            throw new IllegalStateException("Thesis cannot be submitted after the deadline on " + thesis.getDeadline().toString());
        if (thesis.getStatus() != ThesisStatus.IN_PROGRESS)
            throw new IllegalStateException("Thesis is not in the state to be submitted");
        if (thesis.getAuthor() == null)
            throw new IllegalStateException("Thesis cannot be submitted if it hasn't been assigned to a student");
        thesis.setStatus(ThesisStatus.SUBMITTED);
        return utils.update(thesis);
    }

    @Override
    public Thesis deleteThesis(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Provided id must not be null");
        return utils.delete(id, Thesis.class);
    }

    @Override
    public List<Thesis> getTheses() {
        return utils.findByNamedQuery(Thesis.FIND_ALL_QUERY, Thesis.class, Collections.emptyMap());
    }

    @Override
    public List<Thesis> getThesesByTeacher(Long teacherId) {
        return utils.findByNamedQuery(Thesis.FIND_ALL_BY_SUPERVISOR, Thesis.class, Collections.singletonMap("teacherId", teacherId));
    }

    @Override
    public Thesis getThesisByStudent(Long studentId) {
        List<Thesis> results = utils.findByNamedQuery(Thesis.FIND_ALL_BY_AUTHOR, Thesis.class, Collections.singletonMap("studentId", studentId));
        return results.stream().findFirst().orElse(null);
    }

    @Override
    public Thesis getThesis(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Provided id must not be null");
        return utils.findOne(id, Thesis.class);
    }

    @Override
    public Thesis updateThesis(Thesis thesis) {
        if (thesis == null)
            throw new IllegalArgumentException("Provided thesis must not be null");
        if (thesis.getId() == null)
            throw new IllegalArgumentException("Provided thesis.id must not be null");
        return utils.update(thesis);
    }
}

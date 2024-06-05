package sk.stuba.fei.uim.vsa.pr2.utils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import sk.stuba.fei.uim.vsa.pr2.model.dto.response.ThesisResponse;

import java.util.UUID;

public class TestData {

    public static final int OBJECT_CONTENT_LENGTH = "{}".getBytes().length;
    public static final int ARRAY_CONTENT_LENGTH = "[{}]".getBytes().length;

    public static Student S01 = new Student(70127L, "Test Student", "xstudent@stuba.sk", "API", 8, 18, "password");
    public static Student S02 = new Student(81230L, "Testovaci Studenttt", "xstu@stuba.sk", "B-API", 3, 6, "password");
    public static Teacher T01 = new Teacher(45678L, "Testovaci ucitel", "ucitel.test@stuba.sk", "UIM", "SI", "password");
    public static Teacher T02 = new Teacher(123456L, "Meno testovacieho uciteľa", "test.teacher@stuba.sk", "UIMM", "BIS", "password");
    public static Thesis TH01 = new Thesis("FEI-" + UUID.randomUUID().toString().replace("-", ""), "Excellent Simple Bachelor Thesis", "Some description of the thesis", ThesisResponse.Type.BACHELOR.toString());
    public static Thesis TH02 = new Thesis("FEI-" + UUID.randomUUID().toString().replace("-", ""), "Extra Hard Master Thesis", "Some description of the thesis but other then the first one", ThesisResponse.Type.MASTER.toString());

    @NoArgsConstructor
    @AllArgsConstructor
    public abstract static class User {
        public String email;
        public String password;
    }


    public static class Student extends User {
        public Long aisId;
        public String name;
        public String programme;
        public int year;
        public int term;

        public Student(Long aisId, String name, String email, String programme, int year, int term, String password) {
            super(email, password);
            this.aisId = aisId;
            this.name = name;
            this.programme = programme;
            this.year = year;
            this.term = term;
        }
    }


    public static class Teacher extends User {
        public Long aisId;
        public String name;
        public String institute;
        public String department;

        public Teacher(Long aisId, String name, String email, String institute, String department, String password) {
            super(email, password);
            this.aisId = aisId;
            this.name = name;
            this.institute = institute;
            this.department = department;
        }
    }

    @AllArgsConstructor
    public static class Thesis {
        public String registrationNumber;
        public String title;
        public String description;
        public String type;
    }

}

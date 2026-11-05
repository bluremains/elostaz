package com.elostaz;

import com.elostaz.dao.UserDAO;
import com.elostaz.model.LessonType;
import com.elostaz.model.Teacher;
import com.elostaz.service.AuthService;
import com.elostaz.service.ServiceException;
import com.elostaz.service.TeacherService;
import com.elostaz.util.Session;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class DemoData {

    public static void main(String[] args) {
        AuthService auth = new AuthService();
        TeacherService teachers = new TeacherService();
        try {
            if (new UserDAO().findByUsername("parent1") != null) {
                System.out.println("Demo data already exists.");
                return;
            }
            auth.registerParent("parent1", "123456", "Mona Hassan", "01012345678");

            addTutor(auth, teachers, "ahmed", "Ahmed Samir", "01111111111", "ahmed@example.com", 150, LessonType.HOME, 8,
                    "Math teacher for preparatory and secondary students. Simple explanations and lots of practice.",
                    new int[][]{{3, 2}, {3, 3}, {5, 3}}, new int[]{1, 2, 3}, DayOfWeek.SATURDAY, DayOfWeek.MONDAY);
            addTutor(auth, teachers, "sara", "Sara Mahmoud", "01222222222", "sara@example.com", 120, LessonType.ONLINE, 5,
                    "English and French tutor. Conversation, grammar and exam preparation.",
                    new int[][]{{2, 1}, {2, 2}, {10, 2}, {10, 3}}, new int[]{3, 4, 9}, DayOfWeek.SUNDAY, DayOfWeek.WEDNESDAY);
            addTutor(auth, teachers, "khaled", "Khaled Nabil", "01033333333", null, 250, LessonType.CENTER, 12,
                    "Physics and Chemistry for secondary school. Small groups and weekly tests.",
                    new int[][]{{5, 3}, {6, 3}, {5, 4}}, new int[]{6, 7, 8}, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY);
            addTutor(auth, teachers, "nour", "Nour Adel", "01544444444", "nour@example.com", 90, LessonType.HOME, 3,
                    "Arabic and Science for primary and preparatory students.",
                    new int[][]{{1, 1}, {1, 2}, {4, 1}, {4, 2}}, new int[]{1, 5, 10}, DayOfWeek.SATURDAY, DayOfWeek.WEDNESDAY);

            Session.end();
            System.out.println("Demo data created. Parent login: parent1 / 123456. Tutor logins: ahmed, sara, khaled, nour / 123456");
        } catch (SQLException | ServiceException e) {
            System.out.println("Could not create demo data: " + e.getMessage());
        }
    }

    private static void addTutor(AuthService auth, TeacherService teachers, String username, String name, String phone, String email,
                                 double price, LessonType type, int years, String bio, int[][] subjectGrade, int[] areaIds,
                                 DayOfWeek day1, DayOfWeek day2) throws ServiceException {
        Teacher t = new Teacher(0, 0, name, phone, email, price, type, years, bio);
        auth.registerTutor(username, "123456", t);
        auth.login(username, "123456");
        int id = Session.getTeacher().getTeacherId();
        for (int[] sg : subjectGrade) {
            teachers.addSubject(id, sg[0], sg[1]);
        }
        for (int areaId : areaIds) {
            teachers.addArea(id, areaId);
        }
        teachers.addAvailability(id, day1, LocalTime.of(16, 0), LocalTime.of(20, 0));
        teachers.addAvailability(id, day2, LocalTime.of(17, 0), LocalTime.of(21, 0));
    }
}

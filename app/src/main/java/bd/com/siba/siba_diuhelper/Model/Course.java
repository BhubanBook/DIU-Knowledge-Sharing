package bd.com.siba.siba_diuhelper.Model;

public class Course {
    private String courseCode;
    private String courseId;
    private String courseName;
    private String expertiseLevel;

    public Course() {
    }

    public Course(String courseCode, String courseId, String courseName, String expertiseLevel) {
        this.courseCode = courseCode;
        this.courseId = courseId;
        this.courseName = courseName;
        this.expertiseLevel = expertiseLevel;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getExpertiseLevel() {
        return expertiseLevel;
    }
}

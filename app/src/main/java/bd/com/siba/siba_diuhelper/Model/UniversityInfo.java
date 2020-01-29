package bd.com.siba.siba_diuhelper.Model;

public class UniversityInfo {
    private String dept;
    private String roll;
    private String batch;
    private String registration;

    public UniversityInfo() {
    }

    public UniversityInfo(String dept, String roll, String batch, String registration) {
        this.dept = dept;
        this.roll = roll;
        this.batch = batch;
        this.registration = registration;
    }

    public String getDept() {
        return dept;
    }

    public String getRoll() {
        return roll;
    }

    public String getBatch() {
        return batch;
    }

    public String getRegistration() {
        return registration;
    }
}

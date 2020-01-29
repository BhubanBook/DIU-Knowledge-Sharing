package bd.com.siba.siba_diuhelper.Model;

public class GeneralInfo {
    private String name;
    private String gender;
    private String bloodGroup;
    private String email;
    private long birthDate;

    public GeneralInfo(){

    }

    public GeneralInfo(String name, String gender, String bloodGroup, String email, long birthDate) {
        this.name = name;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.email = email;
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getEmail() {
        return email;
    }

    public long getBirthDate() {
        return birthDate;
    }
}

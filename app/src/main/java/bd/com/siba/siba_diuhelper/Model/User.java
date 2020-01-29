package bd.com.siba.siba_diuhelper.Model;

public class User {
    private GeneralInfo GeneralInfo;
    private UniversityInfo UniversityInfo;
    private Address presentAddress;
    private String phoneNumber;
    private String profileImage;

    public User() {
    }

    public User(GeneralInfo generalInfo, UniversityInfo universityInfo) {
        GeneralInfo = generalInfo;
        UniversityInfo = universityInfo;
    }

    public void setPresentAddress(Address presentAddress) {
        this.presentAddress = presentAddress;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public GeneralInfo getGeneralInfo() {
        return GeneralInfo;
    }

    public UniversityInfo getUniversityInfo() {
        return UniversityInfo;
    }

    public Address getPresentAddress() {
        return presentAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }
}

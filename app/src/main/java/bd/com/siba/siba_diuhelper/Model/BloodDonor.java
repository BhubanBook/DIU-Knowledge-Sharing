package bd.com.siba.siba_diuhelper.Model;

public class BloodDonor {
    private GeneralInfo GeneralInfo;
    private UniversityInfo UniversityInfo;
    private String phoneNumber;
    private String profileImage;
    private String location;

    public BloodDonor() {
    }

    public BloodDonor(GeneralInfo generalInfo, UniversityInfo universityInfo, String phoneNumber) {
        GeneralInfo = generalInfo;
        UniversityInfo = universityInfo;
        this.phoneNumber = phoneNumber;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public GeneralInfo getGeneralInfo() {
        return GeneralInfo;
    }

    public UniversityInfo getUniversityInfo() {
        return UniversityInfo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}

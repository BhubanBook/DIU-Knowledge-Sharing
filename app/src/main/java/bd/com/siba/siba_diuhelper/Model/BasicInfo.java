package bd.com.siba.siba_diuhelper.Model;

public class BasicInfo {
    private String userId;
    private String phoneNumber;
    private long createdDate;

    public BasicInfo() {
    }

    public BasicInfo(String userId, String phoneNumber, long createdDate) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.createdDate = createdDate;
    }

    public String getUserId() {
        return userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public long getCreatedDate() {
        return createdDate;
    }
}

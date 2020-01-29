package bd.com.siba.siba_diuhelper.Model;

public class RequestReceived {
    private String receiverId;
    private String requestId;

    private String senderId;
    private GeneralInfo senderGeneralInfo;
    private UniversityInfo senderUniversityInfo;
    private Address senderAddressInfo;
    private String courseName;
    private String senderPhoneNumber;
    private String senderProfileImage;
    private double senderAverageRating;
    private int senderTotalRatingCounter;
    private String status;
    private long requestDate;

    public RequestReceived() {
    }

    public RequestReceived(String receiverId, String requestId, String senderId, GeneralInfo senderGeneralInfo, UniversityInfo senderUniversityInfo, Address senderAddressInfo, String courseName,
                           String senderPhoneNumber, String senderProfileImage, double senderAverageRating, int senderTotalRatingCounter, String status, long requestDate) {
        this.receiverId = receiverId;
        this.requestId = requestId;
        this.senderId = senderId;
        this.senderGeneralInfo = senderGeneralInfo;
        this.senderUniversityInfo = senderUniversityInfo;
        this.senderAddressInfo = senderAddressInfo;
        this.courseName = courseName;
        this.senderPhoneNumber = senderPhoneNumber;
        this.senderProfileImage = senderProfileImage;
        this.senderAverageRating = senderAverageRating;
        this.senderTotalRatingCounter = senderTotalRatingCounter;
        this.status = status;
        this.requestDate = requestDate;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getSenderId() {
        return senderId;
    }

    public GeneralInfo getSenderGeneralInfo() {
        return senderGeneralInfo;
    }

    public UniversityInfo getSenderUniversityInfo() {
        return senderUniversityInfo;
    }

    public Address getSenderAddressInfo() {
        return senderAddressInfo;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getSenderPhoneNumber() {
        return senderPhoneNumber;
    }

    public String getSenderProfileImage() {
        return senderProfileImage;
    }

    public double getSenderAverageRating() {
        return senderAverageRating;
    }

    public int getSenderTotalRatingCounter() {
        return senderTotalRatingCounter;
    }

    public String getStatus() {
        return status;
    }

    public long getRequestDate() {
        return requestDate;
    }
}

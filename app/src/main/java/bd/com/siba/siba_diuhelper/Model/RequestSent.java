package bd.com.siba.siba_diuhelper.Model;

public class RequestSent {
    private String senderId;
    private String requestId;

    private String receiverId;
    private GeneralInfo receiverGeneralInfo;
    private UniversityInfo receiverUniversityInfo;
    private Address receiverAddressInfo;
    private String courseName;
    private String receiverPhoneNumber;
    private String receiverProfileImage;
    private double receiverAverageRating;
    private int receiverTotalRatingCounter;
    private String status;
    private long requestDate;


    public RequestSent() {
    }

    public RequestSent(String senderId, String requestId, String receiverId, GeneralInfo receiverGeneralInfo, UniversityInfo receiverUniversityInfo, Address receiverAddressInfo,
                       String courseName, String receiverPhoneNumber, String receiverProfileImage, double receiverAverageRating, int receiverTotalRatingCounter, String status, long requestDate) {
        this.senderId = senderId;
        this.requestId = requestId;
        this.receiverId = receiverId;
        this.receiverGeneralInfo = receiverGeneralInfo;
        this.receiverUniversityInfo = receiverUniversityInfo;
        this.receiverAddressInfo = receiverAddressInfo;
        this.courseName = courseName;
        this.receiverPhoneNumber = receiverPhoneNumber;
        this.receiverProfileImage = receiverProfileImage;
        this.receiverAverageRating = receiverAverageRating;
        this.receiverTotalRatingCounter = receiverTotalRatingCounter;
        this.status = status;
        this.requestDate = requestDate;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public GeneralInfo getReceiverGeneralInfo() {
        return receiverGeneralInfo;
    }

    public UniversityInfo getReceiverUniversityInfo() {
        return receiverUniversityInfo;
    }

    public Address getReceiverAddressInfo() {
        return receiverAddressInfo;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getReceiverPhoneNumber() {
        return receiverPhoneNumber;
    }

    public String getReceiverProfileImage() {
        return receiverProfileImage;
    }

    public double getReceiverAverageRating() {
        return receiverAverageRating;
    }

    public int getReceiverTotalRatingCounter() {
        return receiverTotalRatingCounter;
    }

    public String getStatus() {
        return status;
    }

    public long getRequestDate() {
        return requestDate;
    }
}

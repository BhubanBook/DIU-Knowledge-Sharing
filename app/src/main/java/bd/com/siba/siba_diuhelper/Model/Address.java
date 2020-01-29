package bd.com.siba.siba_diuhelper.Model;

public class Address {
    private String address;
    private double latitude;
    private double longitude;

    public Address() {
    }

    public Address(String address, double latitude, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}

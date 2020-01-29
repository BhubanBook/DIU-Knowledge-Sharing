package bd.com.siba.siba_diuhelper.Model;

public class Notification {
    private String title;
    private String description;
    private long validity;

    public Notification() {
    }

    public Notification(String title, String description, long validity) {
        this.title = title;
        this.description = description;
        this.validity = validity;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getValidity() {
        return validity;
    }
}

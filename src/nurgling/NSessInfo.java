package nurgling;

public class NSessInfo {
    String username;
    boolean isVerified = false;
    boolean isSubscribed = false;
    public NCharacterInfo characterInfo = null;

    public NSessInfo(String username) {
        this.username = username;
    }
}

package nurgling;

public class NConfiguration {

    public boolean isRealTime = true;

    private static NConfiguration instance = null;
    public static NConfiguration getInstance() {
        if(instance == null){
            instance = new NConfiguration();
        }
        return instance;
    }
}

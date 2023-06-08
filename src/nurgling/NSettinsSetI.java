package nurgling;

public class NSettinsSetI extends NSettinsSetW {
    public NSettinsSetI(String label, NConfiguration.NInteger val) {
        super(label);
        this.val = val;
    }

    NConfiguration.NInteger val;

    @Override
    void parseValue() {
        val.set(Integer.parseInt(textEntry.text()));
    }
}

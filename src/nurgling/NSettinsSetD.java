package nurgling;

public class NSettinsSetD extends NSettinsSetW {
    public NSettinsSetD(String label, NConfiguration.NDouble val) {
        super(label);
        this.val = val;
    }

    NConfiguration.NDouble val;

    @Override
    void parseValue() {
        val.set(Double.parseDouble(textEntry.text()));
    }
}

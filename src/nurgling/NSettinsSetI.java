package nurgling;

public class NSettinsSetI extends NSettinsSetW {
    public NSettinsSetI(String label, NConfiguration.NInteger val) {
        super(label);
        this.val = val;
    }

    public NSettinsSetI(String label) {
        super(label);
        this.val = new NConfiguration.NInteger(0);
    }

    public void setVal(NConfiguration.NInteger val) {
        this.val = val;
        setText(val.toString());
    }

    NConfiguration.NInteger val;

    @Override
    void parseValue() {
        if(textEntry.text().isEmpty())
        {
            textEntry.settext("0");
            val.set(0);
        }
        try {
            val.set(Integer.parseInt(textEntry.text()));
        }
        catch (NumberFormatException e)
        {
            NUtils.getGameUI().error("Incorrect format");
        }
    }
}

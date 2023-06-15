package nurgling;

public class NSettinsSetD extends NSettinsSetW {
    public NSettinsSetD(String label, NConfiguration.NDouble val) {
        super(label);
        this.val = val;
    }

    NConfiguration.NDouble val;

    public NSettinsSetD(String label) {
        super(label);
        this.val = new NConfiguration.NDouble(0.);
    }

    public void setVal(NConfiguration.NDouble val) {
        this.val = val;
        setText(val.toString());
    }

    @Override
    void parseValue() {
        if(textEntry.text().isEmpty())
        {
            textEntry.settext("0");
            val.set(0.);
        }
        try {
            val.set(Double.parseDouble(textEntry.text()));
        }
        catch (NumberFormatException e)
        {
            NUtils.getGameUI().error("Incorrect format");
        }
    }
}

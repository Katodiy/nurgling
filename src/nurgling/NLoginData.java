package nurgling;


public class NLoginData {
    public String name;
    public String pass;
    
    public NLoginData(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }
    
    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof NLoginData ))return false;
        NLoginData ol = ( NLoginData )other;
        return ol.name.equals(name) && ol.pass.equals(pass);
    }
}
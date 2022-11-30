package nurgling;


import haven.Fightview;
import haven.Gob;

public class NFightView extends Fightview {
    public void give () {
        if ( current != null ) {
            wdgmsg ( "give", ( int ) current.gobid, 1 );
        }
    }
    
    @Override
    public boolean show ( boolean show ) {
        give ();
        return super.show ( show );
    }
    
    public Gob getCurrentGob () {
        if ( current != null ) {
            return NUtils.getGameUI().ui.sess.glob.oc.getgob ( current.gobid );
        }
        else {
            return null;
        }
    }
}

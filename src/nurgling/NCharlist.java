package nurgling;

import haven.Button;
import haven.Charlist;
import haven.RemoteUI;
import haven.UI;

public class NCharlist extends Charlist {
    static NCharlist instance;

    Button logout;

    public NCharlist(int height ) {
        super ( height );
        instance = this;
    }
    
    public static void play(){
        if(NConfiguration.botmod!=null && instance!=null) {
            for ( Char c: instance.chars) {
                if(c.name.equals ( NConfiguration.botmod.character  ))
                {
                    instance.wdgmsg ( "play", NConfiguration.botmod.character );
                    instance = null;
                    break;
                }
            }
        }
    }

    protected void added() {
        parent.setfocus(this);
        logout = parent.add(new Button(UI.scale(90), "Log out") {
            @Override
            public void click() {
                RemoteUI rui = (RemoteUI) ui.rcvr;
                synchronized (rui.sess) {
                    rui.sess.close();
                }
            }
        }, UI.scale(121, 553));
    }
}

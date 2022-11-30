package nurgling;

import haven.*;

import java.awt.*;

public class NLoginScreen extends LoginScreen {
    static Text.Foundry NUtils;
    boolean isStarted = false;
    
    static {
        NUtils = new Text.Foundry ( Text.sans, 14 ).aa ( true );
    }
    
    public NLoginScreen (String hostname) {
        super (hostname);
        NConfiguration.getInstance().disabledCheck = false;
        add ( new LoginList ( 200, 29 ), new Coord ( 10, 10 ) );
        optbtn.move(new Coord(680, 30));
        adda(new StatusLabel(hostname, 0.5), bgc.x, bg.sz().y, 0.5, 1);
    }
    
    @Override
    protected void progress ( String p ) {
        super.progress ( p );
        if ( !isStarted ) {
            if ( NConfiguration.botmod!=null && NConfiguration.getInstance ().restart ) {
                for ( NLoginData data : NConfiguration.getInstance ().logins ) {
                    if ( data.name.equals ( NConfiguration.botmod.user ) ) {
                        wdgmsg ( "login", new Object[]{ new AuthClient.NativeCred (  NConfiguration.botmod.user, NConfiguration.botmod.password ), false } );
                        NConfiguration.getInstance ().restart = false;
                        isStarted = true;
                        break;
                    }
                }
            }
        }
    }
    
    public void wdgmsg (
            Widget sender,
            String msg,
            Object... args
    ) {
        if ( sender != optbtn || sender != opts ) {
                if ( !login.pass.text ().isEmpty () ) {
                    boolean isNew = true;
                    for ( NLoginData value : NConfiguration.getInstance ().logins ) {
                        if ( value.name.equals ( login.user.text() ) ) {
                            isNew = false;
                            value.pass = login.pass.text();
                            break;
                        }
                    }
                    if ( isNew ) {
                        NConfiguration.getInstance ().logins
                                .add ( new NLoginData ( login.user.text(),
                                        login.pass.text() ) );
                    }
                }
        }
        NConfiguration.getInstance ().write ();
//        Thresholds.write ();
        super.wdgmsg ( sender, msg, args );
    }
    
    public class LoginList extends Listbox<NLoginData> {
        private final Tex xicon = NUtils.render ( "\u2716", Color.RED ).tex ();
        private int hover = -1;
        private final static int ITEM_HEIGHT = 20;
        private Coord lastMouseDown = Coord.z;
        
        public LoginList (
                int w,
                int h
        ) {
            super ( w, h, ITEM_HEIGHT );
        }
        
        @Override
        protected void drawbg ( GOut g ) {
            g.chcolor ( 0, 0, 0, 120 );
            g.frect ( Coord.z, sz );
            g.chcolor ();
        }
        
        @Override
        protected void drawsel ( GOut g ) {
        }
        
        @Override
        protected NLoginData listitem ( int i ) {
            return NConfiguration.getInstance ().logins.get ( i );
        }
        
        @Override
        protected int listitems () {
            return NConfiguration.getInstance ().logins.size ();
        }
        
        @Override
        public void mousemove ( Coord c ) {
            setHoverItem ( c );
            super.mousemove ( c );
        }
        
        @Override
        public boolean mousewheel (
                Coord c,
                int amount
        ) {
            setHoverItem ( c );
            return super.mousewheel ( c, amount );
        }
        
        private void setHoverItem ( Coord c ) {
            if ( c.x > 0 && c.x < sz.x && c.y > 0 && c.y < listitems () * ITEM_HEIGHT ) {
                hover = c.y / ITEM_HEIGHT + sb.val;
            }
            else {
                hover = -1;
            }
        }
        
        @Override
        protected void drawitem (
                GOut g,
                NLoginData item,
                int i
        ) {
            if ( hover == i ) {
                g.chcolor ( 96, 96, 96, 255 );
                g.frect ( Coord.z, g.sz () );
                g.chcolor ();
            }
            Tex tex = textfs.render ( item.name, Color.WHITE ).tex ();
            int y = ITEM_HEIGHT / 2 - tex.sz ().y / 2;
            g.image ( tex, new Coord ( 5, y ) );
            g.image ( xicon, new Coord ( sz.x - 25, y ) );
        }
        
        @Override
        public boolean mousedown (
                Coord c,
                int button
        ) {
            lastMouseDown = c;
            return super.mousedown ( c, button );
        }
        
        @Override
        protected void itemclick (
                NLoginData itm,
                int button
        ) {
            if ( button == 1 ) {
                if ( lastMouseDown.x >= sz.x - 25 && lastMouseDown.x <= sz.x - 25 + 20 ) {
                    NConfiguration.getInstance ().logins.remove ( itm );
                    NConfiguration.getInstance ().write ();
                }
                else if ( c.x < sz.x - 35 ) {
                    parent.wdgmsg ( "forget" );
                    parent.wdgmsg ( "login", new Object[]{ new AuthClient.NativeCred ( itm.name, itm.pass ), false } );
                }
                super.itemclick ( itm, button );
            }
        }
    }
    
}

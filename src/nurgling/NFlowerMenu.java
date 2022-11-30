package nurgling;

import haven.FlowerMenu;

public class NFlowerMenu extends FlowerMenu {
    public static boolean selectNext = false;
    public static boolean selectPick = false;
    public static String name;
    public static boolean needCheck;
    public static NFlowerMenu instance;
    
    public NFlowerMenu(String... options ) {
        super ( options );
        instance = this;
    }
    
    public static void stop ()
            throws InterruptedException {
        if ( instance != null ) {
            instance.choose ( null );
            if ( instance.kg != null ) {
                instance.kg.remove ();
                instance.mg.remove ();
            }
            instance.lostfocus ();
            instance.destroy ();
            instance = null;
        }
    }
    
    public static void select ( String name1 ) {
        name = name1;
        selectNext = true;
    }
    
    public void selectInCurrent ( String name1 ) {
        for ( Petal p : opts ) {
            if ( p.name.contains ( name1 ) ) {
                choose ( p );
            }
        }
    }
    
    public boolean isContain(String name1 ){
        for ( Petal p : opts ) {
            if ( p.name.contains ( name1 ) ) {
                return true;
            }
        }
        return false;
    }
    
    public void selectInCurrent ( NAlias name1 ) {
        for ( Petal p : opts ) {
            if ( NUtils.checkName ( p.name,name1 ) ) {
                choose ( p );
            }
        }
    }
    
    
    protected void added () {
        if ( c.equals ( -1, -1 ) ) {
            c = parent.ui.lcc;
        }
        mg = ui.grabmouse ( this );
        kg = ui.grabkeys ( this );
        organize ( opts );
        new NOpenings ().ntick ( 0 );
    }
    
    public class NOpenings extends Opening {

        @Override
        public void ntick(double s) {
            super.ntick(s);
            if (s == 1.0) {
                if (selectNext) {
                    for (Petal p : opts) {
                        if (p.name.contains(name)) {
                            selectNext = false;
                            choose(p);
                        }
                    }

                }
            }
            if (NConfiguration.getInstance().autoPicking && !NUtils.getUI().modshift) {
                for (Petal p : opts) {
                    for (NConfiguration.PickingAction pa : NConfiguration.getInstance().pickingActions)
                        if (pa.isEnable && p.name.contains(pa.action))
                            choose(p);
                }
            }
        }
    }

    
    
    public boolean findInCurrentFlower ( String name ) {
        for ( Petal p : opts ) {
            if ( p.name.contains ( name ) ) {
                return true;
            }
        }
        return false;
    }
    
    public boolean findInCurrentFlower ( NAlias name ) {
        for ( Petal p : opts ) {
            if ( NUtils.checkName ( p.name,name ) ) {
                return true;
            }
        }
        return false;
    }
    
    public static void check () {
        needCheck = true;
    }
}

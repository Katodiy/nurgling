package nurgling.bots;

import haven.Button;
import haven.Coord;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.CraftAndFill;
import nurgling.bots.tools.CraftCommand;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class CabbageMaker extends Bot {

    public CabbageMaker(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "CabbageMaker";
        win_sz.y = 250;


        runActions.add ( new CraftAndFill(out_area ,
                new NAlias( new ArrayList<String> ( Arrays.asList ( "wrap", "fruitroast", "nutje" ) ) ), 1024, command,
                1000 ) );
        
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException { super.initAction();
        int y = 0;
        r_1 = window.add ( new Button ( window.buttons_size, "Мясо" ) {
            @Override
            public void click () {
                command.command = new char[]{ 'c', 'f', 'w', 'p', 'n' };
                command.name = "Nutjerky";
                command.ingredients = new ArrayList<Ingredient> ();
                command.ingredients.add ( new Ingredient( new NAlias ( new ArrayList<String> (
                        Arrays.asList ( "Raw", "Chicken Meat", "Magpie Meat", "Rock Dove Meat" ) ) ), ingred_2,
                        1 ) );
                command.ingredients.add ( new Ingredient ( new NAlias ( new ArrayList<String> (
                        Arrays.asList ( "Almond", "Beech Nuts", "Carob Pod", "Chestnut", "Hazelnut", "King's Acorn",
                                "Oak Acorn", "Walnut", "Withercorn" ) ) ), ingred_1, 2 ) );
                command.ingredients.add ( new Ingredient ( new NAlias ( "Leaf" ,"Leav" ), ingred_3, 2 ) );
                _start.set ( true );
                installButtons();
                types = Types.MEAT;
            }
        }, new Coord ( 0, y ) );
        y += 25;
        r_2 = window.add ( new Button ( window.buttons_size, "Рыба" ) {
            @Override
            public void click () {
                command.command = new char[]{ 'c', 'f', 'w', 'p', 'i' };
                command.name = "Fishwrap";
                command.ingredients = new ArrayList<Ingredient> ();
                command.ingredients.add ( new Ingredient ( new NAlias ( new ArrayList<String> (
                        Arrays.asList ( "Blueberries", "Raspberry", "Blackberry", "Blackcurrant", "Cherries",
                                "Dog Rose Hips", "Elderberries", "Gooseberry", "Lingonberries", "Mulberry",
                                "Redcurrant", "Seaberries", "Sloan Berries" ) ) ), ingred_1, 2 ) );
                command.ingredients.add ( new Ingredient ( new NAlias ( "Filet" ), ingred_2, 1 ) );
                command.ingredients.add ( new Ingredient ( new NAlias ( "Leaf" ,"Leav" ),  ingred_3, 2 ) );
                command.ingredients.add ( new Ingredient ( new NAlias ( "Leaf" , "Leav", "Chives"),  ingred_4, 1 ) );
                _start.set ( true );
                installButtons();
                types = Types.FISH;
            }
        }, new Coord ( 0, y ) );
        y += 25;
        r_3 = window.add ( new Button ( window.buttons_size, "Фрукты" ) {
            @Override
            public void click () {
                command.command = new char[]{ 'c', 'f', 'w', 'p', 'f' };
                command.name = "Fruitroast";
                command.ingredients = new ArrayList<Ingredient> ();
                command.ingredients.add ( new Ingredient (  new NAlias ( new ArrayList<String> (
                        Arrays.asList ( "Blueberries", "Raspberry", "Blackberry", "Blackcurrant", "Cherries",
                                "Dog Rose Hips", "Elderberries", "Gooseberry", "Lingonberries", "Mulberry",
                                "Redcurrant", "Seaberries", "Sloan Berries", "Grapes", "Lemon", "Medlar", "Pear",
                                "Persimmon", "Plum", "Quince", "Red Apple", "Sorb Apple", "Wood Strawberry" ) ) ), ingred_1,
                         3 ) );
                command.ingredients
                        .add ( new Ingredient (  new NAlias ( "Leaf" ,"Leav" ), ingred_2,
                                2 ) );
                
                _start.set ( true );
                installButtons();
                types = Types.FRUIT;
            }
        }, new Coord ( 0, y ) );
        y += 25;
        window.add ( new Button ( window.buttons_size, "Результаты" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, out_zone, m_selection_start, out_area),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        b_1 = window.add ( new Button ( window.buttons_size, "1" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone1, m_selection_start, ingred_1),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        b_1.hide();
        y += 25;
        b_2 = window.add ( new Button ( window.buttons_size, "2" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone2, m_selection_start, ingred_2),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        b_2.hide();
        y += 25;
        b_3 = window.add ( new Button ( window.buttons_size, "3" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone3, m_selection_start, ingred_3),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        b_3.hide();
        y += 25;
        b_4 = window.add ( new Button ( window.buttons_size, "4" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone4, m_selection_start, ingred_4),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        b_4.hide();



        while ( !_start.get () || !out_zone.get() || !checkCommand() ) {
            Thread.sleep ( 100 );
        }
    }

    Button r_1;
    Button r_2;
    Button r_3;
    Button b_1;
    Button b_2;
    Button b_3;
    Button b_4;

    private void installButtons(){
        b_1.hide();
        b_2.hide();
        b_3.hide();
        b_4.hide();
        r_1.hide();
        r_2.hide();
        r_3.hide();
        switch (command.name){
            case "Fruitroast":
                b_1.setText("1. Фрукты");
                b_1.show();
                b_2.setText("2. Листья");
                b_2.show();
            break;
            case "Fishwrap":
                b_1.setText("1. Ягода");
                b_1.show();
                b_2.setText("2. Рыба");
                b_2.show();
                b_3.setText("3. Листья");
                b_3.show();
                b_4.setText("4. Специи");
                b_4.show();
                break;
            case "Nutjerky":
                b_1.setText("1. Орехи");
                b_1.show();
                b_2.setText("2. Мясо");
                b_2.show();
                b_3.setText("3. Листья");
                b_3.show();
                break;
        }
    }

    private boolean checkCommand(){
        switch (command.name){
            case "Fruitroast":
                return (_zone1.get() && _zone2.get());
            case "Fishwrap":
                return (_zone1.get() && _zone2.get() && _zone3.get() && _zone4.get());
            case "Nutjerky":
                return (_zone1.get() && _zone2.get() && _zone3.get());
        }
        return false;
    }
    
    @Override
    public void endAction () {
        _start.set ( false );
        out_zone.set ( false );
        _zone1.set ( false );
        _zone2.set ( false );
        _zone3.set ( false );
        _zone4.set ( false );
        b_1.hide();
        b_2.hide();
        b_3.hide();
        b_4.hide();
        super.endAction ();
    }
    
    enum Types {
        MEAT, FISH, FRUIT
    }
    
    CraftCommand command = new CraftCommand ();
    private Types types = Types.MEAT;
    private AtomicBoolean _start = new AtomicBoolean ( false );

    private AtomicBoolean out_zone = new AtomicBoolean ( false );
    private AtomicBoolean _zone1 = new AtomicBoolean ( false );
    private AtomicBoolean _zone2 = new AtomicBoolean ( false );
    private AtomicBoolean _zone3 = new AtomicBoolean ( false );
    private AtomicBoolean _zone4 = new AtomicBoolean ( false );
    private NArea out_area = new NArea ();
    private NArea ingred_1 = new NArea ();
    private NArea ingred_2 = new NArea ();
    private NArea ingred_3 = new NArea ();
    private NArea ingred_4 = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );


}

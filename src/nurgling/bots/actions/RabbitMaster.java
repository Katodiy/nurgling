package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import haven.WItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class RabbitMaster implements Action {
    int delay = 200;
    private class RabbitCoop {

        /// Зайчатник
        Gob gob;
        /// Качество зайца
        double quality;
        /// Массив зайчих
        ArrayList<Double> bunnies = new ArrayList<> ();
        
        public RabbitCoop (
                Gob gob,
                double quality
        ) {
            this.gob = gob;
            this.quality = quality;
        }
    }
    
    Comparator<RabbitCoop> comparator1 = new Comparator<RabbitCoop> () {
        @Override
        public int compare (
                RabbitCoop o1,
                RabbitCoop o2
        ) {
            int res = Double.compare ( o1.quality, o2.quality );
            if ( res == 0 ) {
                if ( o1.bunnies.size () > 0 && o2.bunnies.size () > 0 ) {
                    double s1 = 0;
                    for ( Double h1 : o1.bunnies ) {
                        s1 += h1;
                    }
                    double s2 = 0;
                    for ( Double h2 : o2.bunnies ) {
                        s2 += h2;
                    }
                    s1 /= o1.bunnies.size ();
                    s2 /= o2.bunnies.size ();
                    res = Double.compare ( s1, s2 );
                }
            }
            return res;
        }
    };
    
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        ArrayList<Gob> rc = Finder
                .findObjectsInArea ( new NAlias( "rabbithutch" ), Finder.findNearestMark( AreasID.rabbit ));
        ArrayList<Gob> bunnies = Finder
                .findObjectsInArea ( new NAlias ( "rabbithutch" ), Finder.findNearestMark( AreasID.bunny ));
        ArrayList<RabbitCoop> does = new ArrayList<RabbitCoop> ();
        
        for ( Gob gob : rc ) {
            new PathFinder( gui, gob ).run ();
            if ( new OpenTargetContainer ( gob, "Rabbit Hutch" ).run ( gui ).type != Results.Types.SUCCESS ) {
                return new Results ( Results.Types.OPEN_FAIL );
            }
            NUtils.waitEvent(()-> gui.getInventory ( "Rabbit Hutch" ).getItem ( new NAlias ( "rabbit-buck" ) )!=null,50);
            WItem buck = gui.getInventory ( "Rabbit Hutch" ).getItem ( new NAlias ( "rabbit-buck" ) );
            RabbitCoop curRabbitHutch = new RabbitCoop ( gob, NUtils.getWItemQuality ( buck ) );
            
            /// Получаем инфо по зайчихам
            ArrayList<WItem> curdoes = gui.getInventory ( "Rabbit Hutch" ).getItems ( new NAlias ( "rabbit-doe" ) );
            for ( WItem hen : curdoes ) {
                curRabbitHutch.bunnies.add ( NUtils.getWItemQuality ( hen ) );
            }
            curRabbitHutch.bunnies.sort ( Double::compareTo );
            does.add ( curRabbitHutch );
        }
        does.sort ( comparator1 );

    
        for ( Gob bunnie : bunnies ) {
            ArrayList<Double> qdoes = new ArrayList<Double> ();
            ArrayList<Double> qbucks = new ArrayList<Double> ();
            new PathFinder ( gui, bunnie ).run ();
            if ( new OpenTargetContainer ( bunnie, "Rabbit Hutch" ).run ( gui ).type != Results.Types.SUCCESS ) {
                return new Results ( Results.Types.OPEN_FAIL );
            }
            /// Получаем инфо по зайчихам
            ArrayList<WItem> curdoes = gui.getInventory ( "Rabbit Hutch" ).getItems ( new NAlias ( "rabbit-doe" ) );
            for ( WItem hen : curdoes ) {
                qdoes.add ( NUtils.getWItemQuality ( hen ) );
            }
            /// Получаем инфо по зайцам
            ArrayList<WItem> curbucks = gui.getInventory ( "Rabbit Hutch" ).getItems ( new NAlias ( "rabbit-buck" ) );
            for ( WItem roost : curbucks ) {
                qbucks.add ( NUtils.getWItemQuality ( roost ) );
            }
            qdoes.sort ( Double::compareTo );
            qbucks.sort ( Double::compareTo );
    
    
            /// Цикл зайчих
            for ( int i = qbucks.size () - 1 ; i >= 0 ; i -= 1 ) {
                new PathFinder ( gui, bunnie ).run ();
                if ( new OpenTargetContainer ( bunnie, "Rabbit Hutch" ).run ( gui ).type != Results.Types.SUCCESS ) {
                    return new Results ( Results.Types.OPEN_FAIL );
                }
                NUtils.waitEvent ( () -> gui.getInventory ( "Rabbit Hutch" )!=null, delay );
                double current_quality = qbucks.get ( i );
                NUtils.transferItem ( gui.getInventory ( "Rabbit Hutch" ),
                        gui.getInventory ( "Rabbit Hutch" ).getItem ( current_quality, new NAlias ( "rabbit-buck" ) ) );
        
                for ( int j = does.size () - 1 ; j >= 0 ; j -= 1 ) {
                    if ( does.get ( j ).quality < current_quality ) {
                        new PathFinder ( gui, does.get ( j ).gob ).run ();
                        if ( new OpenTargetContainer ( does.get ( j ).gob, "Rabbit Hutch" ).run ( gui ).type != Results.Types.SUCCESS ) {
                            return new Results ( Results.Types.OPEN_FAIL );
                        }
                        NUtils.waitEvent ( ()->gui.getInventory ( "Rabbit Hutch" )!=null,delay );
                        int finalJ = j;
                        NUtils.waitEvent ( () -> gui.getInventory("Rabbit Hutch")
                                .getItem(does.get(finalJ).quality, new NAlias("rabbit-buck"))!=null, 20 );
                        WItem lqhen = gui.getInventory ( "Rabbit Hutch" ).getItem ( does.get ( j ).quality, new NAlias ( "rabbit-buck" ) );
                        Coord pos = new Coord ( ( lqhen.c.x - 1 ) / 33, ( lqhen.c.y - 1 ) / 33 );
                        NUtils.transferItem ( gui.getInventory ( "Rabbit Hutch" ), lqhen );
                        NUtils.takeItemToHand (
                                gui.getInventory ().getItem ( current_quality, new NAlias ( "rabbit-buck" ) ).item );
                        NUtils.waitEvent(()-> gui.vhand!=null,50);

                        NUtils.transferToInventory ( "Rabbit Hutch", pos );
                        does.get ( j ).quality = current_quality;
                        current_quality = NUtils.getWItemQuality ( lqhen );
                    }
                }
                NUtils.waitEvent (()->gui.getInventory ().getItem ( new NAlias ( "rabbit-buck" ) )!=null,delay  );
                /// Сворачиваем шею зайцу
                WItem buck = gui.getInventory ().getItem ( new NAlias ( "rabbit-buck" ) );
                if ( buck == null ) {
                    return new Results ( Results.Types.NO_ITEMS );
                }
                new SelectFlowerAction ( buck, "Wring neck", SelectFlowerAction.Types.Inventory ).run ( gui );
                NUtils.waitEvent (()->gui.getInventory ().getItem ( new NAlias ( "rabbit-dead" ) )!=null,delay) ;
                /// Снимаем шкуру
                WItem rabbit_dead = gui.getInventory ().getItem ( new NAlias ( "rabbit-dead" ) );
                if ( rabbit_dead == null ) {
                    return new Results ( Results.Types.NO_ITEMS );
                }
                new SelectFlowerAction ( rabbit_dead, "Flay", SelectFlowerAction.Types.Inventory ).run ( gui );
                NUtils.waitEvent (()->gui.getInventory ().getItem ( new NAlias ( "rabbit-carcas" ) )!=null,delay  );
                NUtils.waitEvent (()->gui.getInventory ().getItem ( TransferRawHides.raw_hides )!=null,delay  );
                /// Сбрасываем шкуру
                new TransferRawHides ().run ( gui );
                /// Свежуем зайца
                WItem carcas = gui.getInventory ().getItem ( new NAlias ( "rabbit-carcas" ) );
                if ( carcas == null ) {
                    return new Results ( Results.Types.NO_ITEMS );
                }
                new SelectFlowerAction ( carcas, "Clean", SelectFlowerAction.Types.Inventory ).run ( gui );
                NUtils.waitEvent (()->gui.getInventory ().getItem ( new NAlias ( "rabbit-clean" ) )!=null,delay);
                NUtils.waitEvent (()->gui.getInventory ().getItem ( new NAlias ( "entrails" ) )!=null,delay);
                new TransferTrash ().run ( gui );
                /// Разделываем зайца
                WItem rabbit_cleaned = gui.getInventory ().getItem ( new NAlias ( "rabbit-clean" ) );
                if ( rabbit_cleaned == null ) {
                    return new Results ( Results.Types.NO_ITEMS );
                }
                new SelectFlowerAction ( rabbit_cleaned, "Butcher", SelectFlowerAction.Types.Inventory ).run ( gui );
                NUtils.waitEvent (()->gui.getInventory ().getItem ( new NAlias ( "bone" ) )!=null,delay  );
                NUtils.waitEvent (()->gui.getInventory ().getItem ( new NAlias ( "meat" ) )!=null,delay  );
                new TransferBones ().run ( gui );
                new TransferMeat ().run ( gui );
        
            }
    
            for ( int i = qdoes.size () - 1 ; i >= 0 ; i -= 1 ) {
                new PathFinder ( gui, bunnie ).run ();
                if ( new OpenTargetContainer ( bunnie, "Rabbit Hutch" ).run ( gui ).type != Results.Types.SUCCESS ) {
                    return new Results ( Results.Types.OPEN_FAIL );
                }
                NUtils.waitEvent ( () -> gui.getInventory ( "Rabbit Hutch" )!=null, delay );
                double current_quality = qdoes.get ( i );
                NUtils.transferItem ( gui.getInventory ( "Rabbit Hutch" ),
                        gui.getInventory ( "Rabbit Hutch" ).getItem ( current_quality, new NAlias ( "rabbit-doe" ) ) );
        
                for ( int j = does.size () - 1 ; j >= 0 ; j -= 1 ) {
                    for ( int k = 0 ; k < does.get ( j ).bunnies.size () ; k += 1 ) {
                        if ( does.get ( j ).bunnies.get ( k ) < current_quality ) {
                            new PathFinder ( gui, does.get ( j ).gob ).run ();
                            if ( new OpenTargetContainer ( does.get ( j ).gob, "Rabbit Hutch" ).run ( gui ).type !=
                                    Results.Types.SUCCESS ) {
                                return new Results ( Results.Types.OPEN_FAIL );
                            }
                            NUtils.waitEvent ( () -> false, 5 );
                            WItem lqhen = gui.getInventory ( "Rabbit Hutch" ).getItem ( does.get ( j ).bunnies.get ( k ), new NAlias ( "rabbit-doe" ) );
                            Coord pos = new Coord ( ( lqhen.c.x - 1 ) / 33, ( lqhen.c.y - 1 ) / 33 );
                            NUtils.transferItem ( gui.getInventory ( "Rabbit Hutch" ), lqhen );
                            NUtils.takeItemToHand (
                                    gui.getInventory ().getItem ( current_quality, new NAlias ( "rabbit-doe" ) ).item );
                            int counter = 0;
                            while ( gui.vhand == null && counter < 20 ) {
                                Thread.sleep ( 100 );
                                counter += 1;
                            }
                            NUtils.transferToInventory ( "Rabbit Hutch", pos );
                            current_quality = NUtils.getWItemQuality ( lqhen );
                            /// Обновляем данные по зайчихам
                            Thread.sleep ( 300 );
                            ArrayList<WItem> newcurhens = gui.getInventory ( "Rabbit Hutch" ).getItems ( new NAlias ( "rabbit-doe" ) );
                            does.get ( j ).bunnies = new ArrayList<> ();
                            for ( WItem hen : newcurhens ) {
                                does.get ( j ).bunnies.add ( NUtils.getWItemQuality ( hen ) );
                            }
                            does.get ( j ).bunnies.sort ( Double::compareTo );
                            break;
                        }
                    }
                }
                NUtils.waitEvent (()->gui.getInventory ().getItem ( new NAlias ( "rabbit-doe" ) )!=null,delay  );
                /// Сворачиваем шею зайцу
                WItem buck = gui.getInventory ().getItem ( new NAlias ( "rabbit-doe" ) );
                if ( buck == null ) {
                    return new Results ( Results.Types.NO_ITEMS );
                }
                new SelectFlowerAction ( buck, "Wring neck", SelectFlowerAction.Types.Inventory ).run ( gui );
                NUtils.waitEvent (()->gui.getInventory ().getItem ( new NAlias ( "rabbit-dead" ) )!=null,delay  );
                /// Снимаем шкуру
                WItem rabbit_dead = gui.getInventory ().getItem ( new NAlias ( "rabbit-dead" ) );
                if ( rabbit_dead == null ) {
                    return new Results ( Results.Types.NO_ITEMS );
                }
                new SelectFlowerAction ( rabbit_dead, "Flay", SelectFlowerAction.Types.Inventory ).run ( gui );
                NUtils.waitEvent (()->gui.getInventory ().getItem ( new NAlias ( "rabbit-carcas" ) )!=null,delay  );
                NUtils.waitEvent (()->gui.getInventory ().getItem ( TransferRawHides.raw_hides )!=null,delay  );
                /// Сбрасываем шкуру
                new TransferRawHides ().run ( gui );
                /// Свежуем зайца
                WItem carcas = gui.getInventory ().getItem ( new NAlias ( "rabbit-carcas" ) );
                if ( carcas == null ) {
                    return new Results ( Results.Types.NO_ITEMS );
                }
                new SelectFlowerAction ( carcas, "Clean", SelectFlowerAction.Types.Inventory ).run ( gui );
                NUtils.waitEvent (()->gui.getInventory ().getItem ( new NAlias ( "rabbit-clean" ) )!=null,delay  );
                NUtils.waitEvent (()->gui.getInventory ().getItem ( new NAlias ( "entrails" ) )!=null,delay);
                new TransferTrash ().run ( gui );
                /// Разделываем зайца
                WItem rabbit_cleaned = gui.getInventory ().getItem ( new NAlias ( "rabbit-clean" ) );
                if ( rabbit_cleaned == null ) {
                    return new Results ( Results.Types.NO_ITEMS );
                }
                new SelectFlowerAction ( rabbit_cleaned, "Butcher", SelectFlowerAction.Types.Inventory ).run ( gui );
                NUtils.waitEvent (()->gui.getInventory ().getItem ( new NAlias ( "bone" ) )!=null,100  );
                NUtils.waitEvent (()->gui.getInventory ().getItem ( new NAlias ( "meat" ) )!=null,delay  );
                new TransferBones ().run ( gui );
                new TransferMeat ().run ( gui );
        
            }
        }
        
        /// Собираем зайчат и переносим
        new TransferFromContainerToContainer ( 45, new NAlias ( new ArrayList<> ( Arrays.asList ( "bunny" ) ) ),
                AreasID.rabbit, AreasID.bunny ).run ( gui );

       
        return new Results ( Results.Types.SUCCESS );
    }
    
}

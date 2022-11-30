package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import haven.WItem;
import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class ChickenMaster implements Action {
    private class ChickenCoop {
        /// Курятник
        Gob gob;
        /// Качество петушка
        double quality;
        /// Массив кур
        ArrayList<Double> hens = new ArrayList<> ();
        
        public ChickenCoop (
                Gob gob,
                double quality
        ) {
            this.gob = gob;
            this.quality = quality;
        }
    }

    private class KFC_chicken_Q {
        /// Курятник
        Gob gob;
        /// Качество курочкии
        double quality;

        public KFC_chicken_Q (
                Gob gob,
                double quality
        ) {
            this.gob = gob;
            this.quality = quality;
        }
    }

    Comparator<KFC_chicken_Q> comparator3 = new Comparator<KFC_chicken_Q> () {
        @Override
        public int compare (
                KFC_chicken_Q o1,
                KFC_chicken_Q o2
        ) {
            return Double.compare ( o1.quality, o2.quality );
        }
    };
    
    Comparator<ChickenCoop> comparator1 = new Comparator<ChickenCoop> () {
        @Override
        public int compare (
                ChickenCoop o1,
                ChickenCoop o2
        ) {
            int res = Double.compare ( o1.quality, o2.quality );
            if ( res == 0 ) {
                if ( o1.hens.size () > 0 && o2.hens.size () > 0 ) {
                    double s1 = 0;
                    for ( Double h1 : o1.hens ) {
                        s1 += h1;
                    }
                    double s2 = 0;
                    for ( Double h2 : o2.hens ) {
                        s2 += h2;
                    }
                    s1 /= o1.hens.size ();
                    s2 /= o2.hens.size ();
                    res = Double.compare ( s1, s2 );
                }
            }
            return res;
        }
    };
    
    private class EggsInfo {
        double quality;
        
        Gob gob;
        
        public EggsInfo (
                double quality,
                Gob gob
        ) {
            this.quality = quality;
            this.gob = gob;
        }
    }
    
    Comparator<EggsInfo> comparator2 = new Comparator<EggsInfo> () {
        @Override
        public int compare (
                EggsInfo o1,
                EggsInfo o2
        ) {
            return Double.compare ( o1.quality, o2.quality );
        }
    };
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        ArrayList<Gob> hh = Finder.findObjectsInArea ( new NAlias( "chickencoop" ),
                Finder.findNearestMark ( AreasID.hens ) );
        ArrayList<Gob> chicken_inc = Finder.findObjectsInArea  ( new NAlias ( "chickencoop" ), Finder.findNearestMark ( AreasID.chicken ) );
        ArrayList<ChickenCoop> chickens = new ArrayList<ChickenCoop> ();
        
        ArrayList<EggsInfo> eggs = new ArrayList<EggsInfo> ();
        for ( Gob gob : hh ) {
            new PathFinder( gui, gob ).run ();
            if ( new OpenTargetContainer ( gob, "Chicken Coop" ).run ( gui ).type != Results.Types.SUCCESS ) {
                return new Results ( Results.Types.OPEN_FAIL );
            }
            NUtils.waitEvent ( () -> false, 5 );
            WItem curroost = gui.getInventory ( "Chicken Coop" ).getItem ( new NAlias ( "roost" ) );
            ChickenCoop currentChickenCoop = new ChickenCoop ( gob, NUtils.getWItemQuality ( curroost ) );
            
            /// Получаем инфо по курочкам
            ArrayList<WItem> curhens = gui.getInventory ( "Chicken Coop" ).getItems ( new NAlias ( "hen" ) );
            for ( WItem hen : curhens ) {
                currentChickenCoop.hens.add ( NUtils.getWItemQuality ( hen ) );
            }
            currentChickenCoop.hens.sort ( Double::compareTo );
            chickens.add ( currentChickenCoop );
            
            /// Получаем инфо по яйкам
            ArrayList<WItem> cureggs = gui.getInventory ( "Chicken Coop" ).getItems ( new NAlias ( "egg" ) );
            for ( WItem egg : cureggs ) {
                eggs.add ( new EggsInfo ( NUtils.getWItemQuality ( egg ), gob ) );
            }
        }
        eggs.sort ( comparator2 );
        chickens.sort ( comparator1 );
        ArrayList<KFC_chicken_Q> qhens = new ArrayList<> ();
        ArrayList<KFC_chicken_Q> qcocks = new ArrayList<> ();

        for(Gob inc: chicken_inc) {
            new PathFinder(gui, inc).run();
            if (new OpenTargetContainer(inc, "Chicken Coop").run(gui).type != Results.Types.SUCCESS) {
                return new Results(Results.Types.OPEN_FAIL);
            }
            NUtils.waitEvent(() -> false, 5);
            /// Получаем инфо по курочкам
            ArrayList<WItem> curhens = gui.getInventory("Chicken Coop").getItems(new NAlias("hen"));
            for (WItem hen : curhens) {
                qhens.add(new KFC_chicken_Q(inc,NUtils.getWItemQuality(hen)));
            }
            /// Получаем инфо по петушкам
            ArrayList<WItem> curroost = gui.getInventory("Chicken Coop").getItems(new NAlias("roost"));
            for (WItem roost : curroost) {
                qcocks.add(new KFC_chicken_Q(inc,NUtils.getWItemQuality(roost)));
            }
        }

        qhens.sort (comparator3 );
        qcocks.sort ( comparator3 );

        /// Цикл петушков
        for ( int i = qcocks.size () - 1 ; i >= 0 ; i -= 1 ) {
            new PathFinder(gui, qcocks.get(i).gob).run();
            if (new OpenTargetContainer(qcocks.get(i).gob, "Chicken Coop").run(gui).type != Results.Types.SUCCESS) {
                return new Results(Results.Types.OPEN_FAIL);
            }
            NUtils.waitEvent(() -> gui.getInventory("Chicken Coop") != null, 20);
            double current_quality = qcocks.get(i).quality;
            NUtils.transferItem(gui.getInventory("Chicken Coop"),
                    gui.getInventory("Chicken Coop").getItem(current_quality, new NAlias("roost")));

            for (int j = chickens.size() - 1; j >= 0; j -= 1) {
                if (chickens.get(j).quality < current_quality) {
                    new PathFinder(gui, chickens.get(j).gob).run();
                    if (new OpenTargetContainer(chickens.get(j).gob, "Chicken Coop").run(gui).type !=
                            Results.Types.SUCCESS) {
                        return new Results(Results.Types.OPEN_FAIL);
                    }
                    NUtils.waitEvent ( () -> gui.getInventory ( "Chicken Coop" )!=null,200);
                    int finalJ = j;
                    NUtils.waitEvent ( () -> gui.getInventory("Chicken Coop")
                            .getItem(chickens.get(finalJ).quality, new NAlias("roost"))!=null, 20 );
                    WItem lqhen = gui.getInventory("Chicken Coop")
                            .getItem(chickens.get(j).quality, new NAlias("roost"));
                    Coord pos = new Coord((lqhen.c.x - 1) / 33, (lqhen.c.y - 1) / 33);
                    NUtils.transferItem(gui.getInventory("Chicken Coop"), lqhen);
                    NUtils.takeItemToHand(
                            gui.getInventory().getItem(current_quality, new NAlias("roost")).item);
                    int counter = 0;
                    while (gui.vhand == null && counter < 20) {
                        Thread.sleep(100);
                        counter += 1;
                    }
                    NUtils.transferToInventory("Chicken Coop", pos);
                    chickens.get(j).quality = current_quality;
                    current_quality = NUtils.getWItemQuality(lqhen);
                }
            }

            NUtils.waitEvent(()-> gui.getInventory().getItem(new NAlias("roost"))!=null,200);
            /// Сворачиваем шею петуху
            WItem roost = gui.getInventory().getItem(new NAlias("roost"));
            if (roost == null) {
                return new Results(Results.Types.NO_ITEMS);
            }
            new SelectFlowerAction(roost, "Wring neck", SelectFlowerAction.Types.Inventory).run(gui);
            NUtils.waitEvent(()-> gui.getInventory().getItem(new NAlias("rooster-dead"))!=null,200);
            /// Ощипываем
            WItem roost_dead = gui.getInventory().getItem(new NAlias("rooster-dead"));
            if (roost_dead == null) {
                return new Results(Results.Types.NO_ITEMS);
            }
            new SelectFlowerAction(roost_dead, "Pluck", SelectFlowerAction.Types.Inventory).run(gui);
            NUtils.waitEvent(()-> gui.getInventory().getItem(new NAlias("chicken-plucked"))!=null,200);
            NUtils.waitEvent(()-> gui.getInventory().getItems(new NAlias("feather")).size()>=3,200);

            /// Сбрасываем перья

            ArrayList<WItem> items = gui.getInventory()
                    .getItems(new NAlias(new ArrayList<String>(Arrays.asList("feather"))),
                            AreasID.getTh(AreasID.feather), false);
            /// Переносим предметы в инвентарь

            for (WItem item : items) {
                NUtils.drop(item);
            }
            if (Finder.findObjectInArea(new NAlias("barter"), 1000, Finder.findNearestMark(AreasID.feather)) != null) {
                new TransferItemsToBarter(AreasID.feather,new NAlias("feather"), false, AreasID.getTh(AreasID.feather)).run(gui);
            } else {
                new TransferToPile(AreasID.feather, NHitBox.get(), new NAlias("stockpile"),
                        new NAlias("feather")).run(gui);
            }
            /// Свежуем курицу
            WItem chicken_plucked = gui.getInventory().getItem(new NAlias("chicken-plucked"));
            if (chicken_plucked == null) {
                return new Results(Results.Types.NO_ITEMS);
            }
            new SelectFlowerAction(chicken_plucked, "Clean", SelectFlowerAction.Types.Inventory).run(gui);
            NUtils.waitEvent(()-> gui.getInventory().getItem(new NAlias("entrails"))!=null,200);
            new TransferTrash().run(gui);
            /// Разделываем курицу
            WItem chicken_cleaned = gui.getInventory().getItem(new NAlias("chicken-cleaned"));
            if (chicken_cleaned == null) {
                return new Results(Results.Types.NO_ITEMS);
            }
            new SelectFlowerAction(chicken_cleaned, "Butcher", SelectFlowerAction.Types.Inventory).run(gui);
            NUtils.waitEvent(()-> gui.getInventory().getItem(new NAlias("bone"))!=null,200);
            NUtils.waitEvent(()-> gui.getInventory().getItem(new NAlias("meat"))!=null,200);
            new TransferBones().run(gui);
            new TransferMeat().run(gui);

        }

        for ( int i = qhens.size () - 1 ; i >= 0 ; i -= 1 ) {
            new PathFinder ( gui, qhens.get(i).gob ).run ();
            if ( new OpenTargetContainer ( qhens.get(i).gob, "Chicken Coop" ).run ( gui ).type != Results.Types.SUCCESS ) {
                return new Results ( Results.Types.OPEN_FAIL );
            }
            NUtils.waitEvent ( () -> gui.getInventory ( "Chicken Coop" ) != null, 60 );
            double current_quality = qhens.get(i).quality;
            NUtils.transferItem ( gui.getInventory ( "Chicken Coop" ),
                    gui.getInventory ( "Chicken Coop" ).getItem ( current_quality, new NAlias ( "hen" ) ) );

            for ( int j = chickens.size () - 1 ; j >= 0 ; j -= 1 ) {
                for ( int k = 0 ; k < chickens.get ( j ).hens.size () ; k += 1 ) {
                    if ( chickens.get ( j ).hens.get ( k ) < current_quality ) {
                        new PathFinder ( gui, chickens.get ( j ).gob ).run ();
                        if ( new OpenTargetContainer ( chickens.get ( j ).gob, "Chicken Coop" ).run ( gui ).type !=
                                Results.Types.SUCCESS ) {
                            return new Results ( Results.Types.OPEN_FAIL );
                        }
                        NUtils.waitEvent ( () -> gui.getInventory ( "Chicken Coop" )!=null,200);
                        int finalJ = j;
                        int finalK = k;
                        NUtils.waitEvent ( () -> gui.getInventory ( "Chicken Coop" )
                                .getItem ( chickens.get (finalJ).hens.get (finalK), new NAlias ( "hen" ) )!=null, 20 );
                        WItem lqhen = gui.getInventory ( "Chicken Coop" )
                                         .getItem ( chickens.get ( j ).hens.get ( k ), new NAlias ( "hen" ) );
                        Coord pos = new Coord ( ( lqhen.c.x - 1 ) / 33, ( lqhen.c.y - 1 ) / 33 );
                        NUtils.transferItem ( gui.getInventory ( "Chicken Coop" ), lqhen );
                        NUtils.takeItemToHand (
                                gui.getInventory ().getItem ( current_quality, new NAlias ( "hen" ) ).item );
                        int counter = 0;
                        while ( gui.vhand == null && counter < 20 ) {
                            Thread.sleep ( 100 );
                            counter += 1;
                        }
                        NUtils.transferToInventory ( "Chicken Coop", pos );
                        current_quality = NUtils.getWItemQuality ( lqhen );
                        /// Обновляем данные по курочкам
                        Thread.sleep ( 300 );
                        ArrayList<WItem> newcurhens = gui.getInventory ( "Chicken Coop" )
                                                         .getItems ( new NAlias ( "hen" ) );
                        chickens.get ( j ).hens = new ArrayList<> ();
                        for ( WItem hen : newcurhens ) {
                            chickens.get ( j ).hens.add ( NUtils.getWItemQuality ( hen ) );
                        }
                        chickens.get ( j ).hens.sort ( Double::compareTo );
                        break;
                    }
                }
            }
            NUtils.waitEvent(()-> gui.getInventory().getItem(new NAlias("hen"))!=null,200);
            /// Сворачиваем шею курице
            WItem hen = gui.getInventory ().getItem ( new NAlias ( "hen" ) );
            if ( hen == null ) {
                return new Results ( Results.Types.NO_ITEMS );
            }
            new SelectFlowerAction ( hen, "Wring neck", SelectFlowerAction.Types.Inventory ).run ( gui );

            NUtils.waitEvent(()-> gui.getInventory().getItem(new NAlias("hen-dead"))!=null,200);
            /// Ощипываем
            WItem hen_dead = gui.getInventory ().getItem ( new NAlias ( "hen-dead" ) );
            if ( hen_dead == null ) {
                return new Results ( Results.Types.NO_ITEMS );
            }
            new SelectFlowerAction ( hen_dead, "Pluck", SelectFlowerAction.Types.Inventory ).run ( gui );
            NUtils.waitEvent(()-> gui.getInventory().getItems(new NAlias("feather")).size()>=3,200);
            /// Сбрасываем перья
            ArrayList<WItem> items = gui.getInventory()
                    .getItems(new NAlias(new ArrayList<String>(Arrays.asList("feather"))),
                            AreasID.getTh(AreasID.feather), false);
            /// Переносим предметы в инвентарь

            for (WItem item : items) {
                NUtils.drop(item);
            }
            if (Finder.findObjectInArea(new NAlias("barter"), 1000, Finder.findNearestMark(AreasID.feather)) != null) {
                new TransferItemsToBarter(AreasID.feather,new NAlias("feather"), false, AreasID.getTh(AreasID.feather)).run(gui);
            } else {
                new TransferToPile(AreasID.feather, NHitBox.get(), new NAlias("stockpile"),
                        new NAlias("feather")).run(gui);
            }
            NUtils.waitEvent(()-> gui.getInventory().getItem(new NAlias("chicken-plucked"))!=null,200);
            /// Свежуем курицу
            WItem chicken_plucked = gui.getInventory ().getItem ( new NAlias ( "chicken-plucked" ) );
            if ( chicken_plucked == null ) {
                return new Results ( Results.Types.NO_ITEMS );
            }
            new SelectFlowerAction ( chicken_plucked, "Clean", SelectFlowerAction.Types.Inventory ).run ( gui );
            NUtils.waitEvent(()-> gui.getInventory().getItem(new NAlias("entrails"))!=null,200);

            new TransferTrash ().run ( gui );
            /// Разделываем курицу
            WItem chicken_cleaned = gui.getInventory ().getItem ( new NAlias ( "chicken-cleaned" ) );
            if ( chicken_cleaned == null ) {
                return new Results ( Results.Types.NO_ITEMS );
            }
            new SelectFlowerAction ( chicken_cleaned, "Butcher", SelectFlowerAction.Types.Inventory ).run ( gui );
            NUtils.waitEvent(()-> gui.getInventory().getItem(new NAlias("bone"))!=null,200);
            new TransferBones ().run ( gui );
            new TransferMeat ().run ( gui );

        }

        /// Собираем циплят и переносим
        new TransferFromContainerToContainer ( 20, new NAlias ( new ArrayList<> ( Arrays.asList ( "chick" ) ),
                new ArrayList<> ( Arrays.asList ( "egg" ) ) ), AreasID.hens, AreasID.chicken ).run ( gui );

        /// Выясняем пороговое качество для яиц
        new PathFinder ( gui, chickens.get ( chickens.size () - 1 ).gob ).run ();
        if ( new OpenTargetContainer ( chickens.get ( chickens.size () - 1 ).gob, "Chicken Coop" ).run ( gui ).type !=
                Results.Types.SUCCESS ) {
            return new Results ( Results.Types.OPEN_FAIL );
        }
        NUtils.waitEvent ( () -> false, 5 );
        /// Получаем инфо по курочкам
        ArrayList<WItem> tophens = gui.getInventory ( "Chicken Coop" ).getItems ( new NAlias ( "hen" ) );
        ArrayList<Double> qtop = new ArrayList<> ();
        for ( WItem top : tophens ) {
            qtop.add ( NUtils.getWItemQuality ( top ) );
        }
        qtop.sort ( Double::compareTo );
        
        gui.msg ( String.valueOf ( qtop.get ( 0 ) ) );

        double chicken_th = qtop.get ( 0 );


        if ( Finder.findObjectInArea ( new NAlias ( "barter" ), 1000, Finder.findNearestMark ( AreasID.meat4 ) ) !=
                null ) {
            new TransferFromContainerToBarter ( new NAlias("egg"), AreasID.hens, AreasID.eggs, chicken_th, false, false ).run ( gui );
        }
        else {
            new TransferFromContainerToContainer ( new NAlias ( new ArrayList<> ( Arrays.asList ( "egg" ) ) ),
                    AreasID.hens, AreasID.eggs, chicken_th, false ).run ( gui );
            
        }
        return new Results ( Results.Types.SUCCESS );
    }
    
}

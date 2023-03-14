package nurgling;

import haven.*;
import nurgling.bots.*;
import nurgling.bots.build.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс виджета управления ботами
 */
public class NBotsInfo extends Widget implements KeyBinding.Bindable {
    NSButton back;

    @Override
    public void draw(GOut g) {
        super.draw(g);
        if(NUtils.getGameUI().map!=null) {
            if (NUtils.getUI().dragged != null) {
                Tex dt = new TexI(NUtils.getUI().dragged.cont);
                ui.drawafter(new UI.AfterDraw() {
                    public void draw(GOut g) {
                        g.image(dt, ui.mc.add(dt.sz().div(2).inv()));
                    }
                });
            }
        }
    }

    /**
     * Конструктор
     *
     * @param gui Интерфейс клиента
     */
    public NBotsInfo(NGameUI gui ) {
        /// Устанавливаем размеры виджета
        super ( new Coord ( 150, 800 ) );
        
        /// Положение элементов по вертикали
        int y = 0;
        
        try {
            back = add(new NSButton(32, 32, Resource.local().load("bots/icons/back")){
                @Override
                public void click () {
                    super.click ();
                    showLayouts();
                }
            });
            back.hide();
            layouts.put ( 0, new Layout ( this, "bots/icons/forest" ) );
            layouts.get ( 0 ).add ( new NButton ( "Chopper", new Chopper( gui ), "bots/icons/block" ) );
            layouts.get ( 0 ).add ( new NButton ( "Bark Collection", new BarkCollector( gui ),
                    "bots/icons/bark" ) );
            layouts.get ( 0 ).add ( new NButton ( "Bough Collection", new BoughCollector( gui ),
                    "bots/icons/bough" ) );
            layouts.get ( 0 ).add ( new NButton ( "Leaf Collection", new LeafCollector( gui ),
                    "bots/icons/leaf" ) );
            layouts.get ( 0 ).add ( new NButton ( "Nuts Collection", new NutsCollector ( gui ), "bots/icons/nuts" ) );
            layouts.get ( 0 ).add ( new NButton ( "Fruits Collection", new FruitCollector ( gui ), "bots/icons/fruit" ) );
            layouts.get ( 0 ).add ( new NButton ( "Chipper", new Chipper ( gui ), "bots/icons/chip" ) );
            layouts.get ( 0 ).add ( new NButton ( "Clay Coollector", new ClayCollector ( gui ), "bots/icons/clay" ) );
            layouts.get ( 0 ).add ( new NButton ( "Sand Coollector", new SandCollector ( gui ), "bots/icons/sand" ) );
            //layouts.get ( 0 ).add ( new NButton ( "Fishing", new Fisher ( gui ), "bots/icons/fishing" ) );
            layouts.get ( 0 ).add ( new NButton ( "Plow", new Plower ( gui ), "bots/icons/plow" ) );
            layouts.get ( 0 ).add ( new NButton ( "Cattail", new CattailPicker ( gui ),"bots/icons/cattail" ) );
            layouts.get ( 0 ).add ( new NButton ( "Butcher", new Butcher ( gui ),
                    "bots/icons/butcher") );
            layouts.get ( 0 ).add ( new NButton ( "Truffle", new Truffle( gui ),
                    "bots/icons/truffle") );
//            layouts.get ( 0 ).add ( new NButton ( "ButcherSupport", new nurgler.bots.resources.ButcherSupport ( gui ),
//                    Special.getPath () + "/icons/butcherSupport.png" ) );

            layouts.put ( 2, new Layout ( this, "bots/icons/frame" ) );
            layouts.get ( 2 ).add ( new NButton ( "Block & Board", new BlockAndBoard( gui ), "bots/icons/blockboards" ) );
            layouts.get ( 2 ).add ( new NButton ( "DryerSeed", new SeedDryer ( gui ),
                    "bots/icons/www" ) );
            layouts.get ( 2 ).add ( new NButton ( "Dryer", new Dryer ( gui ),
                    "bots/icons/hide" ) );
            layouts.get ( 2 ).add ( new NButton ( "TarKiln", new TarKilnRefiller ( gui ),
                    "bots/icons/tarkiln" ) );
            layouts.get ( 2 ).add ( new NButton ( "Smelter", new Smelter ( gui ),
                    "bots/icons/smelter" ) );
            layouts.get ( 2 ).add ( new NButton ( "FriedFish", new FriedFish ( gui ),
                    "bots/icons/friedfish" ) );
            layouts.get ( 2 ).add ( new NButton ( "Brick", new BrickMaker ( gui ),
                    "bots/icons/brick" ) );
            layouts.get ( 2 ).add ( new NButton ( "DryFish", new FishDryer ( gui ),
                    "bots/icons/driedfish" ) );
            layouts.get ( 2 ).add ( new NButton ( "BranchAsh", new BranchAsh( gui ),
                    "bots/icons/branchash" ) );
            layouts.get ( 2 ).add ( new NButton ( "Tanning", new Tanning ( gui ),
                    "bots/icons/tanning" ) );
            layouts.get ( 2 ).add ( new NButton ( "Branch", new BranchMaker (  gui ),"bots/icons/branch" ) );


//            layouts.get ( 2 ).add ( new NButton ( "GardenPot", new Gardener ( gui ),
//                    Special.getPath () + "/icons/gardenpot.png" ) );
//            layouts.get ( 2 ).add ( new NButton ( "UGardenPot", new GardenPotMaker ( gui ),
//                    Special.getPath () + "/icons/ugardenpot.png" ) );

//            layouts.get ( 2 ).add ( new NButton ( "Silk Maker", new SilkMaker ( gui ),
//                    Special.getPath () + "/icons/silk.png" ) );
//            layouts.get ( 2 ).add ( new NButton ( "Ash", new BoneAsh ( gui ),
//                    Special.getPath () + "/icons/ash.png" ) );
//            layouts.get ( 2 ).add ( new NButton ( "BoiledEgg", new EggBoiled ( gui ),
//                    Special.getPath () + "/icons/boiledegg.png" ) );
//            layouts.get ( 2 ).add ( new NButton ( "lyeMaker", new LyeMaker ( gui ),
//                    Special.getPath () + "/icons/lye.png" ) );
//            layouts.get ( 2 ).add ( new NButton ( "BoneAsh2", new BoneAsh2 ( gui ),
//                    Special.getPath () + "/icons/boneash.png" ) );
//            layouts.get ( 2 ).add ( new NButton ( "Wax", new WaxCollector ( gui ),
//                    Special.getPath () + "/icons/wax.png" ) );

//            layouts.get ( 2 ).add ( new NButton ( "Glass", new Glass ( gui ),
//                    Special.getPath () + "/icons/glass.png" ) );

//
//
            layouts.put ( 3, new Layout ( this, "bots/icons/craft" ) );
            layouts.get ( 3 ).add ( new NButton ( "Craft Rope", new CreaterRope (  gui ),
                    "bots/icons/rope" ) );
            layouts.get ( 3 ).add ( new NButton ( "Craft Cloth", new CreaterCloth ( gui ),
                    "bots/icons/cloth" ) );
            layouts.get ( 3 ).add ( new NButton ( "Craft Tarsticks", new TarSticker ( gui ),
                    "bots/icons/tarsticks" ) );
//            layouts.get ( 3 ).add ( new NButton ( "Bone Glue", new nurgler.bots.crafting.BoneGlue (  gui ),
//                    Special.getPath () + "/icons/boneglue.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "Harden Leather", new HardenLeatherMaker (  gui ),
//                    Special.getPath () + "/icons/harden.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "Bronze", new nurgler.bots.crafting.Bronze (  gui ),
//                    Special.getPath () + "/icons/bronze.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "Whought Iron", new FineryForger (  gui ),
//                    Special.getPath () + "/icons/ff.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "Tar Stick", new TarSticker (  gui ),
//                    Special.getPath () + "/icons/tarstick.png" ) );

//            layouts.get ( 3 ).add ( new NButton ( "StitchedCasing", new StichedCasingMaker (  gui ),
//                    Special.getPath () + "/icons/stitched.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "ButterMaker", new ButterMaker (  gui ),
//                    Special.getPath () + "/icons/butter.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "GelatinMaker", new GelatinMaker (  gui ),
//                    Special.getPath () + "/icons/gelatin.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "BClay", new CreaterBoneClay (  gui ),
//                    Special.getPath () + "/icons/bclay.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "PClay", new CreaterPotterClay (  gui ),
//                    Special.getPath () + "/icons/pclay.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "FeltM", new FeltMaker (  gui ),
//                    Special.getPath () + "/icons/felt.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "RedPigment", new RedPigment (  gui ),
//                    Special.getPath () + "/icons/pigment.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "EasterEgg", new EasterEggs (  gui ),
//                    Special.getPath () + "/icons/easter.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "JellyHearth", new JellyHearths (  gui ),
//                    Special.getPath () + "/icons/hearth.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "SeerSpindle", new SpindleMaker (  gui ),
//                    Special.getPath () + "/icons/spindle.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "TinyAbacus", new TinyAbacus (  gui ),
//                    Special.getPath () + "/icons/abacus.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "ShinyMarbles", new ShinyMarbles (  gui ),
//                    Special.getPath () + "/icons/shiny.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "SnowGlobes", new SnowGlobes (  gui ),
//                    Special.getPath () + "/icons/snowglobe.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "SeerStones", new SeerStone (  gui ),
//                    Special.getPath () + "/icons/sstones.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "FossilCollection", new FossilCollection (  gui ),
//                    Special.getPath () + "/icons/fossil.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "PotentRod", new PotentRod (  gui ),
//                    Special.getPath () + "/icons/potent.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "DeepSeaAtavism", new DeepSea (  gui ),
//                    Special.getPath () + "/icons/deepsea.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "BearToy", new BearMaker (  gui ),
//                    Special.getPath () + "/icons/bear.png" ) );
//            layouts.get ( 3 ).add ( new NButton ( "CandleMaker", new CandleMaker (  gui ),
//                    Special.getPath () + "/icons/candle.png" ) );
//
            //layouts.put ( 4, new Layout ( this, "bots/icons/cooking" ) );
            //layouts.get ( 4 ).add ( new NButton ( "Cabbage", new CabbageMaker (  gui ),
            //        "bots/icons/cabbagemaker" ) );

//            layouts.get ( 4 ).add ( new NButton ( "SausagesMaker", new SausageMaker (  gui ),
//                    Special.getPath () + "/icons/sausages.png" ) );
//            layouts.get ( 4 ).add ( new NButton ( "SmokedMaker", new SmokedMaker (  gui ),
//                    Special.getPath () + "/icons/smoked.png" ) );
//            layouts.get ( 4 ).add ( new NButton ( "Backer", new BackerMaker (  gui ),
//                    Special.getPath () + "/icons/backer.png" ) );
//            layouts.get ( 4 ).add ( new NButton ( "Steak", new SteakMaker (  gui ),
//                    Special.getPath () + "/icons/steak.png" ) );
//
//
            layouts.put ( 5, new Layout ( this, "bots/icons/farming" ) );
            layouts.get ( 5 ).add ( new NButton ( "Carrot", new FarmerCarrot (  gui ),
                    "bots/icons/carrot" ) );
            layouts.get ( 5 ).add ( new NButton ( "Flax", new FarmerFlax (  gui ),
                    "bots/icons/flax" ) );
            layouts.get ( 5 ).add ( new NButton ( "Pigs", new Pigs ( gui ),
                    "bots/icons/pigs" ) );
            layouts.get ( 5 ).add ( new NButton ( "Ochs", new Cows ( gui ),
                    "bots/icons/cows" ) );
            layouts.get ( 5 ).add ( new NButton ( "Sheeps", new Sheeps ( gui ),
                    "bots/icons/sheeps" ) );
            layouts.get ( 5 ).add ( new NButton ( "Goats", new Goats ( gui ),
                    "bots/icons/goats" ) );
            layouts.get ( 5 ).add ( new NButton ( "KFC", new KFC ( gui ),
                    "bots/icons/kfc" ) );
            layouts.get ( 5 ).add ( new NButton ( "Brander", new BrandedBot ( gui ),
                    "bots/icons/brander" ) );
            layouts.get ( 5 ).add ( new NButton ( "RabbitM", new RabbitMaster ( gui ),
                    "bots/icons/rabbit" ) );
            layouts.get ( 5 ).add ( new NButton ( "Horses", new Horses ( gui ),
                    "bots/icons/horses" ) );
//            layouts.put ( 5, new Layout ( this, Special.getPath () + "/icons/farming.png" ) );
//            layouts.get ( 5 ).add ( new NButton ( "FarmerQ", new FarmerQ (  gui ),
//                    Special.getPath () + "/icons/farmerq.png" ) );
//            layouts.get ( 5 ).add ( new NButton ( "PepperSeeder", new PepperSeeder (  gui ),
//                    Special.getPath () + "/icons/peppers.png" ) );
//            layouts.get ( 5 ).add ( new NButton ( "HopsSeeder", new HopsSeeder (  gui ),
//                    Special.getPath () + "/icons/hops.png" ) );
//            layouts.get ( 5 ).add ( new NButton ( "GrapeSeeder", new GrapeSeeder (  gui ),
//                    Special.getPath () + "/icons/grapes.png" ) );
//            layouts.get ( 5 ).add ( new NButton ( "Wheat", new FarmerWheat (  gui ),
//                    Special.getPath () + "/icons/wheat.png" ) );
//            layouts.get ( 5 ).add ( new NButton ( "Poppy", new FarmerPoppy (  gui ),
//                    Special.getPath () + "/icons/poppy.png" ) );
//            layouts.get ( 5 ).add ( new NButton ( "Turnip", new FarmerTurneps (  gui ),
//                    Special.getPath () + "/icons/turnip.png" ) );
//            layouts.get ( 5 ).add ( new NButton ( "PepperH", new PepperHarvester (  gui ),
//                    Special.getPath () + "/icons/pepperh.png" ) );

            layouts.put (6, new Layout ( this, "bots/icons/build" ) );
            layouts.get ( 6 ).add ( new NButton ( "Trellis", new BuildTrellis( gui ), "bots/icons/trellisb" ) );
            layouts.get ( 6 ).add ( new NButton ( "Frame", new BuildFrames( gui ), "bots/icons/dframe" ) );
            layouts.get ( 6 ).add ( new NButton ( "Cupboard", new BuildCupboard( gui ), "bots/icons/cupboard" ) );
            layouts.get ( 6 ).add ( new NButton ( "MCabinet", new BuildMCabinet( gui ), "bots/icons/mcabinet" ) );
            layouts.get ( 6 ).add ( new NButton ( "Barrel", new BuildBarrels( gui ), "bots/icons/barrel" ) );
            layouts.get ( 6 ).add ( new NButton ( "CheeseR", new BuildCheaseRack( gui ), "bots/icons/cheeser" ) );
            layouts.get ( 6 ).add ( new NButton ( "TunningT", new BuildTTube( gui ), "bots/icons/tanningb" ) );
            layouts.get ( 6 ).add ( new NButton ( "WChest", new BuildWChest( gui ), "bots/icons/chest" ) );
            layouts.get ( 6 ).add ( new NButton ( "DSign", new BuildSign( gui ), "bots/icons/dsign" ) );
            layouts.get ( 6 ).add ( new NButton ( "BuildBeackon", new BuildBeacon( gui ), "bots/icons/beacon" ) );
            layouts.get ( 6 ).add ( new NButton ( "Tarkilns", new BuildTarkilns( gui ), "bots/icons/tarkilnb" ) );
            layouts.get ( 6 ).add ( new NButton ( "Crate", new BuildCrate( gui ), "bots/icons/crate" ) );
            layouts.get ( 6 ).add ( new NButton ( "SmokeShed", new BuildSShed( gui ), "bots/icons/smokedshed" ) );
            layouts.get ( 6 ).add ( new NButton ( "Destroyer", new Destroyer (  gui ), "bots/icons/destroyer" ) );
            layouts.get ( 6 ).add ( new NButton ( "Cellar", new DigCellar (  gui ), "bots/icons/cellar" ) );

            layouts.put ( 7, new Layout ( this, "bots/icons/cheese" ) );
            layouts.get ( 7 ).add ( new NButton ( "Shedule", new CheesedShedule (  gui ),
                    "bots/icons/shedule" ) );
            layouts.get ( 7 ).add ( new NButton ( "CowsCurd", new CurdCows (  gui ),
                    "bots/icons/cowscurd" ) );
            layouts.get ( 7 ).add ( new NButton ( "GoatsCurd", new CurdGoats (  gui ),
                    "bots/icons/goatscurd" ) );
            layouts.get ( 7 ).add ( new NButton ( "SheepsCurd", new CurdSheeps (  gui ),
                    "bots/icons/sheepscurd" ) );
            layouts.get ( 7 ).add ( new NButton ( "TransferTray", new TransferCheeseTray (  gui ),
                    "bots/icons/transfertray" ) );
            layouts.get ( 7 ).add ( new NButton ( "TransferIns", new TransferCheeseInside (  gui ),
                    "bots/icons/transfercheese_ins" ) );
            layouts.get ( 7 ).add ( new NButton ( "TransferOut", new TransferCheeseOutside (  gui ),
                    "bots/icons/transfercheese_out" ) );
            layouts.get ( 7 ).add ( new NButton ( "TransferMine", new TransferCheeseMine (  gui ),
                    "bots/icons/transfercheese_mine" ) );
            layouts.get ( 7 ).add ( new NButton ( "TransferCellar", new TransferCheeseCellar (  gui ),
                    "bots/icons/transfercheese_cel" ) );

            layouts.put ( 5000, new Layout ( this, "bots/icons/tools" ) );
            layouts.get ( 5000 ).add ( new NButton ( "TestBot", new TestBot ( gui ), "bots/icons/testbot") );
            layouts.get ( 5000 ).add ( new NButton ( "Timer",null,"bots/icons/timer"){
                @Override
                public void click() {
                    gui.timers.show();
                }
            }  );
            layouts.get ( 5000 ).add ( new NButton ( "LPExplorer", new LFExplorer (  gui ),"bots/icons/lpexplorer" ) );
            //layouts.get ( 5000 ).add ( new NButton ( "Calibration", new Calibration ( gui ),
            //        "bots/icons/calibr" ) );
            layouts.get ( 5000 ).add ( new NButton ( "NomadCalibr", new NomadCalibrator ( gui ),
                    "bots/icons/nomad" ) );
            layouts.get ( 5000 ).add ( new NButton ( "NomadCalibr2", new NomadCalibrator2 ( gui ),
                    "bots/icons/nomad2" ) );
            layouts.get ( 5000 ).add ( new NButton ( "Mining", new MineBot ( gui ),
                    "bots/icons/miner" ) );
            layouts.get ( 5000 ).add ( new NButton ( "SoilDest", new SoilDestroyer (  gui ),
                    "bots/icons/soildest" ) );
            layouts.get ( 5000 ).add ( new NButton ( "Backer", new Backer (  gui ),
                    "bots/icons/backerprep" ) );
            //layouts.get ( 5000 ).add ( new NButton ( "Cabbager", new Cabbager (  gui ),
            //        "bots/icons/cabbager" ) );
            layouts.get ( 5000 ).add ( new NButton ( "FLSmoked", new FLSmoked (  gui ),
                    "bots/icons/flsmoked" ) );
            layouts.get ( 5000 ).add ( new NButton ( "BattleBot", new BattleBot ( gui ),
                    "bots/icons/battle" ) );
            layouts.get ( 5000 ).add ( new NButton ( "ShieldSword", new EquipWaepon (  gui ),
                    "bots/icons/shieldsword" ) );
            layouts.get ( 5000 ).add ( new NButton ( "TanningFluid", new TanningFluid (  gui ),
                    "bots/icons/tanfluid" ) );
            layouts.get ( 5000 ).add ( new NButton ( "TransferLog", new TransferLogs (  gui ),
                    "bots/icons/log" ) );
            layouts.get ( 5000 ).add ( new NButton ( "JugWaterIn", new JugWaterIn (  gui ),
                    "bots/icons/glassjugin" ) );
            layouts.get ( 5000 ).add ( new NButton ( "JugWaterOut", new JugWaterOut (  gui ),
                    "bots/icons/glassjugout" ) );
            layouts.get ( 5000 ).add ( new NButton ( "CartOut", new CartOut (  gui ),
                    "bots/icons/cartout" ) );
            layouts.get ( 5000 ).add ( new NButton ( "SortAndTransferMeat", new SortAndTransferMeat (  gui ),
                    "bots/icons/transfersortmeat" ) );
            layouts.get ( 5000 ).add ( new NButton ( "SortAndTransferBars", new SortAndTransferBars (  gui ),
                    "bots/icons/transfersortbars" ) );
            layouts.get ( 5000 ).add ( new NButton ( "DigSnow", new SnowCleaner ( gui ),
                    "bots/icons/clearsnow"  ) );
            layouts.get ( 5000 ).add ( new NButton ( "Orca", new NomadFinderBot ( gui ),
                    "bots/icons/gobfinder"  ) );
            layouts.get ( 5000 ).add ( new NButton ( "FishSorter", new SortAndTransferFish ( gui ),
                    "bots/icons/fishsorter"  ) );
            layouts.get ( 5000 ).add ( new NButton ( "HideSorter", new SortAndTransferHides ( gui ),
                    "bots/icons/transfersorthides"  ) );
            layouts.get ( 5000 ).add ( new NButton ( "FillSteel", new FillSteel ( gui ),
                    "bots/icons/steelcrucible"  ) );
//            layouts.get ( 5000 ).add ( new NButton ( "BattleBot", new BattleBot ( gui ),
//                    Special.getPath () + "/icons/battle.png" ) );
//            layouts.get ( 5000 ).add ( new NButton ( "BattleBot2", new BattleAction2 ( gui ),
//                    Special.getPath () + "/icons/battle.png" ) );
//            layouts.get ( 5000 ).add ( new NButton ( "BattleMinion", new BattleMinionBot ( gui ),
//                    Special.getPath () + "/icons/minion.png" ) );

//            layouts.get ( 5000 ).add ( new NButton ( "Sort and Transfer Trash", new SortAndTransferTrash ( gui ),
//                    Special.getPath () + "/icons/trash.png" ) );

//            layouts.get ( 5000 ).add ( new NButton ( "NomadFinderBot", new NomadFinderBot (  gui ),
//                    Special.getPath () + "/icons/orca.png" ) );
            

            
            for ( HashMap.Entry<Integer, Layout> entry : layouts.entrySet () ) {
                add ( entry.getValue (), new Coord ( 0, y ) );
                y += 34;
                for ( Layout.ButtonPos buttonPos : entry.getValue ().buttons ) {
                    add ( buttonPos.button, buttonPos.coord );
                    buttonPos.button.hide ();
                }
            }
            //            add ( new NButton ( "Chopper", new Chopper ( gui ), Special.getPath () + "/image.png" ), new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "Block & Board", new BlockAndBoard ( gui ), Special.getPath () + "/image.png" ),
            //                    new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "BarkCollection", new BarkCollector ( gui ), Special.getPath () + "/image.png" ),
            //                    new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "BoneAsh", new BoneAsh2 ( gui ), Special.getPath () + "/image.png" ), new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "CreaterCloth", new CreaterCloth ( gui ), Special.getPath () + "/image.png" ),
            //                    new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "Pepper", new Pepper ( gui ), Special.getPath () + "/image.png" ), new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "GardenPot", new GardenPotMaker ( gui ), Special.getPath () + "/image.png" ),
            //                    new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "Sorter", new Sorter ( gui ), Special.getPath () + "/image.png" ), new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "Gardener", new Gardener ( gui ), Special.getPath () + "/image.png" ), new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "Tanning", new Tanning ( gui ), Special.getPath () + "/image.png" ), new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "Backer", new BackerMaker ( gui ), Special.getPath () + "/image.png" ), new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "Dryer", new Dryer ( gui ), Special.getPath () + "/image.png" ), new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "Plower", new Plower ( gui ), Special.getPath () + "/image.png" ), new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "SnowCleaner", new SnowCleaner ( gui ), Special.getPath () + "/image.png" ),
            //                    new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "Transportation", new Transportation ( gui ), Special.getPath () + "/image.png" ),
            //                    new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "Cabbage", new Cabbage ( gui ), Special.getPath () + "/image.png" ), new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "EarthDestr", new EarthDestroyer ( gui ), Special.getPath () + "/image.png" ),
            //                    new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "Butcher", new Butcher ( gui ), Special.getPath () + "/image.png" ), new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "SausagesMaker", new SausageMaker ( gui ), Special.getPath () + "/image.png" ),
            //                    new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "Calibration", new Calibration ( gui ), Special.getPath () + "/image.png" ),
            //                    new Coord ( 0, y += 32 ) );
            //            add ( new NButton ( "TestBot", new TestBot ( gui ), Special.getPath () + "/image.png" ), new Coord ( 0, y += 32 ) );
        }
        catch ( IOException e ) {
            e.printStackTrace ();
        }
        //        add ( new NButton ( 80, "Block & Board", new BlockBoard ( gui ) ), new Coord ( 0, y += 32 ) );
        //        add ( new NButton ( 80, "Lightning Bot", new LightningBot ( gui ) ), new Coord ( 0, y += 32 ) );
        //        add ( new NButton ( 80, "Pile Maker", new TestPileMaker ( gui ) ), new Coord ( 0, y += 32 ) );
        //        add ( new NButton ( 80, "Test Bot", new TestBot ( gui ) ), new Coord ( 0, y += 32 ) );
        //        add ( new NButton ( 80, "Light Service", new LightService ( gui ) ), new Coord ( 0, y += 32 ) );
        //        add ( new NButton ( 80, "HideMaker", new HideMaker ( gui ) ), new Coord ( 0, y += 32 ) );
        //        add ( new NButton ( 80, "Butcher", new Butcher ( gui ) ), new Coord ( 0, y += 32 ) );
        //        add ( new NButton ( 80, "Coal", new CoalCoollector ( gui ) ), new Coord ( 0, y += 32 ) );
        //        add ( new NButton ( 80, "GardenPot", new GardenPot ( gui ) ), new Coord ( 0, y += 32 ) );
        //        add ( new NButton ( 80, "Sort", new Sort ( gui ) ), new Coord ( 0, y += 32 ) );
        //        add ( new NButton ( 80, "Snow Cleaner", new SnowCleaner ( gui ) ), new Coord ( 0, y += 32 ) );
        //        add ( new NButton ( 80, "Farmer", new Farmer ( gui ) ), new Coord ( 0, y += 32 ) );
        //        add ( new NButton ( 80, "Backer", new Backer ( gui ) ), new Coord ( 0, y += 32 ) );
    }
    
    public void showLayouts(){
        for ( HashMap.Entry<Integer, Layout> entry : layouts.entrySet () ) {
            for ( Layout.ButtonPos buttonPos : entry.getValue ().buttons ) {
                buttonPos.button.hide ();
            }
            entry.getValue ().show ();
            back.hide();
        }
    }
    
    public void hideLayouts(){
        for ( HashMap.Entry<Integer, Layout> entry : layouts.entrySet () ) {
            entry.getValue ().hide ();
        }
    }
    
    @Override
    public KeyBinding getbinding ( Coord cc ) {
        return null;
    }





    /**
     * Класс кнопки активации бота
     */
    class NButton extends NSButton {

        @Override
        public boolean mousedown(Coord c, int button) {
            NUtils.getUI().pressed = this;
            return super.mousedown(c, button);
        }

        @Override
        public void click () {
            super.click ();
            parent.parent.showLayouts ();
            /// Проверяем был ли бот включен раньше
            if ( bots.containsKey ( name ) ) {
                /// Если бот уже активен, то он отключается
                bots.get ( name ).close ();
                /// И исключается из массива активных ботов
                bots.remove ( name );
            }
            /// Добавляем нового бота в массив активных ботов
            bots.put ( name, bot );
            /// Запускаем поток
            new Thread ( bot, name ).start ();
        }
        
        /**
         * Конструктор
         *
         * @param text Текст идентификатор
         * @param nbot Активируемый бот
         */
        public NButton (
                String text,
                Bot nbot,
                String path
        )
                throws IOException {
            super ( 32, 32, Resource.local().load(path) );
//            custom[i] = ui.gui.new PaginaBeltSlot(i, p);
            /// Текст отображается как на кнопке, так и служит ключем в таблице
            name = path;
            /// Сохраняем ссылку на бота
            bot = nbot;
            this.parent = parent;
        }
        
        /// Имя идентификатор
        String name;
        /// Бот
        Bot bot;
        
        public Layout parent;
    }
    
    class Layout extends NSButton {
        @Override
        public void click () {
            super.click ();
            for ( ButtonPos buttonPos : buttons ) {
                buttonPos.button.show ();
            }

            parent.hideLayouts ();
            back.show();
        }
        
        private class ButtonPos {
            NButton button;
            Coord coord;
            
            public ButtonPos (
                    NButton button,
                    Coord coord
            ) {
                this.button = button;
                this.coord = coord;
            }
        }
        
        public Layout (
                NBotsInfo parent,
                String path
        )
                throws IOException {
            super ( 32, 32, Resource.loadsimg(path));
            this.parent = parent;
        }
        
        void add ( NButton button ) {
            ButtonPos bp = new ButtonPos ( button, new Coord ( w * 34, h * 34 ) );
            bp.button.parent = this;
            buttons.add ( bp );
            if ( h > 8 ) {
                w += 1;
                h = 0;
            }
            else {
                h += 1;
            }
        }
        
        ;
        
        public ArrayList<ButtonPos> getButtons () {
            return buttons;
        }
        
        int w = 0;
        int h = 1;
        
        ArrayList<ButtonPos> buttons = new ArrayList<ButtonPos> ();
        
        NBotsInfo parent;
    }

    NButton find(String path){
        for(Integer key: layouts.keySet()){
            for(Layout.ButtonPos button: layouts.get(key).buttons){
                if(NUtils.checkName(path,button.button.name))
                    return button.button;
            }
        }
        return null;
    }

    NButton find(Resource res){
        for(Integer key: layouts.keySet()){
            for(Layout.ButtonPos button: layouts.get(key).buttons){
                if(button.button.name.equals(res.name))
                    return button.button;
            }
        }
        return null;
    }
    /// Ассоциативный массив хранения актвиных ботов
    private HashMap<String, Bot> bots = new HashMap<String, Bot> ();
    
    /// Ассоциативный массив хранения макетов иконок
    private HashMap<Integer, Layout> layouts = new HashMap<Integer, Layout> ();
}

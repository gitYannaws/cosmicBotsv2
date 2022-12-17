//import com.esotericsoftware.kryo.util.Null;
//import com.sun.tools.javac.jvm.Items;
import okhttp3.internal.Util;
import org.apache.commons.collections4.functors.WhileClosure;
import org.apache.tools.ant.types.resources.selectors.None;
import org.dreambot.api.Client;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.login.LoginUtility;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.data.GameState;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.Menu;
import java.time.LocalDateTime;
import java.util.*;
import java.time.LocalTime; // import the LocalTime class
import static java.lang.Thread.sleep;
import static org.dreambot.api.methods.interactive.Players.all;
import static org.dreambot.api.utilities.Logger.log;
//https://twitter.com/keyboardosrs/status/1196548086513983488?lang=en
// 93 hops per hr / 1.55 hops per minute / 390 hops for ~20 hrs
// 10 minutes to hop 57 worlds / ~9 loops / 90 minutes / 4.2 hours to recover to full hops

@ScriptManifest(author = "Swanny", description = "cosmicBots", name = "Cosmic Picker Bots2", category = Category.MONEYMAKING, version = 1.0)

public class TestScript extends AbstractScript {
    private List<Integer> worldList= Arrays.asList(301, 308, 316, 326, 335, 371, 379, 380, 382, 383, 384, 394, 397, 398, 399, 417, 418, 430, 431, 433, 434, 435, 436, 437,451, 452, 453, 454, 455, 456, 469, 470, 471, 475, 476, 483, 497, 498, 499, 500, 501, 537, 542, 543, 544, 545, 546, 547, 552, 553, 554, 555, 556, 557, 562, 563,571,575);
    List <World> worldList2 = Worlds.all(w -> w.isF2P() && !w.isBeta() && !w.isDeadmanMode() && !w.isFreshStart() && !w.isLastManStanding() && !w.isLeagueWorld() && !w.isHighRisk() && !w.isPVP() && !w.isTournamentWorld() && w.getMinimumLevel() == 0);
    Area cosmicArea = new Area(2944, 3903, 2957, 3890);
    long startTime = System.nanoTime();
    int totalCosmicCollected  = 0;
    List<Integer> filterWorlds = new ArrayList<Integer>();
//    String [] filterWorlds = {};
//    String [] finalWorlds = {};
    List<Integer> finalWorlds = new ArrayList<Integer>();
   // String [][] accounts = {{"almightyswan20@mail.com", "OldKyles10"}, {"almightyswan9@mail.com", "OldKyles10"}, {"almightyswan12@mail.com", "OldKyles10"},
   // {"almightyswan13@mail.com", "OldKyles10"}, {"almightyswan6@mail.com", "OldKyles10"},
   // {"almightyswan8@mail.com", "OldKyles10"}};
    String [][] accounts = {{"almightyswan19@mail.com", "OldKyles10"}, {"OldKyles10"}, {"almightyswan16@mail.com", "OldKyles10"},
           {"almightyswan17@mail.com", "OldKyles10"}, {"almightyswan18@mail.com", "OldKyles10"},
           {"almightyswan14@mail.com", "OldKyles10"}, {"almightyswan15@mail.com", "OldKyles10"}};

    @Override //Infinite loop
    public int onLoop() {
        for(int i=0; i < accounts.length; i++) {
            log("Logging into new accounts: " + accounts[i][0]);
            LoginUtility.login(accounts[i][0], accounts[i][1]);
            sleep(1000);
            LoginUtility.login(accounts[i][0], accounts[i][1]);
            Sleep.sleepUntil(()-> Client.getGameState() == GameState.LOGGED_IN, 30000);
            for (int j=1; j <= 15; j++) {
                hopWorlds(accounts[i][0]);
                calcProfit(j);
            }
            log("Logging out of this account");
            Tabs.logout();

        }
        return 100;
    }

    private void hopWorlds(String userAcc) {
        for (int i = 0; i < finalWorlds.size(); i++) {
            if(Client.isLoggedIn() && !Players.getLocal().isInCombat() && cosmicArea.contains(Players.getLocal())) {
                getRandomManager().disableSolver(RandomEvent.LOGIN);
                WorldHopper.hopWorld(finalWorlds.get(i));
                Sleep.sleepUntil(()-> Client.getGameState() == GameState.LOGGED_IN,30000);
                if(Client.getGameState() == GameState.LOGGED_IN) {
                    sleep(300);
                }
                stayLoggedIn(userAcc);
                searchPlayers(Boolean.FALSE, userAcc);
                searchCosmic(Boolean.TRUE);
            }
        }
    }

    private void calcProfit(int loopNum) {
        long endTime = System.nanoTime();
        long minutesRunning = ((endTime-startTime)/1000000000)/60;
        try {
            log(  "Loop: " + loopNum + " finished.   Total $: " + Inventory.count("Cosmic rune")*Inventory.get("Cosmic rune").getLivePrice() + "   " + totalCosmicCollected/minutesRunning + " IPM   " +  Inventory.get("Cosmic rune").getLivePrice() * totalCosmicCollected/minutesRunning + " $pm ");
        }
        catch(Exception e) {
            log("Error " + e);
        }
    }
    private void searchCosmic(Boolean afk) {
        GroundItem cosmicRune = GroundItems.closest(item -> item.getName().equals("Cosmic rune") && item.getAmount() == 3);
        if (cosmicRune != null) {
            sleep(Calculations.random(350,450));
            int currentCosmic = Inventory.count("Cosmic Rune");
            if(cosmicRune.interact("Take")) {
                Sleep.sleepUntil(() -> Inventory.count("Cosmic rune") == currentCosmic+3, 1000);
                if (Inventory.count("Cosmic rune") != currentCosmic+3) {
                    sleep(Calculations.random(100,200));
                    cosmicRune.interact("Take");
                }
                totalCosmicCollected += 3;
            }
        }
    }
    private void stayLoggedIn(String userAcc) {
        if (!Client.isLoggedIn()) {
            int milisecondsOut = (1000*60*20);
            log("Staying logged out for... " + milisecondsOut);
            sleep(milisecondsOut);
            LoginUtility.login(userAcc, "OldKyles10");
            sleep(2000);
            LoginUtility.login(userAcc, "OldKyles10");
            Sleep.sleepUntil(()-> Client.getGameState() == GameState.LOGGED_IN, 30000);
            sleep(750);
        }
    }
    private void searchPlayers(Boolean hopOrNot, String userAcc) {
        long startSearchingTime = System.nanoTime(); //Start timer for search
        List<Player> allNearbyPlayers = all(player -> player != null); // Get all nearby players, you could filter your player out;
        allNearbyPlayers.remove(Players.getLocal()); //Remove yourself fom the list
        if (allNearbyPlayers.size() > 0) { // Ensure there are moe than 1 players in the list
            for (int j = 0; j < allNearbyPlayers.size(); j++) {
                Player randomPlayer = allNearbyPlayers.get(j); //grab each individual player
                List<String> clanmateList = new ArrayList<String>();
                List<String> pvpItems = new ArrayList<String>();
                Collections.addAll(pvpItems,"Staff of fire", "Staff of air", "Staff of water", "Staff of earth");
                Collections.addAll(clanmateList, "omightyswan", "N0rth East", "N0rth E3st", "cdog swan99", "Level 80", "SJW killer", "north pile", "north pure", "off follow", "north attack", "south pile", "r i i", "i ir", "SJW die");
                log(randomPlayer.getEquipment().size() + " " + randomPlayer.getEquipment()); // lists players item
                boolean isaPvper = false;
                for(int i=0; i < randomPlayer.getEquipment().size(); i++) {
                    if (pvpItems.contains(randomPlayer.getEquipment().get(i))) {
                        isaPvper = true;
                    }
                }
                if (!clanmateList.contains(randomPlayer.getName())) { //Make sure there not a clanmate
                    int combatLevel = Combat.getCombatLevel() + Combat.getWildernessLevel(); // 3 + 45 = 48
                    if (randomPlayer.getLevel() <= combatLevel) {
                        Mouse.setAlwaysHop(true);
                        log("He can attack you: " + randomPlayer.getName() +" "+ randomPlayer.getLevel());
                        if (Client.isLoggedIn()) {
                            getRandomManager().disableSolver(RandomEvent.LOGIN);
                            Tabs.logout();
                            log("In seconds how long it took to logout: " + (System.nanoTime()-startSearchingTime)/1000000 + " ms");
                            Mouse.setAlwaysHop(false);
                            boolean skulled = apiDiscord(randomPlayer);
                            if(isaPvper == true || skulled == true) {
                                sleep(1000*60*25);
                                log("Skulled or Staff held");
                            } else if (randomPlayer.getName().equals("Free Cosmics")) {
                                sleep(1000*60*60);
                                log("Free Cosmicer Iron man killer....");
                            } else {
                                sleep(1000*60*5);
                                log("Not pker");
                            }
//                            LoginUtility.openWorldScreen();
//                            Random rand = new Random();
//                            int randomElement = worldList.get(rand.nextInt(20));
//                            LoginUtility.changeWorld(randomElement);
//                            sleep(1000);
//                            LoginUtility.closeWorldScreen();
                            LoginUtility.login(userAcc, "OldKyles10");
                            sleep(2000);
                            LoginUtility.login(userAcc, "OldKyles10");
                            Sleep.sleepUntil(()-> Client.getGameState() == GameState.LOGGED_IN, 30000);
                            sleep(750);
                        }
                    } else {
                        log("In seconds how long it took to log this message: " + (System.nanoTime()-startSearchingTime)/1000000);
                        log("He can't attack you");
                        log(randomPlayer.getLevel() + " " + randomPlayer.getName());
                        apiDiscord(randomPlayer);
                        if (hopOrNot == Boolean.TRUE){
                            World world = Worlds.getRandomWorld(w -> w.isF2P() && w.isNormal() && w.getMinimumLevel() == 0);
                            WorldHopper.hopWorld(world);
                            log("Hopping to because of spam " + world);
                        }
                    }
                } else {
                    log("Found a clanmate: " + randomPlayer.getName());
                }
            }
        }
    }

    public boolean apiDiscord(Player randomPlayer) {
        //#######################
        int playerDefLevel = 0;
        try {
            playerDefLevel = new Highscores(randomPlayer.getName()).getSkillExperience(Highscores.Skills.DEFENCE);
        } catch (Exception e) {
            System.out.println(e);
        }
        int[] equipment = randomPlayer.getComposite().getAppearance();
        List<Item> equipment1 = randomPlayer.getEquipment();
        log(equipment1);
        int total = 0;
        List<String> armour = new ArrayList<String>();
        for (int i = 0; i < equipment.length; i++) {
            if (equipment[i] - 512 > 0){
                armour.add(new Item(equipment[i]-512, 1).getName());
                total += new Item(equipment[i]-512, 1).getLivePrice();
            }
        }
        DiscordWebHook webhook = new DiscordWebHook("https://discord.com/api/webhooks/1017195790961233950/TGz4ALTu8v4zHRtvRcG6KCx0A9z31W88wVO5huO6LtuFBYkO3RYiGXzVnY60jRSiLJxqz");
        webhook.setUsername("Cosmic");
        String skulledOrNot = "";
        boolean skulledorNotBoolean = false;
        if (randomPlayer.getSkullIcon() == 8 || randomPlayer.getSkullIcon() == 9 || randomPlayer.getSkullIcon() == 10 || randomPlayer.getSkullIcon() == 11 || randomPlayer.getSkullIcon() == 12) {
            skulledOrNot = "Key Skulled: " + (randomPlayer.getSkullIcon() - 7);
            skulledorNotBoolean = true;
        } else if(randomPlayer.isSkulled()) {
            skulledOrNot = "Normal Skull";
            skulledorNotBoolean = true;

        } else {
            skulledOrNot = "None";
            skulledorNotBoolean = false;
        }
        String titleLoc = randomPlayer.getTile() + "";
        webhook.addEmbed(new DiscordWebHook.EmbedObject()
                .setTitle(randomPlayer.getName())
                .setDescription(armour + "")
                .addField("World: " , Worlds.getCurrentWorld() + "", true)
                .addField( "Combat: ", randomPlayer.getLevel() + "", true)
                .addField("Skulled: ", skulledOrNot, true)
                .addField("Def lvl: ", "" + playerDefLevel, true)
                .addField("Distance: ", Math.round(randomPlayer.distance()) + ", Tile: " + titleLoc.substring(1, 11), true)
                .addField("Health: ", "Health: " + randomPlayer.getHealthPercent() + "%", true)
                .addField("Total $: ", "$" + total, true));
        try {
            webhook.execute();
        }
        catch (java.io.IOException e) {
            System.out.println(e);
        }
        log(armour);
        return skulledorNotBoolean;
        //######################
    }

    //When script start load this.
    public void onStart () {
        getRandomManager().disableSolver(RandomEvent.LOGIN);
        for (int i=0; i < worldList2.size(); i++) {
            filterWorlds.add(Integer.parseInt(worldList2.get(i).toString().substring(1,4)));
        }
        filterWorlds.add(326);
        Collections.sort(filterWorlds);
        filterWorlds.remove(0);
        log((filterWorlds.size()/2 + " " + filterWorlds.size() + " " + filterWorlds))   ;

        //finalWorlds = filterWorlds.subList(0, filterWorlds.size()/2);
        finalWorlds = filterWorlds.subList((filterWorlds.size()/2), filterWorlds.size());

//        String [][] accounts = {{"almightyswan6@mail.com", "OldKyles10"}, {"almightyswan8@mail.com", "OldKyles10"}, {"almightyswan9@mail.com", "OldKyles10"}, {"almightyswan12@mail.com", "OldKyles10"}, {"almightyswan13@mail.com", "OldKyles10"}, {"almightyswan14@mail.com", "OldKyles10"},  {"almightyswan15@mail.com", "OldKyles10"},  {"almightyswan16@mail.com", "OldKyles10"}, {"almightyswan17@mail.com", "OldKyles10"}, {"almightyswan18@mail.com", "OldKyles10"}};


    }

    //When script ends do this.
    public void onExit () {
        log("Bot Ended");
    }

}


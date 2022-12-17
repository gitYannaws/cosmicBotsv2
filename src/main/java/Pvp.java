import org.dreambot.api.Client;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;

import static org.dreambot.api.utilities.Logger.log;
import static org.dreambot.api.utilities.Sleep.sleep;

public class Pvp {
    Area safeTile = new Area(2944, 3903, 2949, 3897);
    Area finalTile = new Area(2944, 3903, 2944, 3903);

    public void mainPvp(Player player) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        log("Detected Level 3");
        log(player.distance(Players.getLocal().getTile()));
        if (player.canAttack() && !player.isInCombat() && !Players.getLocal().isInCombat() && player.distance(Players.getLocal().getTile()) <= 15) {
            log("Can attack");
            if (player.interactForceRight("Attack")) {
                sleep(2000);
                while (Players.getLocal().isInCombat()) {
                    log("isinteracting" + Players.getLocal().isInteracting(player));
                    log("isinCombat" + player.isInCombat());
                    sleep(250);
                    log("PvP While loop");
                    if (Players.getLocal().getHealthPercent() <= 51) {
                        eatFood();
                        player.interact("Attack");
                    }
                }
                log("Player died?");
                sleep(5000);
                for (GroundItem loot : GroundItems.all(groundItem -> !groundItem.getName().equals("Water rune") && !groundItem.getName().equals("Bones") && groundItem.distance(Players.getLocal().getTile()) <= 15)){
                    if (loot!=null){
                        log(loot);
                        log(loot.getAmount());
                        Sleep.sleepUntil(() -> loot.interact("Take"), 10000);
                        sleep(3500);
                    }
                }
                while (!safeTile.contains(Players.getLocal())) {
                    Walking.walk(safeTile);
                    sleep(1000);
                    log("walking");
                    if (Players.getLocal().getHealthPercent() <= 70) {
                        eatFood();
                    }
                    Sleep.sleepUntil(() -> !Players.getLocal().isMoving(), 5000);
                }
                sleep(1000);
                Walking.walk(safeTile);
                sleep(6000*60*4);
                Tabs.openWithMouse(Tab.INVENTORY);
                sleep(6000*60*4);
                Tabs.openWithMouse(Tab.PRAYER);
                sleep(6000*60*4);
                Tabs.openWithMouse(Tab.LOGOUT);

            }
        }
    }

    public  void eatFood() {
        log("Eating");
        if (!Tabs.isOpen(Tab.INVENTORY)) {
            Tabs.open(Tab.INVENTORY);
        } else {
            Item food = Inventory.get(item -> item != null && item.hasAction("Drink"));
            if (food != null) {
                food.interact("Drink");
                Sleep.sleepUntil(() -> Players.getLocal().getHealthPercent() > 70, 1500);
            }
        }
    }
}

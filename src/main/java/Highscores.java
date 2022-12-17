import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Highscores {
    ArrayList<String> stats = new ArrayList<String>();

    public Highscores(String player) {
        getStats(player);
    }


    public final int getSkillExperience(Skills skill) {
        int index = skill.getLevelIndex();
        String[] array = stats.get(index).split(",");
        return Integer.parseInt(array[1]);
    }

    private void getStats(final String player) {
        try {
            URL url = new URL("https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=" + player);
            URLConnection con = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                stats.add(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            //CATCH
        } catch (IOException e) {
            //CATCH
        }
    }

    public enum Skills {

        TOTAL(0), ATTACK(1), DEFENCE(2), STRENGTH(3), HITPOINTS(4), RANGED(5), PRAYER(6), MAGIC(7),
        COOKING(8), WOODCUTTING(9), FLETCHING(10), FISHING(11), FIREMAKING(12), CRAFTING(13),
        SMITHING(14), MINING(15), HERBLORE(16), AGILITY(17), THIEVING(18), SLAYER(19),
        FARMING(20), RUNECRAFT(21), HUNTER(22), CONSTRUCTION(23);

        Skills(int levelIndex) {
            this.levelIndex = levelIndex;
        }

        private int levelIndex;

        public int getLevelIndex() {
            return levelIndex;
        }
    }
}
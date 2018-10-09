import java.util.Random;

/**
 * Created by BugDeveloper on 14.11.2016.
 */
public class Main {

    public static void main(String[] args) {
        Random rand = new Random();
        BSPGenerator bspGen = new BSPGenerator(100, 100, 6, 4, rand);
        int[][] map = bspGen.generateMap();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++)
                if (map[i][j] == 1) {
                    System.out.print(" ");
                } else {
                    System.out.print("0");
                }
            System.out.println();
        }
    }
}

import java.util.Random;
import java.lang.IllegalArgumentException;

/**
 * Created by BugDeveloper on 14.11.2016.
 */
public class BSPGenerator {

    private enum Orientation { Horizontal, Vertical };
    private int height, width, minimalRoomSize, differenceDivider, minSpace = 1;
    private BSPTree bspTree;
    private Random rnd;
    private int [][] map;

    public int[][] generateMap() {
        bspTree = new BSPTree(0, 0, map.length, map[0].length);
        generateMap(bspTree);
        drawCorridorsFromTop(bspTree);
        createBorders();
        return map;
    }

    public BSPGenerator(int size, int minimalRoomSize, int differenceDivider, Random rnd) {
        if ((differenceDivider >= minimalRoomSize) || (differenceDivider == 1)) {
            throw new IllegalArgumentException("Value of differenceDivider must be less than minimalRoomSize and more than 1");
        }
        this.width = size;
        this.height = size;
        this.minimalRoomSize = minimalRoomSize;
        this.differenceDivider = differenceDivider;
        this.rnd = rnd;
        map = new int[width][height];

    }

    private void generateRoom(BSPTree node)
    {

        int x = node.getStartX() + minSpace + rnd.nextInt(node.getWidth() / minimalRoomSize - minSpace + 1);
        int y = node.getStartY() + minSpace + rnd.nextInt(node.getHeight() / minimalRoomSize - minSpace + 1);

        int width = node.getWidth() - (x - node.getStartX());
        int height = node.getHeight() - (y - node.getStartY());

        width -= rnd.nextInt(width / differenceDivider);
        height -= rnd.nextInt(height / differenceDivider);
        roomToArray(x, y, width, height);
    }

    private void createBorders() {
        for (int i = 0; i < map.length; i++)
            map[i][0] = 0;

        for (int i = 0; i < map.length; i++)
            map[i][map[0].length - 1] = 0;

        for (int i = 0; i < map.length; i++)
            map[0][i] = 0;

        for (int i = 0; i < map.length; i++)
            map[map.length - 1][i] = 0;

    }

    private void generateMap(BSPTree node)
    {

        if (node == null)
            return;

        if (node.getLeftChild() == null && node.getRightChild() == null)
        {
            generateRoom(node);
        }
        else
        {
            generateMap(node.getLeftChild());
            generateMap(node.getRightChild());
        }
    }

    private void roomToArray(int x, int y, int w, int h)
    {
        for (int i = x; i < w + x; i++)
        {
            for (int j = y; j < h + y; j++)
            {
                map[i][j] = 1;
            }
        }
    }

    private void drawCorridorsFromTop(BSPTree node)
    {
        if (node.getRightChild() == null || node.getLeftChild() == null)
            return;

        int startX = (node.getLeftChild().getEndX() + node.getLeftChild().getStartX()) / 2;

        int startY = (node.getLeftChild().getEndY() + node.getLeftChild().getStartY()) / 2;

        int endX = (node.getRightChild().getEndX() + node.getRightChild().getStartX()) / 2;

        int endY = (node.getRightChild().getEndY() + node.getRightChild().getStartY()) / 2;

        int temp;

        if (startX == endX)
        {
            if (startY > endY)
            {
                temp = endY;
                endY = startY;
                startY = temp;
            }

            for (int i = startY; i < endY; i++)
            {
                map[startX][i] = 1;
            }
        }
        else if (startY == endY)
        {
            if (startX > endX)
            {
                temp = endX;
                endX = startX;
                startX = temp;
            }

            for (int i = startX; i < endX; i++)
            {
                map[i][startY] = 1;
            }
        }

        drawCorridorsFromTop(node.getLeftChild());
        drawCorridorsFromTop(node.getRightChild());
    }

    public class BSPTree
    {
        private int startX;
        private int startY;
        private int endX;
        private int endY;
        private int width;

        public int getStartX() {
            return startX;
        }

        public int getStartY() {
            return startY;
        }

        public int getEndX() {
            return endX;
        }

        public int getEndY() {
            return endY;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public BSPTree getLeftChild() {
            return leftChild;
        }

        public BSPTree getRightChild() {
            return rightChild;
        }

        private int height;
        private BSPTree leftChild, rightChild;
        private float maxPartitionSizeRatio = 1f;

        public BSPTree(int startX, int startY, int endX, int endY)
        {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            width = Math.abs(endX - startX);
            height = Math.abs(endY - startY);

        //    if (this.getHeight() < minimalRoomSize || this.getWidth() < minimalRoomSize)
        //        System.out.print("Rot ebal");

            if (shouldSplit(this))
                partition(this);
        }

        private boolean shouldSplit(BSPTree node)
        {
            if (node.getWidth() >= minimalRoomSize * 2 && node.getHeight() >= minimalRoomSize * 2)
                return true;
            return false;
        }

        private void partition(BSPTree node)
        {
            Orientation splitOrient;
            int splitLocation;

            //Debug.Log("Start X: " + node.StartX + ", Start Y: " + node.StartY + ", Width: " + node.Width + ", Height: " + node.Height + ".");

            if (node.getWidth() / node.getHeight() > maxPartitionSizeRatio)
            {
                splitOrient = Orientation.Vertical;
            }
            else if (node.getHeight() / node.getWidth() > maxPartitionSizeRatio)
            {
                splitOrient = Orientation.Horizontal;
            }
            else
            {
                splitOrient = (rnd.nextInt(2) == 1) ? Orientation.Horizontal : Orientation.Vertical;
            }

            if (splitOrient == Orientation.Horizontal)
            {

                splitLocation = node.getStartY() + minimalRoomSize + rnd.nextInt(node.getEndY() - 2 * minimalRoomSize - node.getStartY() + 1);

                node.leftChild = new BSPTree(node.getStartX(), node.getStartY(), node.getEndX(), splitLocation);
                node.rightChild = new BSPTree(node.getStartX(), splitLocation, node.getEndX(), node.getEndY());

            }
            else
            {
                splitLocation = node.getStartX() + minimalRoomSize + rnd.nextInt(node.getEndX() - 2 * minimalRoomSize - node.getStartX() + 1);

                node.leftChild = new BSPTree(node.getStartX(), node.getStartY(), splitLocation, node.getEndY());
                node.rightChild = new BSPTree(splitLocation, node.getStartY(), node.getEndX(), node.getEndY());
            }

        }
    }
}

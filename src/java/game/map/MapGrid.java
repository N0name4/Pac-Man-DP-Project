package game.map;

public interface MapGrid {
    void collapse();
    boolean isConnected();
    char[][] buildGrid();
}

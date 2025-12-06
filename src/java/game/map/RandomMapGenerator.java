package game.map;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public abstract class RandomMapGenerator {
    protected final Random random;

    public RandomMapGenerator()
    {
        this.random = new Random();
    }

    public RandomMapGenerator(long seed)
    {
        this.random = new Random(seed);
    }

    //템플릿 메서드 패턴
    protected abstract MapGrid createGrid(int cols, int rows, Random random);

    public List<List<String>> generate(int targetCols, int targetRows)
    {
        List<List<String>> data = null;
        int attempts = 0;
        int maxAttempts = 100;

        while (attempts < maxAttempts) {
            data = generateMap(targetCols, targetRows);
            if (data != null) {
                return data;
            }
            attempts++;
        }

        return forceGenerateMap(targetCols, targetRows);
    }

    private List<List<String>> generateMap(int targetCols, int targetRows) {
        int groupCols = Math.max(4, (targetCols / 2) / 3);
        int groupRows = Math.max(5, targetRows / 3);

        // Factory Method 호출
        MapGrid grid = createGrid(groupCols, groupRows, random);
        grid.collapse();

        if (!grid.isConnected()) {
            return null;
        }

        char[][] result = grid.buildGrid();

        if (!MapConnectivityValidator.validateGridConnectivity(result)) {
            return null;
        }

        EntityPlacer.placeEntities(result);
        PelletPlacer.placePelletsAndPowerPellets(result, random);

        return toList(result);
    }

    private List<List<String>> forceGenerateMap(int targetCols, int targetRows) {
        int groupCols = Math.max(4, (targetCols / 2) / 3);
        int groupRows = Math.max(5, targetRows / 3);

        MapGrid grid = createGrid(groupCols, groupRows, random);
        grid.collapse();

        char[][] result = grid.buildGrid();
        EntityPlacer.placeEntities(result);
        PelletPlacer.placePelletsAndPowerPellets(result, random);

        return toList(result);
    }

    private List<List<String>> toList(char[][] grid) {
        List<List<String>> list = new ArrayList<>();
        for (char[] row : grid) {
            List<String> rowList = new ArrayList<>();
            for (char ch : row) {
                rowList.add(String.valueOf(ch));
            }
            list.add(rowList);
        }
        return list;
    }



}

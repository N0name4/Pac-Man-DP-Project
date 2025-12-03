package game.map;

import java.util.*;

/**
 * 랜덤 레벨 생성기
 * WFC(Wave Function Collapse) 알고리즘을 사용하여 팩맨 게임 맵을 생성
 * 
 * 적용된 디자인 패턴:
 * - Template Method Pattern: generate() 메서드에서 전체 생성 프로세스 정의
 * - Strategy Pattern: generateMap()과 forceGenerateMap()으로 다른 생성 전략 제공
 * - Builder Pattern: 단계별 맵 구성 (WFC → 그리드 변환 → 엔티티 배치 → 펠릿 배치)
 */
public class RandomLevelGenerator {

    private final Random random;

    public RandomLevelGenerator() {
        this.random = new Random();
    }

    public RandomLevelGenerator(long seed) {
        this.random = new Random(seed);
    }

    /**
     * 유효한 맵 생성 (Template Method Pattern)
     * 최대 시도 횟수 내에서 연결성이 검증된 맵을 생성
     */
    public List<List<String>> generate(int targetCols, int targetRows) {
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

    /**
     * 검증을 포함한 맵 생성 (Strategy Pattern)
     */
    private List<List<String>> generateMap(int targetCols, int targetRows) {
        int groupCols = Math.max(4, (targetCols / 2) / 3);
        int groupRows = Math.max(5, targetRows / 3);

        // 1단계: WFC 알고리즘으로 타일맵 생성
        WFCGrid wfc = new WFCGrid(groupCols, groupRows, random);
        wfc.collapse();
        
        // 2단계: 타일맵 연결성 검증
        if (!wfc.isConnected()) {
            return null;
        }

        // 3단계: 타일맵을 실제 그리드로 변환
        char[][] grid = wfc.buildMacroGridWithCenterPath();
        
        // 4단계: 실제 grid 연결성 검증
        if (!MapConnectivityValidator.validateGridConnectivity(grid)) {
            return null;
        }

        // 5단계: 엔티티 배치
        EntityPlacer.placeEntities(grid);
        
        // 6단계: 펠릿 배치
        PelletPlacer.placePelletsAndPowerPellets(grid, random);

        return toList(grid);
    }

    /**
     * 검증 없이 강제로 맵 생성 (Fallback Strategy)
     */
    private List<List<String>> forceGenerateMap(int targetCols, int targetRows) {
        int groupCols = Math.max(4, (targetCols / 2) / 3);
        int groupRows = Math.max(5, targetRows / 3);

        WFCGrid wfc = new WFCGrid(groupCols, groupRows, random);
        wfc.collapse();

        char[][] grid = wfc.buildMacroGridWithCenterPath();
        EntityPlacer.placeEntities(grid);
        PelletPlacer.placePelletsAndPowerPellets(grid, random);

        return toList(grid);
    }

    /**
     * char 2D 배열을 List<List<String>>으로 변환
     */
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
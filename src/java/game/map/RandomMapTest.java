package game.map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RandomMapTest {

    @Nested
    @DisplayName("Factory Method 패턴 테스트")
    class FactoryMethodTest {

        @Test
        @DisplayName("WFCMapGenerator는 WFCGrid를 생성한다")
        void createGrid_WFCMapGenerator_ReturnsWFCGrid() {
            WFCMapGenerator generator = new WFCMapGenerator(12345L);

            MapGrid grid = generator.createGrid(5, 5, new Random(12345L));

            assertNotNull(grid);
            assertInstanceOf(WFCGrid.class, grid);
        }

        @Test
        @DisplayName("다른 시드로 생성된 Grid는 다른 결과를 만든다")
        void createGrid_DifferentSeeds_ProducesDifferentResults() {
            WFCMapGenerator generator1 = new WFCMapGenerator(11111L);
            WFCMapGenerator generator2 = new WFCMapGenerator(99999L);

            MapGrid grid1 = generator1.createGrid(5, 5, new Random(11111L));
            MapGrid grid2 = generator2.createGrid(5, 5, new Random(99999L));
            grid1.collapse();
            grid2.collapse();

            assertFalse(java.util.Arrays.deepEquals(grid1.buildGrid(), grid2.buildGrid()));
        }
    }

    @Nested
    @DisplayName("Template Method 패턴 테스트")
    class TemplateMethodTest {
        @Test
        @DisplayName("같은 시드는 같은 맵을 생성한다")
        void generate_SameSeed_ProducesSameMap() {
            long seed = 12345L;
            RandomMapGenerator generator1 = new WFCMapGenerator(seed);
            RandomMapGenerator generator2 = new WFCMapGenerator(seed);

            List<List<String>> map1 = generator1.generate(30, 30);
            List<List<String>> map2 = generator2.generate(30, 30);

            assertEquals(map1, map2);
        }

        @Test
        @DisplayName("다른 시드는 다른 맵을 생성한다")
        void generate_DifferentSeed_ProducesDifferentMap() {
            RandomMapGenerator generator1 = new WFCMapGenerator(11111L);
            RandomMapGenerator generator2 = new WFCMapGenerator(99999L);

            List<List<String>> map1 = generator1.generate(30, 30);
            List<List<String>> map2 = generator2.generate(30, 30);

            assertNotEquals(map1, map2);
        }
    }

    @Nested
    @DisplayName("MapGrid 인터페이스 계약 테스트")
    class MapGridContractTest {

        @Test
        @DisplayName("collapse 후 buildGrid는 유효한 결과를 반환한다")
        void collapse_ThenBuildGrid_ReturnsValidResult() {
            MapGrid grid = new WFCGrid(5, 5, new Random(12345L));
            grid.collapse();
            char[][] result = grid.buildGrid();

            assertNotNull(result);
            assertTrue(result.length > 0 && result[0].length > 0);
        }

        @Test
        @DisplayName("buildGrid는 공백과 벽 문자만 포함한다")
        void buildGrid_ContainsOnlyValidCharacters() {
            WFCGrid grid = new WFCGrid(5, 5, new Random(12345L));
            grid.collapse();

            for (char[] row : grid.buildGrid()) {
                for (char cell : row) {
                    assertTrue(cell == ' ' || cell == 'x');
                }
            }
        }
    }
}
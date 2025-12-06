package game.map;

import java.util.*;

/**
 * 게임 맵에 펠릿과 파워펠릿을 배치하는 클래스
 */
public class PelletPlacer {
    
    /**
     * 펠릿과 파워펠릿을 맵에 배치
     */
    public static void placePelletsAndPowerPellets(char[][] grid, Random random) {
        int rows = grid.length;
        int cols = grid[0].length;

        int macroRows = rows / 6;
        int macroCols = cols / 6;

        // 기존 펠릿 제거
        clearExistingPellets(grid);

        // 매크로 타일 단위로 펠릿 배치
        placePelletsInMacroTiles(grid, macroRows, macroCols);

        // 엔티티 주변 펠릿 제거
        clearPelletsAroundEntities(grid);

        // 파워펠릿 배치
        placePowerPellets(grid, random, 4);
    }

    /**
     * 기존에 있던 펠릿 제거
     */
    private static void clearExistingPellets(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '.' || grid[r][c] == 'o') {
                    grid[r][c] = ' ';
                }
            }
        }
    }

    /**
     * 매크로 타일 단위로 펠릿 배치
     */
    private static void placePelletsInMacroTiles(char[][] grid, int macroRows, int macroCols) {
        int rows = grid.length;
        int cols = grid[0].length;

        for (int gr = 0; gr < macroRows; gr++) {
            for (int gc = 0; gc < macroCols; gc++) {
                int baseR = gr * 6;
                int baseC = gc * 6;

                if (baseR + 5 >= rows || baseC + 5 >= cols) continue;

                boolean hasRoom = false;
                for (int r = 1; r <= 4 && !hasRoom; r++) {
                    for (int c = 1; c <= 4 && !hasRoom; c++) {
                        if (grid[baseR + r][baseC + c] != 'x') {
                            hasRoom = true;
                        }
                    }
                }
                if (!hasRoom) continue;

                boolean hasN = checkPath(grid, baseR + 0, baseC + 1, baseC + 4);
                boolean hasS = checkPath(grid, baseR + 5, baseC + 1, baseC + 4);
                boolean hasW = checkPathVertical(grid, baseC + 0, baseR + 1, baseR + 4);
                boolean hasE = checkPathVertical(grid, baseC + 5, baseR + 1, baseR + 4);

                // 중앙에 펠릿 배치
                tryPlacePellet(grid, baseR + 2, baseC + 2);

                // 연결된 방향에 펠릿 배치
                if (hasN) {
                    tryPlacePellet(grid, baseR + 0, baseC + 2);
                }

                if (hasW) {
                    tryPlacePellet(grid, baseR + 2, baseC + 0);
                }

                if (hasS) {
                    tryPlacePellet(grid, baseR + 5, baseC + 2);
                }

                if (hasE) {
                    tryPlacePellet(grid, baseR + 2, baseC + 5);
                }
            }
        }
    }

    /**
     * 엔티티 주변의 펠릿 제거
     */
    private static void clearPelletsAroundEntities(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char ch = grid[r][c];
                if (ch == 'P' || ch == 'b' || ch == 'p' || ch == 'i' || ch == 'c') {
                    clearPelletAround(grid, r, c, 2);
                }
            }
        }
    }

    /**
     * 기존 펠릿 중 일부를 파워펠릿으로 변환
     */
    private static void placePowerPellets(char[][] grid, Random random, int count) {
        int rows = grid.length, cols = grid[0].length;
        List<int[]> pellets = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '.') {
                    pellets.add(new int[]{r, c});
                }
            }
        }

        if (pellets.isEmpty()) return;

        Collections.shuffle(pellets, random);
        int n = Math.min(count, pellets.size());
        for (int i = 0; i < n; i++) {
            int[] pos = pellets.get(i);
            grid[pos[0]][pos[1]] = 'o';
        }
    }

    /**
     * 수평 경로 확인
     */
    private static boolean checkPath(char[][] grid, int row, int colStart, int colEnd) {
        for (int c = colStart; c <= colEnd; c++) {
            if (row >= 0 && row < grid.length && c >= 0 && c < grid[0].length) {
                if (grid[row][c] != 'x') {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 수직 경로 확인
     */
    private static boolean checkPathVertical(char[][] grid, int col, int rowStart, int rowEnd) {
        for (int r = rowStart; r <= rowEnd; r++) {
            if (r >= 0 && r < grid.length && col >= 0 && col < grid[0].length) {
                if (grid[r][col] != 'x') {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 펠릿 배치 시도
     */
    private static void tryPlacePellet(char[][] grid, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length) return;
        if (grid[r][c] == ' ') {
            grid[r][c] = '.';
        }
    }

    /**
     * 특정 위치 주변의 펠릿 제거
     */
    private static void clearPelletAround(char[][] grid, int r, int c, int radius) {
        int rows = grid.length, cols = grid[0].length;
        for (int dr = -radius; dr <= radius; dr++) {
            for (int dc = -radius; dc <= radius; dc++) {
                int nr = r + dr, nc = c + dc;
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                if (grid[nr][nc] == '.' || grid[nr][nc] == 'o') {
                    grid[nr][nc] = ' ';
                }
            }
        }
    }
}
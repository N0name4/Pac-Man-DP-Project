package game.map;

import java.util.*;

/**
 * 게임 맵에 엔티티(팩맨, 유령)를 배치하는 클래스
 */
public class EntityPlacer {
    
    /**
     * 팩맨과 유령들을 맵에 배치
     */
    public static void placeEntities(char[][] grid) {
        placePacman(grid);
        placeGhosts(grid);
    }

    /**
     * 팩맨을 맵 하단 중앙에 배치
     */
    private static void placePacman(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        int startRow = rows - 3;
        int startCol = cols / 2;
        int[] pPos = findClosestPath(grid, startRow, startCol);

        if (pPos != null) {
            int baseR = (pPos[0] / 6) * 6;
            int baseC = (pPos[1] / 6) * 6;

            int newPR = baseR + 1;
            int newPC = baseC + 1;

            grid[pPos[0]][pPos[1]] = ' ';

            if (isWalkable(grid, newPR, newPC)) {
                grid[newPR][newPC] = 'P';
            } else {
                grid[pPos[0]][pPos[1]] = 'P';
            }
        }
    }

    /**
     * 유령들을 맵 중앙에 배치
     */
    private static void placeGhosts(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        char[] ghosts = {'b', 'p', 'i', 'c'};
        int ghostPlaced = 0;

        int centerRow = rows / 2;
        int centerCol = cols / 2;

        List<int[]> candidates = new ArrayList<>();
        for (int r = centerRow - 4; r <= centerRow + 4; r++) {
            for (int c = centerCol - 8; c <= centerCol + 8; c++) {
                if (r < 0 || r >= rows || c < 0 || c >= cols) continue;
                if (grid[r][c] == ' ') {
                    candidates.add(new int[]{r, c});
                }
            }
        }

        candidates.sort(Comparator.comparingInt(p ->
                Math.abs(p[0] - centerRow) + Math.abs(p[1] - centerCol)));

        Set<String> usedMacro = new HashSet<>();

        for (int[] pos : candidates) {
            if (ghostPlaced >= ghosts.length) break;

            int r = pos[0];
            int c = pos[1];

            int baseR = (r / 6) * 6;
            int baseC = (c / 6) * 6;
            String key = baseR + "," + baseC;

            if (usedMacro.contains(key)) continue;

            int gr = baseR + 1;
            int gc = baseC + 1;

            if (!inBounds(grid, gr, gc)) continue;
            if (grid[gr][gc] == 'P') continue;

            grid[r][c] = ' ';

            if (isWalkable(grid, gr, gc)) {
                grid[gr][gc] = ghosts[ghostPlaced++];
                usedMacro.add(key);
            } else {
                grid[r][c] = ghosts[ghostPlaced++];
                usedMacro.add(key);
            }
        }
    }

    /**
     * 주어진 위치에서 가장 가까운 걸을 수 있는 공간 찾기 (BFS)
     */
    private static int[] findClosestPath(char[][] grid, int sr, int sc) {
        int rows = grid.length, cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> q = new LinkedList<>();
        q.offer(new int[]{sr, sc});
        if (sr >= 0 && sr < rows && sc >= 0 && sc < cols)
            visited[sr][sc] = true;

        int[][] DIR = {{-1,0},{1,0},{0,-1},{0,1}};

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int r = cur[0], c = cur[1];

            if (r < 0 || r >= rows || c < 0 || c >= cols) continue;
            if (grid[r][c] == ' ') return new int[]{r, c};

            for (int[] d : DIR) {
                int nr = r + d[0], nc = c + d[1];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                if (visited[nr][nc]) continue;
                visited[nr][nc] = true;
                q.offer(new int[]{nr, nc});
            }
        }
        return null;
    }

    private static boolean isWalkable(char[][] grid, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length) return false;
        return grid[r][c] != 'x';
    }

    private static boolean inBounds(char[][] grid, int r, int c) {
        return r >= 0 && r < grid.length && c >= 0 && c < grid[0].length;
    }
}
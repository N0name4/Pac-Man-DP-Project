package game.map;

import java.util.*;

/**
 * 맵의 연결성을 검증하는 유틸리티 클래스
 * Flood Fill 알고리즘을 사용하여 모든 공간이 연결되어 있는지 확인
 */
public class MapConnectivityValidator {

    /**
     * 타일맵 레벨에서의 연결성 검증 (WFCGrid)
     * 모든 비어있지 않은 타일들이 서로 연결되어 있는지 확인
     */
    public static boolean validateTileConnectivity(TileGroup[][] groups, int rows, int cols) {
        // 1. 비어있지 않은 타일 찾기
        int startX = -1, startY = -1;
        int totalNonEmpty = 0;
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                TileShape shape = groups[r][c].getShape();
                if (shape != null && shape != TileShape.EMPTY) {
                    totalNonEmpty++;
                    if (startX == -1) {
                        startX = c;
                        startY = r;
                    }
                }
            }
        }
        
        // 최소 타일 수 확인
        if (totalNonEmpty < 10) return false;
        
        // 2. Flood Fill로 타일 연결성 검증
        int connectedCount = floodFillTiles(groups, rows, cols, startX, startY);
        
        // 3. 모든 타일이 연결되어 있는지 확인
        boolean allConnected = (connectedCount == totalNonEmpty);
        
        return allConnected;
    }

    /**
     * 실제 grid 레벨에서의 연결성 검증
     * 모든 걸을 수 있는 공간이 서로 연결되어 있는지 확인
     */
    public static boolean validateGridConnectivity(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        
        // 1. 걸을 수 있는 공간 찾기
        int startX = -1, startY = -1;
        int totalWalkable = 0;
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == ' ') {
                    totalWalkable++;
                    if (startX == -1) {
                        startX = c;
                        startY = r;
                    }
                }
            }
        }
        
        // 최소 공간 확인
        if (totalWalkable < 100) return false;
        
        // 2. Flood Fill로 연결성 검증
        int connected = floodFillGrid(grid, startX, startY);
        
        boolean isValid = (connected == totalWalkable);
        
        return isValid;
    }

    /**
     * 타일맵에 대한 Flood Fill
     */
    private static int floodFillTiles(TileGroup[][] groups, int rows, int cols, int startX, int startY) {
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startX, startY});
        visited[startY][startX] = true;
        int connectedCount = 1;
        
        int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}}; // 위, 아래, 왼쪽, 오른쪽
        
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int x = cur[0];
            int y = cur[1];
            
            TileShape curShape = groups[y][x].getShape();
            if (curShape == null || curShape == TileShape.EMPTY) continue;
            
            for (int d = 0; d < 4; d++) {
                int nx = x + dirs[d][0];
                int ny = y + dirs[d][1];
                
                // 범위 체크
                if (nx < 0 || nx >= cols || ny < 0 || ny >= rows) continue;
                if (visited[ny][nx]) continue;
                
                TileShape neighborShape = groups[ny][nx].getShape();
                if (neighborShape == null || neighborShape == TileShape.EMPTY) continue;
                
                // 두 타일이 실제로 연결되어 있는지 확인
                boolean isConnected = switch (d) {
                    case 0 -> curShape.n && neighborShape.s; // 위쪽
                    case 1 -> curShape.s && neighborShape.n; // 아래쪽
                    case 2 -> curShape.w && neighborShape.e; // 왼쪽
                    case 3 -> curShape.e && neighborShape.w; // 오른쪽
                    default -> false;
                };
                
                if (isConnected) {
                    visited[ny][nx] = true;
                    queue.offer(new int[]{nx, ny});
                    connectedCount++;
                }
            }
        }
        
        return connectedCount;
    }

    /**
     * 실제 grid에 대한 Flood Fill
     */
    private static int floodFillGrid(char[][] grid, int startX, int startY) {
        int rows = grid.length;
        int cols = grid[0].length;
        
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        
        queue.offer(new int[]{startX, startY});
        visited[startY][startX] = true;
        int count = 1;
        
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];
            
            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];
                
                if (nx < 0 || nx >= cols || ny < 0 || ny >= rows) continue;
                if (visited[ny][nx]) continue;
                if (grid[ny][nx] != ' ') continue; // 벽이면 스킵
                
                visited[ny][nx] = true;
                queue.offer(new int[]{nx, ny});
                count++;
            }
        }
        
        return count;
    }
}
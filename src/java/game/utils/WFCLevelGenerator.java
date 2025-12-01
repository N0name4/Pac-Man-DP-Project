package game.utils;

import java.util.*;

/**
 * WFC 기반 업스케일 팩맨 레벨 생성기
 * 
 * 엔티티 크기: 32x32픽셀 (4x4 셀)
 * 셀 크기: 8x8픽셀
 * 
 * 4x4 블록 내 배치:
 * - 팩맨(P), 블링키(b): [0][3]
 * - 나머지 유령(p,i,c): [1][0]
 * - 펠릿: 가로 [1][1],[1][3] / 세로 [1][1],[3][1]
 * - 슈퍼펠릿: [1][1]
 */
public class WFCLevelGenerator {

    private final Random random;
    private static final int SCALE_FACTOR = 4;

    public WFCLevelGenerator() {
        this.random = new Random();
    }

    public WFCLevelGenerator(long seed) {
        this.random = new Random(seed);
    }

    public List<List<String>> generate(int targetCols, int targetRows) {
        System.out.println("=== WFC 레벨 생성 시작 ===");
        
        int logicalCols = Math.max(14, targetCols / SCALE_FACTOR);
        int logicalRows = Math.max(14, targetRows / SCALE_FACTOR);
        
        System.out.println("논리 크기: " + logicalCols + " x " + logicalRows);
        
        char[][] logicalMaze = generateLogicalMaze(logicalCols, logicalRows);
        char[][] upscaledMaze = upscaleMaze(logicalMaze);
        
        System.out.println("업스케일 완료: " + upscaledMaze[0].length + " x " + upscaledMaze.length);
        
        List<List<String>> result = toListFormat(upscaledMaze);
        
        System.out.println("=== 레벨 생성 완료 ===");
        printPreview(result);
        
        return result;
    }

    private char[][] generateLogicalMaze(int cols, int rows) {
        int groupCols = Math.max(3, cols / 6);
        int groupRows = Math.max(4, rows / 4);
        
        WFCGrid wfc = new WFCGrid(groupCols, groupRows);
        wfc.collapse();
        char[][] grid = wfc.toCharGrid();
        
        removeDeadEnds(grid);
        ensureConnectivity(grid);
        placeEntities(grid);
        
        return grid;
    }

    private char[][] upscaleMaze(char[][] logical) {
        int logicalRows = logical.length;
        int logicalCols = logical[0].length;
        
        int scaledRows = logicalRows * SCALE_FACTOR;
        int scaledCols = logicalCols * SCALE_FACTOR;
        
        char[][] scaled = new char[scaledRows][scaledCols];
        
        for (int r = 0; r < scaledRows; r++) {
            Arrays.fill(scaled[r], ' ');
        }
        
        for (int lr = 0; lr < logicalRows; lr++) {
            for (int lc = 0; lc < logicalCols; lc++) {
                char cell = logical[lr][lc];
                expandCell(scaled, lr, lc, cell, logical);
            }
        }
        
        return scaled;
    }

    private void expandCell(char[][] scaled, int logicalRow, int logicalCol, 
                           char cell, char[][] logical) {
        int startRow = logicalRow * SCALE_FACTOR;
        int startCol = logicalCol * SCALE_FACTOR;
        int logicalRows = logical.length;
        int logicalCols = logical[0].length;
        
        if (cell == 'x') {
            // 벽 → 4x4 전체를 'x'로
            for (int dr = 0; dr < SCALE_FACTOR; dr++) {
                for (int dc = 0; dc < SCALE_FACTOR; dc++) {
                    scaled[startRow + dr][startCol + dc] = 'x';
                }
            }
        } 
        else {
            // 통로 → 4x4 전체가 공백 (통과 가능!)
            
            if (cell == 'P') {
                // 팩맨 → [0][3]
                scaled[startRow + 0][startCol + 3] = 'P';
            }
            else if (cell == 'b') {
                // 블링키 → [0][3]
                scaled[startRow + 0][startCol + 3] = 'b';
            }
            else if (cell == 'p' || cell == 'i' || cell == 'c') {
                // 나머지 유령 → [1][0]
                scaled[startRow + 1][startCol + 0] = cell;
            }
            else if (cell == ' ') {
                // 빈 통로 → 펠릿 배치
                boolean hasRight = logicalCol < logicalCols - 1 && 
                                  logical[logicalRow][logicalCol + 1] != 'x';
                boolean hasDown = logicalRow < logicalRows - 1 && 
                                 logical[logicalRow + 1][logicalCol] != 'x';
                boolean hasLeft = logicalCol > 0 && 
                                 logical[logicalRow][logicalCol - 1] != 'x';
                boolean hasUp = logicalRow > 0 && 
                               logical[logicalRow - 1][logicalCol] != 'x';
                
                // 가로 연결: [1][1], [1][3]
                if (hasRight || hasLeft) {
                    scaled[startRow + 1][startCol + 1] = '.';
                    scaled[startRow + 1][startCol + 3] = '.';
                }
                
                // 세로 연결: [1][1], [3][1]
                if (hasDown || hasUp) {
                    if (scaled[startRow + 1][startCol + 1] != '.') {
                        scaled[startRow + 1][startCol + 1] = '.';
                    }
                    scaled[startRow + 3][startCol + 1] = '.';
                }
            }
            else if (cell == 'o') {
                // 슈퍼 펠릿 → [1][1]
                scaled[startRow + 1][startCol + 1] = 'o';
            }
        }
    }

    private void placeEntities(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        
        List<int[]> emptySpaces = new ArrayList<>();
        for (int r = 2; r < rows - 2; r++) {
            for (int c = 2; c < cols - 2; c++) {
                if (grid[r][c] == ' ') {
                    emptySpaces.add(new int[]{r, c});
                }
            }
        }
        
        if (emptySpaces.isEmpty()) return;
        Collections.shuffle(emptySpaces, random);
        
        // 팩맨 중앙 배치
        int midR = rows / 2;
        int midC = cols / 2;
        int[] pacPos = emptySpaces.stream()
            .min(Comparator.comparingInt(p -> 
                Math.abs(p[0] - midR) + Math.abs(p[1] - midC)))
            .orElse(emptySpaces.get(0));
        
        grid[pacPos[0]][pacPos[1]] = 'P';
        
        // ✅ 팩맨 주변 4방향을 공백으로 만들기!
        clearSpaceAround(grid, pacPos[0], pacPos[1]);
        
        // 유령 4마리
        char[] ghosts = {'b', 'p', 'i', 'c'};
        int ghostCount = 0;
        
        for (int[] pos : emptySpaces) {
            if (ghostCount >= 4) break;
            int r = pos[0], c = pos[1];
            
            int dist = Math.abs(r - pacPos[0]) + Math.abs(c - pacPos[1]);
            if (dist > 3 && grid[r][c] == ' ') {
                grid[r][c] = ghosts[ghostCount++];
                // ✅ 유령 주변도 공백으로!
                clearSpaceAround(grid, r, c);
            }
        }
        
        // 슈퍼 펠릿
        placeSuperPellets(grid);
    }
    
    /**
     * 엔티티 주변 4방향을 공백으로 만들기
     * (업스케일 후 32x32 엔티티가 통과할 수 있도록)
     */
    private void clearSpaceAround(char[][] grid, int r, int c) {
        int[][] dirs = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];
            if (nr >= 0 && nr < grid.length && nc >= 0 && nc < grid[0].length) {
                if (grid[nr][nc] == 'x') {
                    grid[nr][nc] = ' ';  // 벽을 공백으로 변경!
                }
            }
        }
    }

    private void placeSuperPellets(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        
        placeSuperPelletNear(grid, 3, 3);
        placeSuperPelletNear(grid, cols - 4, 3);
        placeSuperPelletNear(grid, 3, rows - 4);
        placeSuperPelletNear(grid, cols - 4, rows - 4);
    }

    private void placeSuperPelletNear(char[][] grid, int targetCol, int targetRow) {
        for (int radius = 0; radius < 8; radius++) {
            for (int dr = -radius; dr <= radius; dr++) {
                for (int dc = -radius; dc <= radius; dc++) {
                    int r = targetRow + dr;
                    int c = targetCol + dc;
                    
                    if (r >= 0 && r < grid.length && c >= 0 && c < grid[0].length &&
                        grid[r][c] == ' ') {
                        grid[r][c] = 'o';
                        return;
                    }
                }
            }
        }
    }

    enum TileShape {
        EMPTY(new boolean[][]{{false,false,false},{false,false,false},{false,false,false}}, false,false,false,false),
        HORIZONTAL(new boolean[][]{{false,false,false},{true,true,true},{false,false,false}}, false,true,false,true),
        VERTICAL(new boolean[][]{{false,true,false},{false,true,false},{false,true,false}}, true,false,true,false),
        CROSS(new boolean[][]{{false,true,false},{true,true,true},{false,true,false}}, true,true,true,true),
        T_UP(new boolean[][]{{false,true,false},{true,true,true},{false,false,false}}, true,true,false,true),
        T_DOWN(new boolean[][]{{false,false,false},{true,true,true},{false,true,false}}, false,true,true,true),
        T_LEFT(new boolean[][]{{false,true,false},{false,true,true},{false,true,false}}, true,true,true,false),
        T_RIGHT(new boolean[][]{{false,true,false},{true,true,false},{false,true,false}}, true,false,true,true),
        CORNER_NE(new boolean[][]{{false,true,false},{false,true,true},{false,false,false}}, true,true,false,false),
        CORNER_NW(new boolean[][]{{false,true,false},{true,true,false},{false,false,false}}, true,false,false,true),
        CORNER_SE(new boolean[][]{{false,false,false},{false,true,true},{false,true,false}}, false,true,true,false),
        CORNER_SW(new boolean[][]{{false,false,false},{true,true,false},{false,true,false}}, false,false,true,true);

        final boolean[][] pattern;
        final boolean n, e, s, w;
        
        TileShape(boolean[][] p, boolean n, boolean e, boolean s, boolean w) {
            this.pattern = p;
            this.n = n; this.e = e; this.s = s; this.w = w;
        }
        
        boolean hasConnection(int dir) {
            return switch(dir) {
                case 0 -> n; case 1 -> s; case 2 -> w; case 3 -> e;
                default -> false;
            };
        }
    }

    private class WFCGrid {
        final int cols, rows;
        final TileGroup[][] groups;
        
        WFCGrid(int cols, int rows) {
            this.cols = cols;
            this.rows = rows;
            this.groups = new TileGroup[rows][cols];
            
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    groups[r][c] = new TileGroup();
                }
            }
        }
        
        void collapse() {
            for (int r = 0; r < rows; r++) {
                groups[r][0].remove(false, false, false, true);
                groups[r][cols-1].remove(false, true, false, false);
            }
            for (int c = 0; c < cols; c++) {
                groups[0][c].remove(true, false, false, false);
                groups[rows-1][c].remove(false, false, true, false);
            }
            
            List<int[]> positions = new ArrayList<>();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (!groups[r][c].isFixed()) {
                        positions.add(new int[]{c, r});
                    }
                }
            }
            Collections.shuffle(positions, random);
            
            for (int[] p : positions) {
                int c = p[0], r = p[1];
                if (!groups[r][c].isFixed()) {
                    groups[r][c].fix(groups[r][c].pick(random));
                    propagate(c, r);
                }
            }
        }
        
        void propagate(int c, int r) {
            Queue<int[]> q = new LinkedList<>();
            Set<String> visited = new HashSet<>();
            q.offer(new int[]{c, r});
            visited.add(c + "," + r);
            
            while (!q.isEmpty()) {
                int[] cur = q.poll();
                int cx = cur[0], cy = cur[1];
                
                int[][] dirs = {{0,-1}, {0,1}, {-1,0}, {1,0}};
                for (int d = 0; d < 4; d++) {
                    int nx = cx + dirs[d][0], ny = cy + dirs[d][1];
                    if (nx < 0 || nx >= cols || ny < 0 || ny >= rows) continue;
                    
                    if (constrain(cx, cy, nx, ny, d)) {
                        String key = nx + "," + ny;
                        if (!visited.contains(key)) {
                            q.offer(new int[]{nx, ny});
                            visited.add(key);
                        }
                    }
                }
            }
        }
        
        boolean constrain(int cx, int cy, int nx, int ny, int dir) {
            TileGroup cur = groups[cy][cx];
            TileGroup neighbor = groups[ny][nx];
            if (neighbor.isFixed()) return false;
            
            Set<TileShape> valid = new HashSet<>();
            for (TileShape s : cur.shapes) {
                boolean has = s.hasConnection(dir);
                int oppositeDir = (dir + 1) % 2 + (dir / 2) * 2;
                
                for (TileShape t : TileShape.values()) {
                    boolean can = t.hasConnection(oppositeDir);
                    if (has == can) valid.add(t);
                }
            }
            
            return neighbor.shapes.retainAll(valid);
        }
        
        char[][] toCharGrid() {
            int logicalRows = rows * 3;
            int logicalCols = cols * 3 * 2;
            boolean[][] logical = new boolean[logicalRows][logicalCols];
            
            for (int gr = 0; gr < rows; gr++) {
                for (int gc = 0; gc < cols; gc++) {
                    TileShape shape = groups[gr][gc].shape;
                    for (int dr = 0; dr < 3; dr++) {
                        for (int dc = 0; dc < 3; dc++) {
                            logical[gr * 3 + dr][gc * 3 + dc] = shape.pattern[dr][dc];
                        }
                    }
                }
            }
            
            int mid = cols * 3;
            for (int r = 0; r < logicalRows; r++) {
                for (int c = 0; c < mid; c++) {
                    logical[r][logicalCols - 1 - c] = logical[r][c];
                }
            }
            
            remove2x2Open(logical);
            
            int gridRows = logicalRows + 2;
            int gridCols = logicalCols + 2;
            char[][] grid = new char[gridRows][gridCols];
            
            for (int r = 0; r < gridRows; r++) {
                Arrays.fill(grid[r], ' ');
            }
            
            for (int c = 0; c < gridCols; c++) {
                grid[0][c] = 'x';
                grid[gridRows - 1][c] = 'x';
            }
            for (int r = 0; r < gridRows; r++) {
                grid[r][0] = 'x';
                grid[r][gridCols - 1] = 'x';
            }
            
            for (int r = 0; r < logicalRows; r++) {
                for (int c = 0; c < logicalCols; c++) {
                    grid[r + 1][c + 1] = logical[r][c] ? ' ' : 'x';
                }
            }
            
            return grid;
        }
        
        void remove2x2Open(boolean[][] maze) {
            for (int r = 0; r < maze.length - 1; r++) {
                for (int c = 0; c < maze[0].length - 1; c++) {
                    if (maze[r][c] && maze[r+1][c] && maze[r][c+1] && maze[r+1][c+1]) {
                        int choice = random.nextInt(4);
                        switch (choice) {
                            case 0 -> maze[r][c] = false;
                            case 1 -> maze[r+1][c] = false;
                            case 2 -> maze[r][c+1] = false;
                            case 3 -> maze[r+1][c+1] = false;
                        }
                    }
                }
            }
        }
    }

    private static class TileGroup {
        Set<TileShape> shapes;
        TileShape shape;
        
        TileGroup() {
            shapes = new HashSet<>(Arrays.asList(TileShape.values()));
            shapes.remove(TileShape.EMPTY);
        }
        
        boolean isFixed() { return shape != null; }
        void fix(TileShape s) { shape = s; shapes.clear(); shapes.add(s); }
        
        TileShape pick(Random r) {
            List<TileShape> list = new ArrayList<>(shapes);
            return list.isEmpty() ? TileShape.EMPTY : list.get(r.nextInt(list.size()));
        }
        
        void remove(boolean n, boolean e, boolean s, boolean w) {
            shapes.removeIf(sh -> (n && sh.n) || (e && sh.e) || (s && sh.s) || (w && sh.w));
        }
    }

    private void removeDeadEnds(char[][] grid) {
        for (int iter = 0; iter < 3; iter++) {
            for (int r = 1; r < grid.length - 1; r++) {
                for (int c = 1; c < grid[0].length - 1; c++) {
                    if (grid[r][c] == 'x' && countOpen(grid, r, c) >= 3) {
                        grid[r][c] = ' ';
                    }
                }
            }
        }
    }
    
    private int countOpen(char[][] grid, int r, int c) {
        int cnt = 0;
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (nr >= 0 && nr < grid.length && nc >= 0 && nc < grid[0].length && grid[nr][nc] == ' ') {
                cnt++;
            }
        }
        return cnt;
    }

    private void ensureConnectivity(char[][] grid) {
        for (int iter = 0; iter < 5; iter++) {
            Set<String> main = findConnected(grid);
            if (main == null) break;
            
            boolean found = false;
            for (int r = 0; r < grid.length && !found; r++) {
                for (int c = 0; c < grid[0].length && !found; c++) {
                    if (grid[r][c] == ' ' && !main.contains(r + "," + c)) {
                        connectTo(grid, main, r, c);
                        found = true;
                    }
                }
            }
            if (!found) break;
        }
    }
    
    private Set<String> findConnected(char[][] grid) {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == ' ') return flood(grid, r, c);
            }
        }
        return null;
    }
    
    private Set<String> flood(char[][] grid, int sr, int sc) {
        Set<String> set = new HashSet<>();
        Queue<int[]> q = new LinkedList<>();
        q.offer(new int[]{sr, sc});
        set.add(sr + "," + sc);
        
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        while (!q.isEmpty()) {
            int[] pos = q.poll();
            for (int[] d : dirs) {
                int nr = pos[0] + d[0], nc = pos[1] + d[1];
                String key = nr + "," + nc;
                if (nr >= 0 && nr < grid.length && nc >= 0 && nc < grid[0].length &&
                    grid[nr][nc] == ' ' && !set.contains(key)) {
                    set.add(key);
                    q.offer(new int[]{nr, nc});
                }
            }
        }
        return set;
    }
    
    private void connectTo(char[][] grid, Set<String> main, int r, int c) {
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] d : dirs) {
            for (int dist = 1; dist < 30; dist++) {
                int nr = r + d[0] * dist, nc = c + d[1] * dist;
                if (nr < 0 || nr >= grid.length || nc < 0 || nc >= grid[0].length) break;
                
                if (main.contains(nr + "," + nc)) {
                    for (int s = 0; s <= dist; s++) {
                        grid[r + d[0] * s][c + d[1] * s] = ' ';
                    }
                    return;
                }
            }
        }
    }

    private List<List<String>> toListFormat(char[][] grid) {
        List<List<String>> result = new ArrayList<>();
        for (char[] row : grid) {
            List<String> rowList = new ArrayList<>();
            for (char ch : row) {
                rowList.add(String.valueOf(ch));
            }
            result.add(rowList);
        }
        return result;
    }
    
    private void printPreview(List<List<String>> data) {
        System.out.println("\n=== 레벨 미리보기 ===");
        for (int r = 0; r < Math.min(30, data.size()); r++) {
            for (int c = 0; c < Math.min(60, data.get(0).size()); c++) {
                System.out.print(data.get(r).get(c));
            }
            if (data.get(0).size() > 60) System.out.print("...");
            System.out.println();
        }
        if (data.size() > 30) System.out.println("... (" + (data.size() - 30) + " more lines)");
        System.out.println("===================\n");
    }
}
package game.utils;

import java.util.*;

public class RandomLevelGenerator {

    private final Random random;

    public RandomLevelGenerator() {
        this.random = new Random();
    }

    public RandomLevelGenerator(long seed) {
        this.random = new Random(seed);
    }

    public List<List<String>> generate(int targetCols, int targetRows) {
        int groupCols = Math.max(4, (targetCols / 2) / 3);
        int groupRows = Math.max(5, targetRows / 3);

        WFCGrid wfc = new WFCGrid(groupCols, groupRows);
        wfc.collapse();

        char[][] grid = wfc.buildMacroGridWithCenterPath();

        placeEntities(grid);
        placePelletsAndPowerPellets(grid);

        return toList(grid);
    }

    enum TileShape {
        EMPTY(new boolean[][]{{false,false,false},{false,false,false},{false,false,false}}, false,false,false,false),
        HORIZONTAL(new boolean[][]{{false,false,false},{true,true,true},{false,false,false}}, false,true,false,true),
        VERTICAL(new boolean[][]{{false,true,false},{false,true,false},{false,true,false}}, true,false,true,false),
        PLUS(new boolean[][]{{false,true,false},{true,true,true},{false,true,false}}, true,true,true,true),
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
                groups[r][cols - 1].remove(false, true, false, false);
            }
            for (int c = 0; c < cols; c++) {
                groups[0][c].remove(true, false, false, false);
                groups[rows - 1][c].remove(false, false, true, false);
            }

            List<int[]> positions = new ArrayList<>();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    positions.add(new int[]{c, r});
                }
            }
            Collections.shuffle(positions, random);

            for (int[] p : positions) {
                int cx = p[0], cy = p[1];
                if (!groups[cy][cx].isFixed()) {
                    TileShape s = groups[cy][cx].pick(random);
                    groups[cy][cx].fix(s);
                    propagate(cx, cy);
                }
            }
        }

        void propagate(int sx, int sy) {
            Queue<int[]> q = new LinkedList<>();
            Set<String> visited = new HashSet<>();
            q.offer(new int[]{sx, sy});
            visited.add(sx + "," + sy);

            int[][] dirs = {{0,-1}, {0,1}, {-1,0}, {1,0}};

            while (!q.isEmpty()) {
                int[] cur = q.poll();
                int cx = cur[0], cy = cur[1];

                for (int d = 0; d < 4; d++) {
                    int nx = cx + dirs[d][0];
                    int ny = cy + dirs[d][1];
                    if (nx < 0 || nx >= cols || ny < 0 || ny >= rows) continue;

                    if (constrain(cx, cy, nx, ny, d)) {
                        String key = nx + "," + ny;
                        if (!visited.contains(key)) {
                            visited.add(key);
                            q.offer(new int[]{nx, ny});
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
                boolean has = switch (dir) {
                    case 0 -> s.n;
                    case 1 -> s.s;
                    case 2 -> s.w;
                    case 3 -> s.e;
                    default -> false;
                };

                for (TileShape t : TileShape.values()) {
                    boolean can = switch (dir) {
                        case 0 -> t.s;
                        case 1 -> t.n;
                        case 2 -> t.e;
                        case 3 -> t.w;
                        default -> false;
                    };
                    if (has == can) {
                        valid.add(t);
                    }
                }
            }

            return neighbor.shapes.retainAll(valid);
        }

        char[][] buildMacroGridWithCenterPath() {
            int macroRows = rows * 6;
            int macroCols = (cols * 2) * 6;
            char[][] result = new char[macroRows][macroCols];

            for (int r = 0; r < macroRows; r++) {
                Arrays.fill(result[r], 'x');
            }

            // 왼쪽 절반 생성
            for (int gr = 0; gr < rows; gr++) {
                for (int gc = 0; gc < cols; gc++) {
                    TileShape shape = groups[gr][gc].shape;
                    if (shape == null) shape = TileShape.EMPTY;

                    int baseR = gr * 6;
                    int baseC = gc * 6;

                    writeMacroTile(result, baseR, baseC,
                            shape.n, shape.e, shape.s, shape.w);
                }
            }

            // 중앙 경로 생성
            ensureCenterPath(result, cols);

            // 오른쪽 절반 미러링 (실제 그리드 복사)
            int totalGroupCols = cols * 2;
            int leftBoundary = cols * 6;
            
            for (int gr = 0; gr < rows; gr++) {
                for (int gc = 0; gc < cols; gc++) {
                    int mirrorGc = totalGroupCols - 1 - gc;
                    
                    int srcBaseR = gr * 6;
                    int srcBaseC = gc * 6;
                    int dstBaseR = gr * 6;
                    int dstBaseC = mirrorGc * 6;
                    
                    if (dstBaseC >= leftBoundary) {
                        for (int dr = 0; dr < 6; dr++) {
                            for (int dc = 0; dc < 6; dc++) {
                                char ch = result[srcBaseR + dr][srcBaseC + dc];
                                int mirrorDc = 5 - dc;
                                result[dstBaseR + dr][dstBaseC + mirrorDc] = ch;
                            }
                        }
                    }
                }
            }

            return result;
        }

        private void ensureCenterPath(char[][] grid, int leftGroupCols) {
            int macroRows = grid.length;
            int centerMacroCol = leftGroupCols - 1;
            
            int minConnections = 3;
            List<Integer> candidateGroupRows = new ArrayList<>();
            
            for (int gr = 1; gr < rows - 1; gr++) {
                candidateGroupRows.add(gr);
            }
            
            Collections.shuffle(candidateGroupRows, random);
            
            int connected = 0;
            for (int i = 0; i < candidateGroupRows.size() && connected < minConnections; i++) {
                int gr = candidateGroupRows.get(i);
                int baseR = gr * 6;
                int baseC = centerMacroCol * 6;
                
                int eCol = baseC + 5;
                for (int r = 1; r <= 4; r++) {
                    if (baseR + r < macroRows) {
                        grid[baseR + r][eCol] = ' ';
                    }
                }
                
                for (int r = 1; r <= 4; r++) {
                    for (int c = 1; c <= 4; c++) {
                        if (baseR + r < macroRows && baseC + c < eCol) {
                            grid[baseR + r][baseC + c] = ' ';
                        }
                    }
                }
                
                connected++;
            }
        }
    }

    private static class TileGroup {
        Set<TileShape> shapes = new HashSet<>();
        TileShape shape;

        TileGroup() {
            shapes.addAll(Arrays.asList(TileShape.values()));
            shapes.remove(TileShape.EMPTY);
        }

        boolean isFixed() {
            return shape != null;
        }

        void fix(TileShape s) {
            this.shape = s;
            shapes.clear();
            shapes.add(s);
        }

        TileShape pick(Random r) {
            if (shapes.isEmpty()) return TileShape.EMPTY;
            List<TileShape> list = new ArrayList<>(shapes);
            return list.get(r.nextInt(list.size()));
        }

        void remove(boolean n, boolean e, boolean s, boolean w) {
            shapes.removeIf(sh ->
                    (n && sh.n) ||
                    (e && sh.e) ||
                    (s && sh.s) ||
                    (w && sh.w));
        }
    }

    private void writeMacroTile(char[][] dst, int baseR, int baseC,
                                boolean n, boolean e, boolean s, boolean w) {
        for (int r = 1; r <= 4; r++) {
            for (int c = 1; c <= 4; c++) {
                dst[baseR + r][baseC + c] = ' ';
            }
        }

        if (n) {
            int r = 0;
            for (int c = 1; c <= 4; c++) {
                dst[baseR + r][baseC + c] = ' ';
            }
        }
        if (s) {
            int r = 5;
            for (int c = 1; c <= 4; c++) {
                dst[baseR + r][baseC + c] = ' ';
            }
        }
        if (w) {
            int c = 0;
            for (int r = 1; r <= 4; r++) {
                dst[baseR + r][baseC + c] = ' ';
            }
        }
        if (e) {
            int c = 5;
            for (int r = 1; r <= 4; r++) {
                dst[baseR + r][baseC + c] = ' ';
            }
        }
    }

    private void placeEntities(char[][] grid) {
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

    private boolean isWalkable(char[][] grid, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length) return false;
        return grid[r][c] != 'x';
    }

    private boolean inBounds(char[][] grid, int r, int c) {
        return r >= 0 && r < grid.length && c >= 0 && c < grid[0].length;
    }

    private int[] findClosestPath(char[][] grid, int sr, int sc) {
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

    // ===================== 펠릿 배치 (새로운 규칙) =====================

    private void placePelletsAndPowerPellets(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        int macroRows = rows / 6;
        int macroCols = cols / 6;

        // 기존 펠릿 정리
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '.' || grid[r][c] == 'o') {
                    grid[r][c] = ' ';
                }
            }
        }

        // ✅ 새로운 펠릿 배치 규칙
        for (int gr = 0; gr < macroRows; gr++) {
            for (int gc = 0; gc < macroCols; gc++) {
                int baseR = gr * 6;
                int baseC = gc * 6;

                if (baseR + 5 >= rows || baseC + 5 >= cols) continue;

                // 방이 있는지 확인
                boolean hasRoom = false;
                for (int r = 1; r <= 4 && !hasRoom; r++) {
                    for (int c = 1; c <= 4 && !hasRoom; c++) {
                        if (grid[baseR + r][baseC + c] != 'x') {
                            hasRoom = true;
                        }
                    }
                }
                if (!hasRoom) continue;

                // 통로 확인
                boolean hasN = checkPath(grid, baseR + 0, baseC + 1, baseC + 4);
                boolean hasS = checkPath(grid, baseR + 5, baseC + 1, baseC + 4);
                boolean hasW = checkPathVertical(grid, baseC + 0, baseR + 1, baseR + 4);
                boolean hasE = checkPathVertical(grid, baseC + 5, baseR + 1, baseR + 4);

                // ✅ 중앙 (2,2)에는 항상 펠릿
                tryPlacePellet(grid, baseR + 2, baseC + 2);

                // ✅ 위쪽 연결 → (0,2)
                if (hasN) {
                    tryPlacePellet(grid, baseR + 0, baseC + 2);
                }

                // ✅ 왼쪽 연결 → (2,0)
                if (hasW) {
                    tryPlacePellet(grid, baseR + 2, baseC + 0);
                }

                // ✅ 아래쪽 연결 → (5,2) 실제로는 4,2가 맞음
                if (hasS) {
                    tryPlacePellet(grid, baseR + 5, baseC + 2);
                }

                // ✅ 오른쪽 연결 → (2,5) 실제로는 2,4가 맞음  
                if (hasE) {
                    tryPlacePellet(grid, baseR + 2, baseC + 5);
                }
            }
        }

        // 엔티티 주변 펠릿 제거
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char ch = grid[r][c];
                if (ch == 'P' || ch == 'b' || ch == 'p' || ch == 'i' || ch == 'c') {
                    clearPelletAround(grid, r, c, 2);
                }
            }
        }

        // ✅ 슈퍼펠릿: 기존 펠릿 위치 중에서 선택
        placePowerPelletsOnExistingPellets(grid, 4);
    }

    private boolean checkPath(char[][] grid, int row, int colStart, int colEnd) {
        for (int c = colStart; c <= colEnd; c++) {
            if (row >= 0 && row < grid.length && c >= 0 && c < grid[0].length) {
                if (grid[row][c] != 'x') {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkPathVertical(char[][] grid, int col, int rowStart, int rowEnd) {
        for (int r = rowStart; r <= rowEnd; r++) {
            if (r >= 0 && r < grid.length && col >= 0 && col < grid[0].length) {
                if (grid[r][col] != 'x') {
                    return true;
                }
            }
        }
        return false;
    }

    private void tryPlacePellet(char[][] grid, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length) return;
        if (grid[r][c] == ' ') {
            grid[r][c] = '.';
        }
    }

    private void clearPelletAround(char[][] grid, int r, int c, int radius) {
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

    /**
     * ✅ 슈퍼펠릿을 펠릿 위치에만 배치
     */
    private void placePowerPelletsOnExistingPellets(char[][] grid, int count) {
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
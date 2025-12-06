package game.map;

import java.util.*;

/**
 * Wave Function Collapse 알고리즘을 구현한 그리드 클래스
 * 타일 기반 맵 생성을 담당
 */
public class WFCGrid {
    private final int cols, rows;
    private final TileGroup[][] groups;
    private final Random random;

    public WFCGrid(int cols, int rows, Random random) {
        this.cols = cols;
        this.rows = rows;
        this.random = random;
        this.groups = new TileGroup[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                groups[r][c] = new TileGroup();
            }
        }
    }

    /**
     * WFC 알고리즘 실행 - 모든 타일을 확정
     */
    public void collapse() {
        // 경계 제약 조건 설정
        for (int r = 0; r < rows; r++) {
            groups[r][0].remove(false, false, false, true);
            groups[r][cols - 1].remove(false, true, false, false);
        }
        for (int c = 0; c < cols; c++) {
            groups[0][c].remove(true, false, false, false);
            groups[rows - 1][c].remove(false, false, true, false);
        }

        // 랜덤 순서로 타일 확정
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

    /**
     * 타일맵의 연결성 검증
     */
    public boolean isConnected() {
        return MapConnectivityValidator.validateTileConnectivity(groups, rows, cols);
    }

    /**
     * 제약 조건 전파
     */
    private void propagate(int sx, int sy) {
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

    /**
     * 이웃 타일에 제약 조건 적용
     */
    private boolean constrain(int cx, int cy, int nx, int ny, int dir) {
        TileGroup cur = groups[cy][cx];
        TileGroup neighbor = groups[ny][nx];
        if (neighbor.isFixed()) return false;

        Set<TileShape> valid = new HashSet<>();

        for (TileShape s : cur.getShapes()) {
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

        return neighbor.retainAll(valid);
    }

    /**
     * 타일맵을 실제 게임 그리드로 변환 (중앙 경로 포함)
     */
    public char[][] buildMacroGridWithCenterPath() {
        int macroRows = rows * 6;
        int macroCols = (cols * 2) * 6;
        char[][] result = new char[macroRows][macroCols];

        for (int r = 0; r < macroRows; r++) {
            Arrays.fill(result[r], 'x');
        }

        // 왼쪽 절반 생성
        for (int gr = 0; gr < rows; gr++) {
            for (int gc = 0; gc < cols; gc++) {
                TileShape shape = groups[gr][gc].getShape();
                if (shape == null) shape = TileShape.EMPTY;

                int baseR = gr * 6;
                int baseC = gc * 6;

                writeMacroTile(result, baseR, baseC, shape.n, shape.e, shape.s, shape.w);
            }
        }

        // 중앙 경로 생성
        ensureCenterPath(result, cols);

        // 오른쪽 절반 미러링
        mirrorRightHalf(result, cols);

        return result;
    }

    /**
     * 매크로 타일을 그리드에 작성
     */
    private void writeMacroTile(char[][] dst, int baseR, int baseC, boolean n, boolean e, boolean s, boolean w) {
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

    /**
     * 중앙에 좌우를 연결하는 경로 생성
     */
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

    /**
     * 왼쪽 절반을 오른쪽으로 미러링
     */
    private void mirrorRightHalf(char[][] result, int leftGroupCols) {
        int totalGroupCols = leftGroupCols * 2;
        int leftBoundary = leftGroupCols * 6;

        for (int gr = 0; gr < rows; gr++) {
            for (int gc = 0; gc < leftGroupCols; gc++) {
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
    }
}
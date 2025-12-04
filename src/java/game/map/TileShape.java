package game.map;



public enum TileShape {
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

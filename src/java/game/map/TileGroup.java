package game.map;

import java.util.*;

/**
 * WFC 알고리즘에서 사용되는 타일 그룹
 * 가능한 타일 형태들의 집합을 관리하고, 최종적으로 하나의 타일로 확정됨
 */
public class TileGroup {
    private Set<TileShape> shapes = new HashSet<>();
    private TileShape shape;

    public TileGroup() {
        shapes.addAll(Arrays.asList(TileShape.values()));
        shapes.remove(TileShape.EMPTY);
    }

    /**
     * 타일이 확정되었는지 확인
     */
    public boolean isFixed() {
        return shape != null;
    }

    /**
     * 타일을 특정 형태로 확정
     */
    public void fix(TileShape s) {
        this.shape = s;
        shapes.clear();
        shapes.add(s);
    }

    /**
     * 가능한 타일 형태 중 하나를 랜덤으로 선택
     */
    public TileShape pick(Random r) {
        if (shapes.isEmpty()) return TileShape.EMPTY;
        List<TileShape> list = new ArrayList<>(shapes);
        return list.get(r.nextInt(list.size()));
    }

    /**
     * 특정 방향으로 연결이 있는 타일들을 제거
     */
    public void remove(boolean n, boolean e, boolean s, boolean w) {
        shapes.removeIf(sh ->
                (n && sh.n) ||
                (e && sh.e) ||
                (s && sh.s) ||
                (w && sh.w));
    }

    /**
     * 가능한 타일 형태 집합을 제한
     */
    public boolean retainAll(Set<TileShape> validShapes) {
        return shapes.retainAll(validShapes);
    }

    public Set<TileShape> getShapes() {
        return shapes;
    }

    public TileShape getShape() {
        return shape;
    }
}
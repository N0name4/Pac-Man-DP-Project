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
public class WFCMapGenerator extends RandomMapGenerator{

    public WFCMapGenerator() {
        super();
    }

    public WFCMapGenerator(long seed) {
        super(seed);
    }

    @Override
    protected MapGrid createGrid(int cols, int rows, Random random){
        return new WFCGrid(cols, rows, random);
    }
}
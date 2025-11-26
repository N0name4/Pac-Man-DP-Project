package game.levelBuilder;

import java.util.HashMap;
import java.util.Map;

public class RoundTemplateSource {

    private final Map<Integer, RoundTemplate> table = new HashMap<>();

    private final RoundTemplate defaultTemplate;

    public RoundTemplateSource() {
        // Round 1
        table.put(1, new RoundTemplateBuilder()
                .withRoundNumber(1)
                .withSpeedProfile("EasySpeed")
                .withTimerProfile("EasyTimer")
                .withScoreRuleProfile("EasyScore")
                .build());

        // Round 2
        table.put(2, new RoundTemplateBuilder()
                .withRoundNumber(2)
                .withSpeedProfile("EasySpeed")
                .withTimerProfile("NormalTimer")
                .withScoreRuleProfile("EasyScore")
                .build());

        // Round 3
        table.put(3, new RoundTemplateBuilder()
                .withRoundNumber(2)
                .withSpeedProfile("NormalSpeed")
                .withTimerProfile("NormalTimer")
                .build());

        // Round 4
        table.put(4, new RoundTemplateBuilder()
                .withRoundNumber(4)
                .withSpeedProfile("HardSpeed")
                .withTimerProfile("HardTimer")
                .build());

        // Default Round Template Setting
        defaultTemplate = new RoundTemplateBuilder()
                .withRoundNumber(99)
                .withSpeedProfile("HardSpeed")
                .withTimerProfile("HardTimer")
                .withScoreRuleProfile("HardScore")
                .build();
    }

    public RoundTemplate getTemplateForRound(int roundNumber) {
        return table.getOrDefault(roundNumber, defaultTemplate);
    }
}

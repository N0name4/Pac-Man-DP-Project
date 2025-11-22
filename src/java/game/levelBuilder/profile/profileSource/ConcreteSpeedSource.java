package game.levelBuilder.profile.profileSource;

import game.levelBuilder.profile.SpeedProfile;

import java.util.HashMap;
import java.util.Map;

public class ConcreteSpeedSource implements SpeedSource{

    private final Map<String, SpeedProfile> table = new HashMap<>();

    public ConcreteSpeedSource() {
        table.put("EasySpeed", new SpeedProfile(2, 1));
        table.put("NormalSpeed", new SpeedProfile(2, 2));
        table.put("HardSpeed", new SpeedProfile(2, 3));
    }

    @Override
    public SpeedProfile getById(String id) {
        SpeedProfile speedProfile = table.get(id);

        if (speedProfile == null) {
            throw new IllegalArgumentException("Unknown SpeedProfile Exception: " + id);
        }

        return speedProfile;
    }
}

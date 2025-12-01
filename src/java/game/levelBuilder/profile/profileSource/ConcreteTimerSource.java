package game.levelBuilder.profile.profileSource;

import game.levelBuilder.profile.TimerProfile;

import java.util.HashMap;
import java.util.Map;

public class ConcreteTimerSource implements TimerSource{

    private final Map<String, TimerProfile> table = new HashMap<>();

    public ConcreteTimerSource() {
        table.put("EasyTimer", new TimerProfile(60 * 10, 60 * 15, 60 * 15));
        table.put("NormalTimer", new TimerProfile(60 * 15, 60 * 5, 60 * 10));
        table.put("HardTimer", new TimerProfile(60 * 25, 60 * 5, 60 * 5));
    }

    @Override
    public TimerProfile getById(String id) {
        TimerProfile timerProfile = table.get(id);

        if (timerProfile == null) {
            throw new IllegalArgumentException("UnKnown TimerProfile Exception: " + id);
        }

        return timerProfile;
    }
}

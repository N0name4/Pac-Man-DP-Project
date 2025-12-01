package game.levelBuilder.profile.profileSource;

import game.levelBuilder.profile.TimerProfile;

public interface TimerSource {
    TimerProfile getById(String id);
}

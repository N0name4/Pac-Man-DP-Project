package game.levelBuilder.profile.profileSource;

import game.levelBuilder.profile.SpeedProfile;

public interface SpeedSource {
    SpeedProfile getById(String id);
}

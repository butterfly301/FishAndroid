package com.teacher.game.model;

public final class LevelRepository {

	private static final LevelConfig[] LEVELS = new LevelConfig[] {
			new LevelConfig(1, 5, 60, 0, 30, 12, 3),
			new LevelConfig(2, 7, 100, 1, 40, 18, 5),
			new LevelConfig(3, 9, 150, 2, 50, 24, 8),
	};

	private LevelRepository() {}

	public static LevelConfig getLevel(int levelIndex) {
		if (levelIndex < 0) {
			return LEVELS[0];
		}
		if (levelIndex >= LEVELS.length) {
			return LEVELS[LEVELS.length - 1];
		}
		return LEVELS[levelIndex];
	}

	public static int getLevelCount() {
		return LEVELS.length;
	}
}

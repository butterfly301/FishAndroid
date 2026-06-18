package com.teacher.game.model;

public final class LevelRepository {

	private static final int TOTAL_LEVELS = 100;
	private static final LevelConfig[] LEVELS = buildLevels();

	private LevelRepository() {}

	private static LevelConfig[] buildLevels() {
		LevelConfig[] levels = new LevelConfig[TOTAL_LEVELS];
		for (int i = 0; i < TOTAL_LEVELS; i++) {
			levels[i] = buildLevel(i + 1);
		}
		return levels;
	}

	private static LevelConfig buildLevel(int levelNumber) {
		int zeroBased = levelNumber - 1;
		int stage = zeroBased / 10;

		int enemyCount = Math.min(5 + zeroBased / 4, 18);
		int targetScore = 60 + zeroBased * 18 + stage * 20;
		int speedBonus = Math.min(zeroBased / 8, 8);
		int normalChance = Math.min(28 + zeroBased * 2, 52);
		int bigChance = Math.min(12 + stage * 2 + zeroBased / 5, 28);
		int superChance = Math.min(3 + stage + zeroBased / 10, 16);

		// ---- Themed modifiers per stage ----
		float timeLimit = 0;
		int powerUpDropChance = 60;
		PowerUpType[] allowedPowerUps = null; // all

		switch (stage) {
			case 0: // Level 1-10: Tutorial — gentle intro
				timeLimit = 0;
				powerUpDropChance = 45; // more drops
				allowedPowerUps = new PowerUpType[]{PowerUpType.SPEED, PowerUpType.SHIELD};
				break;
			case 1: // Level 11-20: All power-ups, relaxed
				powerUpDropChance = 55;
				break;
			case 2: // Level 21-30: Standard
				powerUpDropChance = 60;
				break;
			case 3: // Level 31-40: Time pressure (+ speed bonus ramps up)
				timeLimit = 50 + (stage % 2) * 10;
				powerUpDropChance = 65;
				break;
			case 4: // Level 41-50: Restricted — no freeze
				timeLimit = 45;
				powerUpDropChance = 70;
				allowedPowerUps = new PowerUpType[]{
						PowerUpType.SPEED, PowerUpType.SHIELD, PowerUpType.BOMB, PowerUpType.LURE};
				break;
			case 5: // Level 51-60: Scarce drops
				timeLimit = 40;
				powerUpDropChance = 85;
				break;
			case 6: // Level 61-70: No shield
				timeLimit = 38;
				powerUpDropChance = 75;
				allowedPowerUps = new PowerUpType[]{
						PowerUpType.SPEED, PowerUpType.FREEZE, PowerUpType.BOMB, PowerUpType.LURE};
				break;
			case 7: // Level 71-80: Tight time, rare drops
				timeLimit = 35;
				powerUpDropChance = 95;
				break;
			case 8: // Level 81-90: Speed + Lure only
				timeLimit = 32;
				powerUpDropChance = 80;
				allowedPowerUps = new PowerUpType[]{PowerUpType.SPEED, PowerUpType.LURE};
				break;
			case 9: // Level 91-100: Final gauntlet
				timeLimit = 30;
				powerUpDropChance = 100;
				allowedPowerUps = new PowerUpType[]{PowerUpType.SHIELD, PowerUpType.BOMB};
				break;
		}

		return new LevelConfig(levelNumber, enemyCount, targetScore, speedBonus,
				normalChance, bigChance, superChance,
				timeLimit, powerUpDropChance, allowedPowerUps);
	}

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

	public static int getPageCount(int levelsPerPage) {
		if (levelsPerPage <= 0) {
			return 1;
		}
		return (LEVELS.length + levelsPerPage - 1) / levelsPerPage;
	}
}

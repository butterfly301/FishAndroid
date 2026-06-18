package com.teacher.game.model;

public class LevelConfig {

	public final int index;
	public final int enemyCount;
	public final int targetScore;
	public final int speedBonus;
	public final int normalChance;
	public final int bigChance;
	public final int superChance;

	/** Time limit in seconds; 0 = no limit. */
	public final float timeLimit;
	/** 1-in-N chance per spawn tick to drop a power-up; 0 = none. */
	public final int powerUpDropChance;
	/** Allowed power-up types; null = all types. */
	public final PowerUpType[] allowedPowerUps;

	public LevelConfig(int index, int enemyCount, int targetScore, int speedBonus,
			int normalChance, int bigChance, int superChance,
			float timeLimit, int powerUpDropChance, PowerUpType[] allowedPowerUps) {
		this.index = index;
		this.enemyCount = enemyCount;
		this.targetScore = targetScore;
		this.speedBonus = speedBonus;
		this.normalChance = normalChance;
		this.bigChance = bigChance;
		this.superChance = superChance;
		this.timeLimit = timeLimit;
		this.powerUpDropChance = powerUpDropChance;
		this.allowedPowerUps = allowedPowerUps;
	}
}

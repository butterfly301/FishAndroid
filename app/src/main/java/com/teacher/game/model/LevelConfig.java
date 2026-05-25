package com.teacher.game.model;

public class LevelConfig {

	public final int index;
	public final int enemyCount;
	public final int targetScore;
	public final int speedBonus;
	public final int normalChance;
	public final int bigChance;
	public final int superChance;

	public LevelConfig(int index, int enemyCount, int targetScore, int speedBonus,
			int normalChance, int bigChance, int superChance) {
		this.index = index;
		this.enemyCount = enemyCount;
		this.targetScore = targetScore;
		this.speedBonus = speedBonus;
		this.normalChance = normalChance;
		this.bigChance = bigChance;
		this.superChance = superChance;
	}
}

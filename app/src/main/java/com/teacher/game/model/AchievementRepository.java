package com.teacher.game.model;

/**
 * Central registry of all achievements.
 * Add new achievements here — AchievementState reads from this.
 */
public final class AchievementRepository {

	private static final AchievementConfig[] ACHIEVEMENTS = {
		new AchievementConfig("ach_eat_fish", 100, AchievementTracker.FISH_EATEN),
		new AchievementConfig("ach_combo", 5, AchievementTracker.COMBO_PEAK),
		new AchievementConfig("ach_item", 30, AchievementTracker.POWERUPS_COLLECTED),
		new AchievementConfig("ach_first_clear", 1, AchievementTracker.UNLOCKED_LEVEL_BOOL),
		new AchievementConfig("ach_all_clear", 99, AchievementTracker.UNLOCKED_LEVEL),
		new AchievementConfig("ach_high_score", 5000, AchievementTracker.HIGH_SCORE),
		new AchievementConfig("ach_endless", 20000, AchievementTracker.ENDLESS_HIGH_SCORE),
		new AchievementConfig("ach_fish_hunter", 500, AchievementTracker.FISH_EATEN),
		new AchievementConfig("ach_combo_expert", 10, AchievementTracker.COMBO_PEAK),
		new AchievementConfig("ach_item_tycoon", 100, AchievementTracker.POWERUPS_COLLECTED),
	};

	private AchievementRepository() {}

	public static AchievementConfig[] getAll() {
		return ACHIEVEMENTS;
	}

	public static int getCount() {
		return ACHIEVEMENTS.length;
	}

	public static AchievementConfig get(int index) {
		return ACHIEVEMENTS[index];
	}
}

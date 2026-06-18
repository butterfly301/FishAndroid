package com.teacher.game.model;

import com.teacher.game.state.L10n;

/**
 * Configuration for a single achievement.
 * Name/description come from L10n via {@link #getName()} / {@link #getDesc()}.
 */
public class AchievementConfig {

	/** L10n key prefix — "ach_eat_fish" → getNameKey()="ach_eat_fish", getDescKey()="ach_eat_fish_desc". */
	public final String id;
	public final int threshold;
	public final AchievementTracker tracker;
	public final String category;  // L10n key for tab category name, e.g. "ach_cat_eat"

	public AchievementConfig(String id, int threshold, AchievementTracker tracker, String category) {
		this.id = id;
		this.threshold = threshold;
		this.tracker = tracker;
		this.category = category;
	}

	public AchievementConfig(String id, int threshold, AchievementTracker tracker) {
		this(id, threshold, tracker, defaultCategory(tracker));
	}

	private static String defaultCategory(AchievementTracker tracker) {
		switch (tracker) {
			case FISH_EATEN:           return "ach_cat_eat";
			case COMBO_PEAK:           return "ach_cat_combo";
			case POWERUPS_COLLECTED:   return "ach_cat_item";
			case UNLOCKED_LEVEL_BOOL:
			case UNLOCKED_LEVEL:       return "ach_cat_level";
			case HIGH_SCORE:
			case ENDLESS_HIGH_SCORE:   return "ach_cat_score";
			default:                   return "ach_cat_other";
		}
	}

	public String getNameKey() {
		return id;
	}

	public String getDescKey() {
		return id + "_desc";
	}

	public String getName() {
		return L10n.get(getNameKey());
	}

	public String getDesc() {
		return L10n.get(getDescKey());
	}
}

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

	public AchievementConfig(String id, int threshold, AchievementTracker tracker) {
		this.id = id;
		this.threshold = threshold;
		this.tracker = tracker;
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

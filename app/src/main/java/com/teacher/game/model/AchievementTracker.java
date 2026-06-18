package com.teacher.game.model;

/**
 * Defines what data source an achievement reads for progress.
 */
public enum AchievementTracker {
	/** Cumulative fish eaten (getFishEaten()). */
	FISH_EATEN,
	/** All-time combo peak (getComboPeak()), capped at threshold. */
	COMBO_PEAK,
	/** Cumulative power-ups collected (getPowerUpsCollected()). */
	POWERUPS_COLLECTED,
	/** Whether unlocked level >= threshold (boolean → 0 or 1). */
	UNLOCKED_LEVEL_BOOL,
	/** Current unlocked level count, capped at threshold. */
	UNLOCKED_LEVEL,
	/** Level mode high score (getHighScore()), capped at threshold. */
	HIGH_SCORE,
	/** Endless mode high score (getEndlessHighScore()), capped at threshold. */
	ENDLESS_HIGH_SCORE,
}

package com.teacher.game.state;

import com.teacher.fish.GameMainActivity;
import com.teacher.game.model.LevelConfig;
import com.teacher.game.state.L10n;

final class RoundTextFormatter {

	private RoundTextFormatter() {
		// no instance
	}

	static String buildRoundEndSubtitle(ModeRules modeRules,
			int score,
			int nextLevelDisplayIndex,
			boolean didClearLevel,
			boolean hasNextLevel,
			RoundStats stats,
			LevelConfig levelConfig) {
		String subtitle = modeRules.getRoundEndSubtitle(score, nextLevelDisplayIndex, didClearLevel, hasNextLevel);
		if (stats != null && stats.comboPeak >= 2) {
			subtitle += "  " + L10n.get("stat_combo_peak", stats.comboPeak);
		}
		return subtitle;
	}

	static String[] buildRoundEndStats(int score, RoundStats stats, boolean endlessMode, LevelConfig config) {
		// Check high score
		int prevHigh = endlessMode ? GameMainActivity.getEndlessHighScore() : GameMainActivity.getHighScore();
		String highScoreNote = "";
		if (!endlessMode && didClearWithScore(score, config)) {
			highScoreNote = L10n.get("stat_goal_achieved");
		} else if (endlessMode && score > prevHigh && prevHigh > 0) {
			highScoreNote = L10n.get("stat_new_record");
		}

		// Stats rows
		java.util.ArrayList<String> rows = new java.util.ArrayList<String>();
		rows.add(L10n.get("stat_score", score, highScoreNote));
		rows.add(L10n.get("stat_fish_eaten", stats.fishEaten));
		rows.add(L10n.get("stat_combo_peak", stats.comboPeak));
		rows.add(L10n.get("stat_powerups", stats.powerUpsCollected));
		rows.add(L10n.get("stat_companion_assists", stats.companionAssists));
		rows.add(L10n.get("stat_survival", formatTime(stats.survivalTime)));

		if (config.timeLimit > 0) {
			rows.add(L10n.get("stat_time_limit", (int)config.timeLimit));
		}

		return rows.toArray(new String[rows.size()]);
	}

	private static boolean didClearWithScore(int score, LevelConfig config) {
		return config.targetScore > 0 && score >= config.targetScore;
	}

	static String formatTime(float seconds) {
		int total = (int) seconds;
		int min = total / 60;
		int sec = total % 60;
		return min > 0 ? L10n.get("time_format_min_sec", min, sec) : L10n.get("time_format_sec", sec);
	}
}

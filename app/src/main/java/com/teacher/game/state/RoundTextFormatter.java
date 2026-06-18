package com.teacher.game.state;

import com.teacher.fish.GameMainActivity;
import com.teacher.game.model.LevelConfig;

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
			subtitle += "  最高连击 x" + stats.comboPeak;
		}
		return subtitle;
	}

	static String[] buildRoundEndStats(int score, RoundStats stats, boolean endlessMode, LevelConfig config) {
		// Check high score
		int prevHigh = endlessMode ? GameMainActivity.getEndlessHighScore() : GameMainActivity.getHighScore();
		String highScoreNote = "";
		if (!endlessMode && didClearWithScore(score, config)) {
			highScoreNote = "  达成目标！";
		} else if (endlessMode && score > prevHigh && prevHigh > 0) {
			highScoreNote = "  新纪录！";
		}

		// Stats rows
		java.util.ArrayList<String> rows = new java.util.ArrayList<String>();
		rows.add("得　分    " + score + "  " + highScoreNote);
		rows.add("吃掉鱼  " + stats.fishEaten + " 条");
		rows.add("最高连击 x" + stats.comboPeak);
		rows.add("收集道具 " + stats.powerUpsCollected + " 个");
		rows.add("同伴助攻 " + stats.companionAssists + " 次");
		rows.add("存活时间 " + formatTime(stats.survivalTime));

		if (config.timeLimit > 0) {
			rows.add("时间限制 " + (int)config.timeLimit + " 秒");
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
		return min > 0 ? min + "分" + sec + "秒" : sec + "秒";
	}
}

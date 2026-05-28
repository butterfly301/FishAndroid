package com.teacher.game.state;

final class RoundTextFormatter {

	private RoundTextFormatter() {
		// no instance
	}

	static String buildRoundEndSubtitle(ModeRules modeRules,
			int score,
			int nextLevelDisplayIndex,
			boolean didClearLevel,
			boolean hasNextLevel,
			RoundStats stats) {
		String subtitle = modeRules.getRoundEndSubtitle(score, nextLevelDisplayIndex, didClearLevel, hasNextLevel);
		if (stats != null && stats.comboPeak >= 2) {
			subtitle += "  最高连击 x" + stats.comboPeak;
		}
		return subtitle;
	}

	static String[] buildRoundEndStats(int score, RoundStats stats) {
		return new String[] {
			"得分    " + score,
			"吃掉鱼  " + stats.fishEaten + " 条",
			"最高连击 x" + stats.comboPeak,
			"收集道具 " + stats.powerUpsCollected + " 个",
			"同伴助攻 " + stats.companionAssists + " 条",
			"存活时间 " + formatTime(stats.survivalTime)
		};
	}

	static String formatTime(float seconds) {
		int total = (int) seconds;
		int min = total / 60;
		int sec = total % 60;
		return min > 0 ? min + "分" + sec + "秒" : sec + "秒";
	}
}

package com.teacher.game.state;

final class ModeRules {

	enum RoundEndAction {
		RESTART,
		NEXT_LEVEL,
		RETURN_MENU
	}

	private final boolean mEndlessMode;

	private ModeRules(boolean endlessMode) {
		mEndlessMode = endlessMode;
	}

	static ModeRules forMode(boolean endlessMode) {
		return new ModeRules(endlessMode);
	}

	boolean didClearLevel(int score, int targetScore) {
		return !mEndlessMode && score >= targetScore;
	}

	boolean isRoundFinished(int life, int score, int targetScore) {
		return life <= 0 || didClearLevel(score, targetScore);
	}

	boolean canGainScore(int score, int targetScore) {
		return mEndlessMode || score < targetScore;
	}

	int clampScore(int score, int targetScore) {
		if (mEndlessMode) {
			return score;
		}
		return Math.min(score, targetScore);
	}

	String[] getRoundEndButtonLabels(boolean didClearLevel, boolean hasNextLevel) {
		if (mEndlessMode) {
			return new String[] {"重新开始", "返回菜单"};
		}
		if (didClearLevel) {
			if (hasNextLevel) {
				return new String[] {"下一关", "重新开始", "返回菜单"};
			}
			return new String[] {"重新开始", "返回菜单"};
		}
		return new String[] {"重试本关", "返回菜单"};
	}

	RoundEndAction resolveRoundEndAction(int buttonIndex, boolean didClearLevel, boolean hasNextLevel) {
		if (buttonIndex < 0) {
			return null;
		}
		if (mEndlessMode) {
			if (buttonIndex == 0) {
				return RoundEndAction.RESTART;
			}
			if (buttonIndex == 1) {
				return RoundEndAction.RETURN_MENU;
			}
			return null;
		}

		if (didClearLevel && hasNextLevel) {
			if (buttonIndex == 0) {
				return RoundEndAction.NEXT_LEVEL;
			}
			if (buttonIndex == 1) {
				return RoundEndAction.RESTART;
			}
			if (buttonIndex == 2) {
				return RoundEndAction.RETURN_MENU;
			}
			return null;
		}

		if (buttonIndex == 0) {
			return RoundEndAction.RESTART;
		}
		if (buttonIndex == 1) {
			return RoundEndAction.RETURN_MENU;
		}
		return null;
	}

	String getRoundEndTitle(boolean didClearLevel, boolean hasNextLevel) {
		if (mEndlessMode) {
			return "无尽结束";
		}
		if (didClearLevel && hasNextLevel) {
			return "本关完成";
		}
		if (didClearLevel) {
			return "全部通关";
		}
		return "挑战失败";
	}

	String getRoundEndSubtitle(int score, int nextLevelDisplayIndex, boolean didClearLevel, boolean hasNextLevel) {
		if (mEndlessMode) {
			return "本次得分 " + score + "，再来挑战更高分";
		}
		if (didClearLevel && hasNextLevel) {
			return "准备进入第 " + nextLevelDisplayIndex + " 关";
		}
		if (didClearLevel) {
			return "恭喜完成全部关卡挑战";
		}
		return "再试一次，看看能不能拿到更高分";
	}

	String getModeLabel(int levelDisplayIndex) {
		if (mEndlessMode) {
			return "无尽模式";
		}
		return "第" + levelDisplayIndex + "关";
	}

	String getScoreLabel(int score, int targetScore) {
		if (mEndlessMode) {
			return "分数 " + score;
		}
		return "分数 " + score + " / " + targetScore;
	}
}

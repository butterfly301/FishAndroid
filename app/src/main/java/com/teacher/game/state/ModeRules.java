package com.teacher.game.state;

import com.teacher.game.state.L10n;

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
			return new String[] {L10n.get("result_restart"), L10n.get("result_menu")};
		}
		if (didClearLevel) {
			if (hasNextLevel) {
				return new String[] {L10n.get("result_next"), L10n.get("result_restart"), L10n.get("result_menu")};
			}
			return new String[] {L10n.get("result_restart"), L10n.get("result_menu")};
		}
		return new String[] {L10n.get("result_retry"), L10n.get("result_menu")};
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
			return L10n.get("result_title_endless_over");
		}
		if (didClearLevel && hasNextLevel) {
			return L10n.get("result_title_clear");
		}
		if (didClearLevel) {
			return L10n.get("result_title_all_clear");
		}
		return L10n.get("result_title_fail");
	}

	String getRoundEndSubtitle(int score, int nextLevelDisplayIndex, boolean didClearLevel, boolean hasNextLevel) {
		if (mEndlessMode) {
			return L10n.get("result_desc_score", score);
		}
		if (didClearLevel && hasNextLevel) {
			return L10n.get("result_desc_next", nextLevelDisplayIndex);
		}
		if (didClearLevel) {
			return L10n.get("result_desc_all_clear");
		}
		return L10n.get("result_desc_retry");
	}

	String getModeLabel(int levelDisplayIndex) {
		if (mEndlessMode) {
			return L10n.get("label_endless");
		}
		return L10n.get("label_level_n", levelDisplayIndex);
	}

	String getScoreLabel(int score, int targetScore) {
		if (mEndlessMode) {
			return L10n.get("label_score", score);
		}
		return L10n.get("label_score_target", score, targetScore);
	}
}

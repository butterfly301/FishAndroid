package com.teacher.game.state;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;
import com.teacher.game.model.AchievementConfig;
import com.teacher.game.model.AchievementRepository;
import com.teacher.game.model.AchievementTracker;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class AchievementState extends State {

	private static final int PANEL_X = 140;
	private static final int PANEL_W = 1000;
	private static final int PANEL_Y = 20;
	private static final int PANEL_H = 660;

	private static final int CARD_X = PANEL_X + 28;
	private static final int CARD_W = PANEL_W - 56;
	private static final int CARD_H = 50;
	private static final int CARD_GAP = 6;
	private static final int ROW_H = CARD_H + CARD_GAP;

	private static final int FIRST_ROW_Y = 140;
	private static final int TITLE_Y = 90;
	private static final int BACK_BTN_Y = 685;

	@Override
	public void init() {
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void render(Painter g) {
		g.drawImage(Assets.menu, 0, 0);

		// Background panel
		g.setColor(Color.argb(180, 6, 32, 64));
		g.fillRoundRect(PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 28);

		// Title field
		g.setColor(Color.argb(130, 255, 255, 255));
		g.fillRoundRect(PANEL_X + 28, PANEL_Y + 44, PANEL_W - 56, 68, 24);

		g.setFont(Typeface.DEFAULT_BOLD, 38);
		g.setColor(Color.WHITE);
		drawCenteredText(g, L10n.get("ach_title"), PANEL_X, PANEL_W, TITLE_Y);

		// Achievement rows — driven by AchievementRepository
		int count = AchievementRepository.getCount();
		for (int i = 0; i < count; i++) {
			drawAchievementRow(g, i);
		}

		// Back button
		int backBtnW = 300;
		int backBtnH = 50;
		int backBtnX = (GameMainActivity.GAME_WIDTH - backBtnW) / 2;
		g.setColor(Color.rgb(106, 191, 245));
		g.fillRoundRect(backBtnX, BACK_BTN_Y, backBtnW, backBtnH, 18);
		g.setFont(Typeface.DEFAULT_BOLD, 26);
		g.setColor(Color.rgb(16, 56, 90));
		drawCenteredText(g, L10n.get("ach_back"), backBtnX, backBtnW, BACK_BTN_Y + 34);
	}

	private void drawAchievementRow(Painter g, int index) {
		AchievementConfig config = AchievementRepository.get(index);
		int y = FIRST_ROW_Y + index * ROW_H;
		boolean earned = isAchievementEarned(config);
		int progress = getAchievementProgress(config);
		int threshold = config.threshold;
		String name = config.getName();
		String desc = config.getDesc();

		// Card background
		g.setColor(earned
				? Color.argb(90, 255, 215, 0)
				: Color.argb(55, 255, 255, 255));
		g.fillRoundRect(CARD_X, y, CARD_W, CARD_H, 12);

		// Achievement icon (✓ / ○)
		g.setFont(Typeface.DEFAULT_BOLD, 24);
		g.setColor(earned ? Color.rgb(255, 215, 0) : Color.argb(120, 200, 200, 200));
		g.drawString(earned ? "\u2713" : "\u25CB", CARD_X + 14, y + 34);

		// Name
		g.setFont(Typeface.DEFAULT_BOLD, 22);
		g.setColor(earned ? Color.rgb(255, 215, 0) : Color.argb(220, 255, 255, 255));
		g.drawString(name, CARD_X + 48, y + 24);

		// Description
		g.setFont(Typeface.SANS_SERIF, 17);
		g.setColor(earned ? Color.argb(255, 255, 240, 190) : Color.argb(180, 200, 220, 240));
		g.drawString(desc, CARD_X + 48, y + 44);

		// Right side: progress bar (unearned) or badge (earned)
		int rightX = CARD_X + CARD_W - 20;

		if (!earned) {
			int barX = rightX - 200;
			int barY = y + 12;
			int barW = 200;
			int barH = 12;
			int fillW = (int)(barW * Math.min((float)progress / threshold, 1.0f));

			g.setColor(Color.argb(80, 0, 0, 0));
			g.fillRoundRect(barX, barY, barW, barH, 6);
			if (fillW > 0) {
				g.setColor(Color.rgb(255, 198, 84));
				g.fillRoundRect(barX, barY, fillW, barH, 6);
			}

			g.setFont(Typeface.SANS_SERIF, 15);
			g.setColor(Color.argb(180, 200, 220, 240));
			g.drawString(progress + "/" + threshold, barX + barW / 2 - 20, barY + 26);
		} else {
			g.setFont(Typeface.DEFAULT_BOLD, 18);
			g.setColor(Color.rgb(255, 215, 0));
			String badge = L10n.get("ach_unlocked");
			float badgeW = g.measureText(badge);
			g.drawString(badge, rightX - (int)badgeW, y + 34);
		}
	}

	private static boolean isAchievementEarned(AchievementConfig config) {
		return getAchievementProgress(config) >= config.threshold;
	}

	private static int getAchievementProgress(AchievementConfig config) {
		switch (config.tracker) {
			case FISH_EATEN:           return GameMainActivity.getFishEaten();
			case COMBO_PEAK:           return Math.min(GameMainActivity.getComboPeak(), config.threshold);
			case POWERUPS_COLLECTED:   return GameMainActivity.getPowerUpsCollected();
			case UNLOCKED_LEVEL_BOOL:  return GameMainActivity.getUnlockedLevel() >= config.threshold ? 1 : 0;
			case UNLOCKED_LEVEL:       return Math.min(GameMainActivity.getUnlockedLevel(), config.threshold);
			case HIGH_SCORE:           return Math.min(GameMainActivity.getHighScore(), config.threshold);
			case ENDLESS_HIGH_SCORE:   return Math.min(GameMainActivity.getEndlessHighScore(), config.threshold);
			default:                   return 0;
		}
	}

	private void drawCenteredText(Painter g, String text, int left, int width, int baselineY) {
		float textWidth = g.measureText(text);
		int textX = left + (int)((width - textWidth) / 2f);
		g.drawString(text, textX, baselineY);
	}

	@Override
	public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
		if (e.getAction() != MotionEvent.ACTION_UP) {
			return true;
		}
		int backBtnW = 300;
		int backBtnH = 50;
		int backBtnX = (GameMainActivity.GAME_WIDTH - backBtnW) / 2;
		if (scaleX >= backBtnX && scaleX <= backBtnX + backBtnW
				&& scaleY >= BACK_BTN_Y && scaleY <= BACK_BTN_Y + backBtnH) {
			setCurrentState(new MenuState());
			return true;
		}
		return true;
	}
}

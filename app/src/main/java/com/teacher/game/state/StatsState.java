package com.teacher.game.state;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class StatsState extends State {

	// ---------- layout constants ----------

	private static final int PANEL_X = 120;
	private static final int PANEL_Y = 40;
	private static final int PANEL_W = 1040;
	private static final int PANEL_H = 640;

	private static final int TITLE_Y = 100;

	private static final int ROW_START_Y = 160;
	private static final int ROW_H = 52;
	private static final int LABEL_X = PANEL_X + 60;

	private static final int BACK_BTN_X = 370;
	private static final int BACK_BTN_Y = 680;
	private static final int BACK_BTN_W = 540;
	private static final int BACK_BTN_H = 50;

	@Override
	public void init() {
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void render(Painter g) {
		g.drawImage(Assets.menu, 0, 0);

		// Dark backdrop
		g.setColor(Color.argb(170, 4, 10, 24));
		g.fillRect(0, 0, GameMainActivity.GAME_WIDTH, GameMainActivity.GAME_HEIGHT);

		// Panel
		g.setColor(Color.argb(200, 10, 36, 66));
		g.fillRoundRect(PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 28);

		// Title
		g.setColor(Color.argb(130, 255, 255, 255));
		g.fillRoundRect(PANEL_X + 28, PANEL_Y + 24, PANEL_W - 56, 64, 20);
		g.setFont(Typeface.DEFAULT_BOLD, 36);
		g.setColor(Color.WHITE);
		drawCenteredText(g, L10n.get("stats_title"), PANEL_X, PANEL_W, TITLE_Y);

		// Stats rows
		int y = ROW_START_Y;
		y = drawStatRow(g, y, L10n.get("stats_games_played"),   valueStr(GameMainActivity.getStatGamesPlayed()));
		y = drawStatRow(g, y, L10n.get("stats_fish_eaten"),     valueStr(GameMainActivity.getFishEaten()));
		y = drawStatRow(g, y, L10n.get("stats_powerups"),       valueStr(GameMainActivity.getPowerUpsCollected()));
		y = drawStatRow(g, y, L10n.get("stats_combo_peak"),     valueStr(GameMainActivity.getComboPeak()));
		y = drawStatRow(g, y, L10n.get("stats_coins_total"),    valueStr(GameMainActivity.getStatTotalCoinsEarned()));
		y = drawStatRow(g, y, L10n.get("stats_coins_balance"),  valueStr(GameMainActivity.getCoins()));
		y = drawStatRow(g, y, L10n.get("stats_survival_total"), formatTime(GameMainActivity.getStatTotalSurvival()));
		y = drawStatRow(g, y, L10n.get("stats_survival_longest"), formatTime(GameMainActivity.getStatLongestSurvival()));

		// High scores
		g.setFont(Typeface.SANS_SERIF, 24);
		g.setColor(Color.argb(180, 200, 220, 240));
		g.drawString(L10n.get("stats_record_level", GameMainActivity.getHighScore()), LABEL_X, y + 20);
		g.drawString(L10n.get("stats_record_endless", GameMainActivity.getEndlessHighScore()), LABEL_X, y + 52);

		// Back button
		int by = BACK_BTN_Y;
		g.setColor(Color.rgb(106, 191, 245));
		g.fillRoundRect(BACK_BTN_X, by, BACK_BTN_W, BACK_BTN_H, 18);
		g.setFont(Typeface.DEFAULT_BOLD, 28);
		g.setColor(Color.rgb(16, 56, 90));
		drawCenteredText(g, L10n.get("stats_back"), BACK_BTN_X, BACK_BTN_W, by + 34);
	}

	private int drawStatRow(Painter g, int y, String label, String value) {
		g.setColor(Color.argb(40, 255, 255, 255));
		g.fillRoundRect(PANEL_X + 30, y, PANEL_W - 60, ROW_H - 4, 12);

		g.setFont(Typeface.SANS_SERIF, 26);
		g.setColor(Color.argb(220, 255, 255, 255));
		g.drawString(label, LABEL_X, y + 36);

		g.setFont(Typeface.DEFAULT_BOLD, 28);
		g.setColor(Color.rgb(255, 215, 0));
		float vw = g.measureText(value);
		g.drawString(value, PANEL_X + PANEL_W - 60 - (int) vw - 14, y + 36);

		return y + ROW_H;
	}

	private static String valueStr(int v) {
		return String.valueOf(v);
	}

	private static String formatTime(int totalSeconds) {
		int min = totalSeconds / 60;
		int sec = totalSeconds % 60;
		if (min > 0) {
			return L10n.get("time_format_min_sec", min, sec);
		}
		return L10n.get("time_format_sec", sec);
	}

	@Override
	public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
		if (e.getAction() != MotionEvent.ACTION_UP) {
			return true;
		}
		if (isInside(scaleX, scaleY, BACK_BTN_X, BACK_BTN_Y, BACK_BTN_W, BACK_BTN_H)) {
			Assets.playBack();
			setCurrentState(new MenuState());
			return true;
		}
		return true;
	}

	private boolean isInside(int x, int y, int left, int top, int w, int h) {
		return x >= left && x <= left + w && y >= top && y <= top + h;
	}

	private void drawCenteredText(Painter g, String text, int left, int width, int baselineY) {
		float tw = g.measureText(text);
		g.drawString(text, left + (int)((width - tw) / 2f), baselineY);
	}
}

package com.teacher.game.state;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class SettingsState extends State {

	private static final int PANEL_X = 340;
	private static final int PANEL_Y = 100;
	private static final int PANEL_W = 600;
	private static final int PANEL_H = 460;

	private static final int ROW_H = 70;
	private static final int ROW_GAP = 12;
	private static final int FIRST_ROW_Y = PANEL_Y + 100;

	private static final int TOGGLE_W = 120;
	private static final int TOGGLE_H = 46;

	private static final int BACK_BTN_X = 340;
	private static final int BACK_BTN_Y = 620;
	private static final int BACK_BTN_W = 600;
	private static final int BACK_BTN_H = 56;

	private String mResetFeedback;
	private float mResetTimer;

	@Override
	public void init() {
		mResetFeedback = null;
		mResetTimer = 0;
	}

	@Override
	public void update(float delta) {
		if (mResetTimer > 0) {
			mResetTimer -= delta;
			if (mResetTimer <= 0) {
				mResetTimer = 0;
				mResetFeedback = null;
			}
		}
	}

	@Override
	public void render(Painter g) {
		g.drawImage(Assets.menu, 0, 0);

		// Dark backdrop
		g.setColor(Color.argb(160, 4, 10, 24));
		g.fillRect(0, 0, GameMainActivity.GAME_WIDTH, GameMainActivity.GAME_HEIGHT);

		// Settings panel
		g.setColor(Color.argb(200, 10, 36, 66));
		g.fillRoundRect(PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 28);

		// Title
		g.setColor(Color.argb(255, 205, 244, 255));
		g.setFont(Typeface.SANS_SERIF, 44);
		float titleW = g.measureText("游戏设置");
		g.drawString("游戏设置", (GameMainActivity.GAME_WIDTH - (int)titleW) / 2, PANEL_Y + 54);

		// Divider line
		g.setColor(Color.argb(80, 255, 255, 255));
		g.fillRoundRect(PANEL_X + 30, PANEL_Y + 76, PANEL_W - 60, 2, 1);

		// ---- Row 0: Sound effects ----
		drawSettingRow(g, 0, "音效", GameMainActivity.isSoundEnabled());

		// ---- Row 1: BGM ----
		drawSettingRow(g, 1, "背景音乐", GameMainActivity.isMusicEnabled());

		// ---- Row 2: Reset scores ----
		int rowY = getRowY(2);
		g.setColor(Color.argb(60, 255, 255, 255));
		g.fillRoundRect(PANEL_X + 30, rowY, PANEL_W - 60, ROW_H, 16);

		g.setFont(Typeface.SANS_SERIF, 28);
		g.setColor(Color.argb(200, 255, 255, 255));
		g.drawString("重置最高分", PANEL_X + 50, rowY + 46);

		// Reset button
		int btnX = PANEL_X + PANEL_W - 60 - TOGGLE_W;
		boolean showFeedback = mResetFeedback != null && mResetTimer > 0;
		g.setColor(showFeedback ? Color.rgb(106, 230, 130) : Color.rgb(255, 100, 80));
		g.fillRoundRect(btnX, rowY + (ROW_H - TOGGLE_H) / 2, TOGGLE_W, TOGGLE_H, 14);
		g.setFont(Typeface.DEFAULT_BOLD, 22);
		g.setColor(Color.rgb(255, 255, 255));
		String resetLabel = showFeedback ? "已重置" : "重置";
		float resetW = g.measureText(resetLabel);
		g.drawString(resetLabel, btnX + (int)((TOGGLE_W - resetW) / 2),
				rowY + (ROW_H + TOGGLE_H) / 2 - 6);

		// ---- Back button ----
		g.setColor(Color.argb(200, 10, 36, 66));
		g.fillRoundRect(BACK_BTN_X, BACK_BTN_Y, BACK_BTN_W, BACK_BTN_H, 22);
		g.setFont(Typeface.SANS_SERIF, 30);
		g.setColor(Color.argb(220, 255, 255, 255));
		float backW = g.measureText("返回主菜单");
		g.drawString("返回主菜单", (GameMainActivity.GAME_WIDTH - (int)backW) / 2, BACK_BTN_Y + 38);
	}

	private void drawSettingRow(Painter g, int index, String label, boolean enabled) {
		int rowY = getRowY(index);
		g.setColor(Color.argb(60, 255, 255, 255));
		g.fillRoundRect(PANEL_X + 30, rowY, PANEL_W - 60, ROW_H, 16);

		g.setFont(Typeface.SANS_SERIF, 28);
		g.setColor(Color.argb(200, 255, 255, 255));
		g.drawString(label, PANEL_X + 50, rowY + 46);

		// Toggle buttons
		int toggleX = PANEL_X + PANEL_W - 60 - TOGGLE_W;
		int onX = toggleX;
		int offX = toggleX + TOGGLE_W / 2;

		drawToggleButton(g, onX, rowY, "开", enabled);
		drawToggleButton(g, offX, rowY, "关", !enabled);
	}

	private void drawToggleButton(Painter g, int x, int rowY, String label, boolean active) {
		int btnW = TOGGLE_W / 2;
		int btnY = rowY + (ROW_H - TOGGLE_H) / 2;
		g.setColor(active ? Color.rgb(255, 198, 84) : Color.argb(100, 255, 255, 255));
		g.fillRoundRect(x + 1, btnY, btnW - 2, TOGGLE_H, 10);
		g.setFont(Typeface.DEFAULT_BOLD, 24);
		g.setColor(active ? Color.rgb(12, 58, 93) : Color.argb(160, 255, 255, 255));
		float textW = g.measureText(label);
		g.drawString(label, x + (int)((btnW - textW) / 2), btnY + TOGGLE_H - 14);
	}

	private int getRowY(int index) {
		return FIRST_ROW_Y + index * (ROW_H + ROW_GAP);
	}

	@Override
	public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
		if (e.getAction() != MotionEvent.ACTION_UP) {
			return true;
		}

		// Back button
		if (isInside(scaleX, scaleY, BACK_BTN_X, BACK_BTN_Y, BACK_BTN_W, BACK_BTN_H)) {
			setCurrentState(new MenuState());
			return true;
		}

		// Row 0: Sound effects toggle
		if (handleToggleTap(scaleX, scaleY, 0)) {
			boolean current = GameMainActivity.isSoundEnabled();
			GameMainActivity.setSoundEnabled(!current);
			return true;
		}

		// Row 1: BGM toggle
		if (handleToggleTap(scaleX, scaleY, 1)) {
			boolean current = GameMainActivity.isMusicEnabled();
			GameMainActivity.setMusicEnabled(!current);
			return true;
		}

		// Row 2: Reset scores button
		int rowY = getRowY(2);
		int btnX = PANEL_X + PANEL_W - 60 - TOGGLE_W;
		if (isInside(scaleX, scaleY, btnX, rowY, TOGGLE_W, ROW_H)) {
			GameMainActivity.resetAllScores();
			mResetFeedback = "已重置";
			mResetTimer = 1.5f;
			return true;
		}

		return true;
	}

	private boolean handleToggleTap(int x, int y, int rowIndex) {
		int rowY = getRowY(rowIndex);
		int toggleX = PANEL_X + PANEL_W - 60 - TOGGLE_W;
		return isInside(x, y, toggleX, rowY, TOGGLE_W, ROW_H);
	}

	private boolean isInside(int x, int y, int left, int top, int width, int height) {
		return x >= left && x <= left + width && y >= top && y <= top + height;
	}
}

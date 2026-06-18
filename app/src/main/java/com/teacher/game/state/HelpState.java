package com.teacher.game.state;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class HelpState extends State {

	// ---------- page data ----------

	private static final String[][] PAGE_TITLES = {
			{ L10n.get("help_credits_title") },
			{ L10n.get("help_goal_title") },
			{ L10n.get("help_control_title") },
			{ L10n.get("help_item_title") },
			{ L10n.get("help_companion_title") },
			{ L10n.get("help_combo_title") },
			{ L10n.get("help_mode_title") },
	};

	private static final String[][] PAGE_LINES = {
			{ // 制作成员
					L10n.get("credits_yang"),
					L10n.get("credits_zhou"),
					L10n.get("credits_wu"),
			},
			{ // 游戏目标
					L10n.get("help_goal_1"),
					L10n.get("help_goal_2"),
					L10n.get("help_goal_3"),
					L10n.get("help_goal_4"),
			},
			{ // 操作方式
					L10n.get("help_control_1"),
					L10n.get("help_control_2"),
					L10n.get("help_control_3"),
					L10n.get("help_control_4"),
			},
			{ // 道具说明
					L10n.get("help_item_1"),
					L10n.get("help_item_2"),
					L10n.get("help_item_3"),
					L10n.get("help_item_4"),
					L10n.get("help_item_5"),
			},
			{ // 同伴系统
					L10n.get("help_companion_1"),
					L10n.get("help_companion_2"),
					L10n.get("help_companion_3"),
					L10n.get("help_companion_4"),
			},
			{ // 连击系统
					L10n.get("help_combo_1"),
					L10n.get("help_combo_2"),
					L10n.get("help_combo_3"),
					L10n.get("help_combo_4"),
			},
			{ // 游戏模式
					L10n.get("help_mode_1"),
					L10n.get("help_mode_2"),
					L10n.get("help_mode_3"),
					L10n.get("help_mode_4"),
			},
	};

	private static final int PAGE_COUNT = PAGE_TITLES.length;

	// ---------- layout constants ----------

	private static final int CARD_X = 152;
	private static final int CARD_Y = 56;
	private static final int CARD_W = 976;
	private static final int CARD_H = 580;

	private static final int TITLE_Y = 130;

	private static final int CONTENT_START_Y = 210;
	private static final int CONTENT_LINE_H = 52;

	private static final int PAGE_INDICATOR_Y = 510;

	private static final int BTN_W = 170;
	private static final int BTN_H = 56;
	private static final int BTN_GAP = 20;
	private static final int BTN_Y = 550;

	// ---------- state ----------

	private int mCurrentPage;

	@Override
	public void init() {
		mCurrentPage = 0;
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void render(Painter g) {
		g.drawImage(Assets.bgimg7, 0, 0);

		// Card background
		g.setColor(Color.argb(150, 5, 29, 58));
		g.fillRoundRect(CARD_X, CARD_Y, CARD_W, CARD_H, 34);

		// Title
		String title = PAGE_TITLES[mCurrentPage][0];
		g.setFont(Typeface.SANS_SERIF, 48);
		g.setColor(Color.WHITE);
		drawCenteredText(g, title, CARD_X, CARD_W, TITLE_Y);

		// Divider line
		g.setColor(Color.argb(80, 255, 255, 255));
		g.fillRect(CARD_X + 60, TITLE_Y + 20, CARD_W - 120, 2);

		// Content lines
		String[] lines = PAGE_LINES[mCurrentPage];
		g.setFont(Typeface.SANS_SERIF, 26);
		g.setColor(Color.argb(255, 219, 246, 255));
		for (int i = 0; i < lines.length; i++) {
			g.drawString(lines[i], CARD_X + 60, CONTENT_START_Y + i * CONTENT_LINE_H);
		}

		// Page indicator
		String pageStr = (mCurrentPage + 1) + " / " + PAGE_COUNT;
		g.setFont(Typeface.SANS_SERIF, 22);
		g.setColor(Color.argb(160, 255, 255, 255));
		drawCenteredText(g, pageStr, CARD_X, CARD_W, PAGE_INDICATOR_Y);

		// Buttons
		int totalW = 3 * BTN_W + 2 * BTN_GAP;
		int btnStartX = CARD_X + (CARD_W - totalW) / 2;
		drawButton(g, L10n.get("help_prev"), btnStartX, mCurrentPage > 0);
		drawButton(g, L10n.get("help_back"), btnStartX + BTN_W + BTN_GAP, true);
		drawButton(g, L10n.get("help_next"), btnStartX + 2 * (BTN_W + BTN_GAP), mCurrentPage < PAGE_COUNT - 1);
	}

	private void drawButton(Painter g, String label, int x, boolean enabled) {
		if (!enabled) {
			return;
		}
		g.setColor(Color.argb(100, 0, 0, 0));
		g.fillRoundRect(x + 3, BTN_Y + 3, BTN_W, BTN_H, 16);
		g.setColor(Color.rgb(255, 197, 81));
		g.fillRoundRect(x, BTN_Y, BTN_W, BTN_H, 16);
		g.setFont(Typeface.DEFAULT_BOLD, 24);
		g.setColor(Color.rgb(16, 56, 90));
		drawCenteredText(g, label, x, BTN_W, BTN_Y + 36);
	}

	@Override
	public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
		if (e.getAction() != MotionEvent.ACTION_UP) {
			return true;
		}

		int totalW = 3 * BTN_W + 2 * BTN_GAP;
		int btnStartX = CARD_X + (CARD_W - totalW) / 2;

		// "上一页"
		if (mCurrentPage > 0 && isInside(scaleX, scaleY, btnStartX, BTN_Y, BTN_W, BTN_H)) {
			Assets.playSound(Assets.selectedID);
			mCurrentPage--;
			return true;
		}

		// "返回菜单"
		int menuBtnX = btnStartX + BTN_W + BTN_GAP;
		if (isInside(scaleX, scaleY, menuBtnX, BTN_Y, BTN_W, BTN_H)) {
			Assets.playSound(Assets.selectedID);
			setCurrentState(new MenuState());
			return true;
		}

		// "下一页"
		int nextBtnX = btnStartX + 2 * (BTN_W + BTN_GAP);
		if (mCurrentPage < PAGE_COUNT - 1 && isInside(scaleX, scaleY, nextBtnX, BTN_Y, BTN_W, BTN_H)) {
			Assets.playSound(Assets.selectedID);
			mCurrentPage++;
			return true;
		}

		return true;
	}

	private boolean isInside(int x, int y, int left, int top, int w, int h) {
		return x >= left && x <= left + w && y >= top && y <= top + h;
	}

	private void drawCenteredText(Painter g, String text, int left, int width, int baselineY) {
		float tw = g.measureText(text);
		g.drawString(text, left + (int) ((width - tw) / 2), baselineY);
	}
}

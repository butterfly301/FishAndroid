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
			{ "制作成员" },
			{ "游戏目标" },
			{ "操作方式" },
			{ "道具说明" },
			{ "同伴系统" },
			{ "连击系统" },
			{ "游戏模式" },
	};

	private static final String[][] PAGE_LINES = {
			{ // 制作成员
					"杨世杰",
					"周欣琦",
					"吴凯东",
			},
			{ // 游戏目标
					"\u2022 操控小鱼在水中游动，吃掉比自己小的鱼",
					"\u2022 躲避比自己大的鱼，避免被吃掉",
					"\u2022 达到目标分数即可通关",
					"\u2022 生命耗尽则挑战失败",
			},
			{ // 操作方式
					"\u2022 触摸屏幕任意位置出现虚拟摇杆",
					"\u2022 拖动控制小鱼游动方向",
					"\u2022 点击右上角暂停按钮暂停游戏",
					"\u2022 可在主菜单切换「手动」/「自动」模式",
			},
			{ // 道具说明
					"\u2022 加速 \u2014 短时间内提高游泳速度",
					"\u2022 护盾 \u2014 抵挡一次大鱼攻击",
					"\u2022 冰冻 \u2014 冻结所有敌鱼，动弹不得",
					"\u2022 炸弹 \u2014 消灭屏幕上所有大鱼",
					"\u2022 吸引 \u2014 将周围小鱼吸引到身边",
			},
			{ // 同伴系统
					"\u2022 每吃掉一条鱼积攒同伴能量",
					"\u2022 能量满后召唤同伴鱼跟随",
					"\u2022 同伴会自动攻击附近的小鱼",
					"\u2022 同伴助攻越多，自身等级越高",
			},
			{ // 连击系统
					"\u2022 连续吃鱼触发连击",
					"\u2022 2秒内再次吃鱼维持连击",
					"\u2022 连击倍率：x1.0 \u2192 x1.5 \u2192 x2.0 \u2192 x2.5 \u2192 x3.0",
					"\u2022 被大鱼攻击后连击重置",
			},
			{ // 游戏模式
					"\u2022 关卡模式：依次挑战3个关卡",
					"\u2022 每关有不同目标分数和难度",
					"\u2022 无尽模式：不限目标，挑战最高分",
					"\u2022 不同关卡背景各异，敌鱼也会变化",
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
		drawButton(g, "上一页", btnStartX, mCurrentPage > 0);
		drawButton(g, "返回菜单", btnStartX + BTN_W + BTN_GAP, true);
		drawButton(g, "下一页", btnStartX + 2 * (BTN_W + BTN_GAP), mCurrentPage < PAGE_COUNT - 1);
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

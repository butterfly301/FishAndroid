package com.teacher.game.state;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;
import com.teacher.game.model.LevelConfig;
import com.teacher.game.model.LevelRepository;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.util.Log;

public class LevelSelectState extends State {

	private static final int CARD_X = 120;
	private static final int CARD_Y = 54;
	private static final int CARD_W = 1040;
	private static final int CARD_H = 612;
	private static final int GRID_LEFT = 168;
	private static final int GRID_TOP = 210;
	private static final int GRID_COLS = 4;
	private static final int GRID_ROWS = 3;
	private static final int LEVELS_PER_PAGE = GRID_COLS * GRID_ROWS;
	private static final int LEVEL_BTN_W = 210;
	private static final int LEVEL_BTN_H = 108;
	private static final int LEVEL_BTN_GAP_X = 24;
	private static final int LEVEL_BTN_GAP_Y = 24;
	private static final int PAGE_BTN_W = 170;
	private static final int PAGE_BTN_H = 58;
	private static final int PAGE_BTN_Y = 604;
	private static final int PREV_BTN_X = 180;
	private static final int NEXT_BTN_X = 930;
	private static final int BACK_BTN_X = 490;
	private static final int BACK_BTN_W = 300;

	private int mPageIndex;

	public LevelSelectState() {
		this(0);
	}

	public LevelSelectState(int pageIndex) {
		mPageIndex = Math.max(0, Math.min(pageIndex, getLastPageIndex()));
	}

	@Override
	public void init() {
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void render(Painter g) {
		g.drawImage(Assets.menu, 0, 0);

		g.setColor(Color.argb(180, 6, 32, 64));
		g.fillRoundRect(CARD_X, CARD_Y, CARD_W, CARD_H, 34);

		g.setColor(Color.argb(130, 255, 255, 255));
		g.fillRoundRect(CARD_X + 28, CARD_Y + 24, CARD_W - 56, 96, 28);

		g.setFont(Typeface.DEFAULT_BOLD, 48);
		g.setColor(Color.WHITE);
		drawCenteredText(g, "选择关卡", CARD_X, CARD_W, 122);

		g.setFont(Typeface.SANS_SERIF, 24);
		g.setColor(Color.argb(255, 220, 244, 255));
		drawCenteredText(g, "当前已扩展为 100 关，难度会持续提升", CARD_X, CARD_W, 170);

		g.setFont(Typeface.DEFAULT_BOLD, 22);
		g.setColor(Color.argb(255, 196, 231, 255));
		drawCenteredText(g, "第 " + (mPageIndex + 1) + " / " + LevelRepository.getPageCount(LEVELS_PER_PAGE) + " 页",
				CARD_X, CARD_W, 202);

		int startIndex = mPageIndex * LEVELS_PER_PAGE;
		int endIndex = Math.min(startIndex + LEVELS_PER_PAGE, LevelRepository.getLevelCount());
		for (int i = startIndex; i < endIndex; i++) {
			drawLevelButton(g, i);
		}

		drawPageButton(g, PREV_BTN_X, "上一页", mPageIndex > 0);
		drawPageButton(g, NEXT_BTN_X, "下一页", mPageIndex < getLastPageIndex());

		g.setColor(Color.rgb(106, 191, 245));
		g.fillRoundRect(BACK_BTN_X, PAGE_BTN_Y, BACK_BTN_W, PAGE_BTN_H, 18);
		g.setFont(Typeface.DEFAULT_BOLD, 28);
		g.setColor(Color.rgb(16, 56, 90));
		drawCenteredText(g, "返回主菜单", BACK_BTN_X, BACK_BTN_W, PAGE_BTN_Y + 38);
	}

	private void drawLevelButton(Painter g, int levelIndex) {
		int localIndex = levelIndex - mPageIndex * LEVELS_PER_PAGE;
		int col = localIndex % GRID_COLS;
		int row = localIndex / GRID_COLS;
		int left = GRID_LEFT + col * (LEVEL_BTN_W + LEVEL_BTN_GAP_X);
		int top = GRID_TOP + row * (LEVEL_BTN_H + LEVEL_BTN_GAP_Y);
		LevelConfig config = LevelRepository.getLevel(levelIndex);
		boolean locked = levelIndex > GameMainActivity.getUnlockedLevel();
		boolean isCurrent = levelIndex == GameMainActivity.getUnlockedLevel();

		// Shadow
		g.setColor(Color.argb(90, 0, 0, 0));
		g.fillRoundRect(left + 4, top + 4, LEVEL_BTN_W, LEVEL_BTN_H, 24);

		// Card color
		if (locked) {
			g.setColor(Color.argb(100, 120, 120, 120));
		} else if (isCurrent) {
			g.setColor(Color.rgb(106, 230, 130)); // green for next-to-play
		} else {
			g.setColor(Color.rgb(255, 199, 84));
		}
		g.fillRoundRect(left, top, LEVEL_BTN_W, LEVEL_BTN_H, 24);

		if (locked) {
			// Lock icon and dimmed text
			g.setFont(Typeface.DEFAULT_BOLD, 30);
			g.setColor(Color.argb(160, 200, 200, 200));
			drawCenteredText(g, "🔒 第 " + (levelIndex + 1) + " 关", left, LEVEL_BTN_W, top + 40);

			g.setFont(Typeface.SANS_SERIF, 18);
			g.setColor(Color.argb(100, 180, 180, 180));
			drawCenteredText(g, "目标分数 " + config.targetScore, left, LEVEL_BTN_W, top + 70);
			drawCenteredText(g, "敌鱼 " + config.enemyCount + " 条", left, LEVEL_BTN_W, top + 94);
		} else {
			g.setFont(Typeface.DEFAULT_BOLD, 30);
			g.setColor(Color.rgb(16, 56, 90));
			drawCenteredText(g, "第 " + (levelIndex + 1) + " 关", left, LEVEL_BTN_W, top + 40);

			g.setFont(Typeface.SANS_SERIF, 18);
			g.setColor(Color.rgb(35, 83, 126));
			drawCenteredText(g, "目标分数 " + config.targetScore, left, LEVEL_BTN_W, top + 70);
			drawCenteredText(g, "敌鱼 " + config.enemyCount + " 条", left, LEVEL_BTN_W, top + 94);
		}
	}

	private void drawPageButton(Painter g, int left, String label, boolean enabled) {
		g.setColor(enabled ? Color.rgb(255, 199, 84) : Color.argb(110, 255, 255, 255));
		g.fillRoundRect(left, PAGE_BTN_Y, PAGE_BTN_W, PAGE_BTN_H, 18);
		g.setFont(Typeface.DEFAULT_BOLD, 26);
		g.setColor(enabled ? Color.rgb(16, 56, 90) : Color.argb(160, 60, 92, 120));
		drawCenteredText(g, label, left, PAGE_BTN_W, PAGE_BTN_Y + 38);
	}

	private int getLastPageIndex() {
		return Math.max(0, LevelRepository.getPageCount(LEVELS_PER_PAGE) - 1);
	}

	private void drawCenteredText(Painter g, String text, int left, int width, int baselineY) {
		float textWidth = g.measureText(text);
		int textX = left + (int)((width - textWidth) / 2f);
		g.drawString(text, textX, baselineY);
	}

	private boolean isPointInside(int x, int y, int left, int top, int width, int height) {
		return x >= left && x <= left + width && y >= top && y <= top + height;
	}

	@Override
	public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
		if (e.getAction() != MotionEvent.ACTION_UP) {
			return true;
		}

		int selectedLevel = findTappedLevel(scaleX, scaleY);
		if (selectedLevel >= 0) {
			Assets.playSound(Assets.selectedID);
			setCurrentState(new PlayState(selectedLevel));
			return true;
		}

		if (mPageIndex > 0 && isPointInside(scaleX, scaleY, PREV_BTN_X, PAGE_BTN_Y, PAGE_BTN_W, PAGE_BTN_H)) {
			Assets.playSound(Assets.selectedID);
			setCurrentState(new LevelSelectState(mPageIndex - 1));
			return true;
		}

		if (mPageIndex < getLastPageIndex()
				&& isPointInside(scaleX, scaleY, NEXT_BTN_X, PAGE_BTN_Y, PAGE_BTN_W, PAGE_BTN_H)) {
			Assets.playSound(Assets.selectedID);
			setCurrentState(new LevelSelectState(mPageIndex + 1));
			return true;
		}

		if (isPointInside(scaleX, scaleY, BACK_BTN_X, PAGE_BTN_Y, BACK_BTN_W, PAGE_BTN_H)) {
			Assets.playSound(Assets.selectedID);
			setCurrentState(new MenuState());
			return true;
		}

		return true;
	}

	private int findTappedLevel(int x, int y) {
		int startIndex = mPageIndex * LEVELS_PER_PAGE;
		int endIndex = Math.min(startIndex + LEVELS_PER_PAGE, LevelRepository.getLevelCount());
		int unlocked = GameMainActivity.getUnlockedLevel();
		for (int levelIndex = startIndex; levelIndex < endIndex; levelIndex++) {
			if (levelIndex > unlocked) {
				continue; // locked — skip
			}
			int localIndex = levelIndex - startIndex;
			int col = localIndex % GRID_COLS;
			int row = localIndex / GRID_COLS;
			int left = GRID_LEFT + col * (LEVEL_BTN_W + LEVEL_BTN_GAP_X);
			int top = GRID_TOP + row * (LEVEL_BTN_H + LEVEL_BTN_GAP_Y);
			if (isPointInside(x, y, left, top, LEVEL_BTN_W, LEVEL_BTN_H)) {
				return levelIndex;
			}
		}
		return -1;
	}
}

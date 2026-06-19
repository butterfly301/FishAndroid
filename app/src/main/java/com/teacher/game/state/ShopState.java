package com.teacher.game.state;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class ShopState extends State {

	// ---------- item definitions ----------

	private static final int ITEM_COUNT = 3;

	private static final int[] ITEM_PRICES = { 10, 20, 8 };

	private String[] getItemLabels() {
		return new String[]{
			L10n.get("shop_item_shield"),
			L10n.get("shop_item_life"),
			L10n.get("shop_item_speed"),
		};
	}

	private String[] getItemDescs() {
		return new String[]{
			L10n.get("shop_desc_shield"),
			L10n.get("shop_desc_life"),
			L10n.get("shop_desc_speed"),
		};
	}

	// ---------- layout constants ----------

	private static final int PANEL_X = 160;
	private static final int PANEL_Y = 50;
	private static final int PANEL_W = 960;
	private static final int PANEL_H = 620;

	private static final int TITLE_Y = 108;

	private static final int COINS_Y = 150;

	private static final int CARD_START_Y = 190;
	private static final int CARD_H = 120;
	private static final int CARD_GAP = 14;

	private static final int NAME_X = PANEL_X + 48;
	private static final int DESC_X = PANEL_X + 48;
	private static final int PRICE_LABEL_X = PANEL_X + 48;

	private static final int BUY_BTN_W = 160;
	private static final int BUY_BTN_H = 52;
	private static final int BUY_BTN_RIGHT_MARGIN = 36;

	private static final int BACK_BTN_X = 370;
	private static final int BACK_BTN_Y = 682;
	private static final int BACK_BTN_W = 540;
	private static final int BACK_BTN_H = 50;

	// ---------- state ----------

	private String[] mItemLabels;
	private String[] mItemDescs;
	private String mFeedback;
	private float mFeedbackTimer;

	@Override
	public void init() {
		mItemLabels = getItemLabels();
		mItemDescs = getItemDescs();
		mFeedback = null;
		mFeedbackTimer = 0;
	}

	@Override
	public void update(float delta) {
		if (mFeedbackTimer > 0) {
			mFeedbackTimer -= delta;
			if (mFeedbackTimer <= 0) {
				mFeedbackTimer = 0;
				mFeedback = null;
			}
		}
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
		g.fillRoundRect(PANEL_X + 28, PANEL_Y + 26, PANEL_W - 56, 66, 20);
		g.setFont(Typeface.DEFAULT_BOLD, 38);
		g.setColor(Color.WHITE);
		drawCenteredText(g, L10n.get("shop_title"), PANEL_X, PANEL_W, TITLE_Y);

		// Coin balance
		int coins = GameMainActivity.getCoins();
		g.setFont(Typeface.SANS_SERIF, 28);
		g.setColor(Color.rgb(255, 215, 0));
		drawCenteredText(g, L10n.get("shop_coins", coins), PANEL_X, PANEL_W, COINS_Y);

		// Item cards
		for (int i = 0; i < ITEM_COUNT; i++) {
			drawItemCard(g, i);
		}

		// Feedback
		if (mFeedback != null) {
			g.setFont(Typeface.DEFAULT_BOLD, 32);
			g.setColor(Color.rgb(106, 230, 130));
			drawCenteredText(g, mFeedback, PANEL_X, PANEL_W, BACK_BTN_Y - 12);
		}

		// Back button
		g.setColor(Color.rgb(106, 191, 245));
		g.fillRoundRect(BACK_BTN_X, BACK_BTN_Y, BACK_BTN_W, BACK_BTN_H, 18);
		g.setFont(Typeface.DEFAULT_BOLD, 28);
		g.setColor(Color.rgb(16, 56, 90));
		drawCenteredText(g, L10n.get("shop_back"), BACK_BTN_X, BACK_BTN_W, BACK_BTN_Y + 34);
	}

	private void drawItemCard(Painter g, int index) {
		int cardY = CARD_START_Y + index * (CARD_H + CARD_GAP);

		// Card background
		g.setColor(Color.argb(60, 255, 255, 255));
		g.fillRoundRect(PANEL_X + 30, cardY, PANEL_W - 60, CARD_H, 16);

		// Item name
		g.setFont(Typeface.DEFAULT_BOLD, 28);
		g.setColor(Color.argb(255, 255, 255, 255));
		g.drawString(mItemLabels[index], NAME_X, cardY + 38);

		// Item description
		g.setFont(Typeface.SANS_SERIF, 20);
		g.setColor(Color.argb(180, 200, 220, 240));
		g.drawString(mItemDescs[index], DESC_X, cardY + 68);

		// Price label
		String priceStr = L10n.get("shop_coins_short", ITEM_PRICES[index]);
		g.setFont(Typeface.SANS_SERIF, 22);
		g.setColor(Color.rgb(255, 215, 0));
		float priceW = g.measureText(priceStr);
		g.drawString(priceStr, PRICE_LABEL_X, cardY + 100);

		// Buy button
		int btnX = PANEL_X + PANEL_W - 60 - BUY_BTN_W - BUY_BTN_RIGHT_MARGIN;
		int btnY = cardY + (CARD_H - BUY_BTN_H) / 2;
		int coins = GameMainActivity.getCoins();
		boolean canAfford = coins >= ITEM_PRICES[index];
		g.setColor(canAfford ? Color.rgb(255, 198, 84) : Color.argb(100, 120, 120, 120));
		g.fillRoundRect(btnX, btnY, BUY_BTN_W, BUY_BTN_H, 14);
		g.setFont(Typeface.DEFAULT_BOLD, 26);
		g.setColor(canAfford ? Color.rgb(16, 56, 90) : Color.argb(160, 180, 180, 180));
		drawCenteredText(g, L10n.get("shop_buy"), btnX, BUY_BTN_W, btnY + 36);
	}

	private void setFeedback(String msg) {
		mFeedback = msg;
		mFeedbackTimer = 1.5f;
	}

	@Override
	public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
		if (e.getAction() != MotionEvent.ACTION_UP) {
			return true;
		}

		// Check buy buttons
		for (int i = 0; i < ITEM_COUNT; i++) {
			int cardY = CARD_START_Y + i * (CARD_H + CARD_GAP);
			int btnX = PANEL_X + PANEL_W - 60 - BUY_BTN_W - BUY_BTN_RIGHT_MARGIN;
			int btnY = cardY + (CARD_H - BUY_BTN_H) / 2;
			if (isInside(scaleX, scaleY, btnX, btnY, BUY_BTN_W, BUY_BTN_H)) {
				handleBuy(i);
				return true;
			}
		}

		// Back button
		if (isInside(scaleX, scaleY, BACK_BTN_X, BACK_BTN_Y, BACK_BTN_W, BACK_BTN_H)) {
			Assets.playBack();
			setCurrentState(new MenuState());
			return true;
		}

		return true;
	}

	private void handleBuy(int index) {
		int price = ITEM_PRICES[index];
		if (!GameMainActivity.spendCoins(price)) {
			Assets.playSound(Assets.sfxLose);
			setFeedback(L10n.get("shop_no_coins"));
			return;
		}
		Assets.playClick();
		switch (index) {
			case 0: GameMainActivity.setShopShield(true); break;
			case 1: GameMainActivity.setShopExtraLife(true); break;
			case 2: GameMainActivity.setShopSpeed(true); break;
		}
		setFeedback(L10n.get("shop_bought"));
	}

	private boolean isInside(int x, int y, int left, int top, int w, int h) {
		return x >= left && x <= left + w && y >= top && y <= top + h;
	}

	private void drawCenteredText(Painter g, String text, int left, int width, int baselineY) {
		float tw = g.measureText(text);
		g.drawString(text, left + (int)((width - tw) / 2f), baselineY);
	}
}

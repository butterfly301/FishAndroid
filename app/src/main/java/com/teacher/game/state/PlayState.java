package com.teacher.game.state;

import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;
import com.teacher.framework.util.RandomNumberGenerator;
import com.teacher.game.model.Fish;
import com.teacher.game.model.LevelConfig;
import com.teacher.game.model.LevelRepository;
import com.teacher.game.model.MyFish;
import com.teacher.game.model.CompanionFish;
import com.teacher.game.model.PowerUp;
import com.teacher.game.model.PowerUpType;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class PlayState extends State {

	// ---------- package-private for extracted components ----------

	Sprite mFloatGrassR, mFloatGrassL;
	Sprite mAirBubbleR, mAirBubbleL;
	LayerManager mLayerManager;
	ArrayList<Fish> mOtherFish;
	MyFish mMyFish;
	ArrayList<PowerUp> mPowerUps;
	ArrayList<CompanionFish> mCompanionFishList;
	ModeRules mModeRules;
	LevelConfig mLevelConfig;
	int mLife;
	int mScore;
	int mLevelIndex;
	int mCompanionCharge;
	float mSpeedTimer;
	float mFreezeTimer;
	float mLureTimer;
	boolean mAutoMode;
	boolean mGamePaused;
	boolean mDebugPanelVisible;
	float mDebugGameSpeed;

	// ---------- components (created in init()) ----------

	AutoPilot mAutoPilot;
	TouchHandler mTouchHandler;
	CollisionManager mCollisionManager;

	// ---------- private helpers ----------

	private float mFlushTime;
	private ArrayList<Sprite> mAirBubbleList;
	private int mHighScore;
	private int mEndlessHighScore;
	private boolean mEndlessMode;

	private static final int IN_R = GameplayTuning.JOYSTICK_INNER_RADIUS;
	private static final int IN_45 = GameplayTuning.JOYSTICK_INNER_DIAGONAL;
	private static final int OUT_W = GameplayTuning.JOYSTICK_OUTER_RADIUS;
	private static final int IN_W = GameplayTuning.JOYSTICK_INNER_HALF;
	private static final int HUD_LEFT = 20;
	private static final int HUD_TOP = 18;
	private static final int MAX_POWERUPS = 3;
	static final int COMPANION_CHARGE_TARGET = 6;

	// ---------- Combo system ----------

	int mCombo;
	private float mComboTimer;
	RoundStats mStats;

	// Drawing constants shared with TouchHandler (used by drawDebugButton/Panel and drawHud)
	private static final int PAUSE_BTN_X = 1182;
	private static final int PAUSE_BTN_Y = 26;
	private static final int PAUSE_BTN_SIZE = 48;
	private static final int DEBUG_BTN_SIZE = 66;
	private static final int DEBUG_BTN_MARGIN = 22;
	private static final int DEBUG_PANEL_W = 640;
	private static final int DEBUG_PANEL_H = 420;
	private static final int DEBUG_PANEL_X = GameMainActivity.GAME_WIDTH - DEBUG_PANEL_W - 24;
	private static final int DEBUG_PANEL_Y = GameMainActivity.GAME_HEIGHT - DEBUG_PANEL_H - 24;
	private static final int DEBUG_CLOSE_W = 56;
	private static final int DEBUG_CLOSE_H = 56;
	private static final int DEBUG_SPEED_DEC_W = 84;
	private static final int DEBUG_SPEED_DEC_H = 64;
	private static final int DEBUG_SPEED_INC_W = 84;
	private static final int DEBUG_SPEED_INC_H = 64;
	private static final int DEBUG_SPAWN_W = 400;
	private static final int DEBUG_SPAWN_H = 72;

	public PlayState() {
		this(0, false);
	}

	public PlayState(boolean endlessMode) {
		this(0, endlessMode);
	}

	public PlayState(int levelIndex) {
		this(levelIndex, false);
	}

	public PlayState(int levelIndex, boolean endlessMode) {
		mLevelIndex = levelIndex;
		mEndlessMode = endlessMode;
	}

	@Override
	public void init() {
		mLife = 4;
		mScore = 0;
		mCombo = 0;
		mComboTimer = 0;
		mStats = new RoundStats();
		mHighScore = GameMainActivity.getHighScore();
		mEndlessHighScore = GameMainActivity.getEndlessHighScore();
		mLevelConfig = LevelRepository.getLevel(mLevelIndex);
		mAutoMode = GameMainActivity.isAutoMode();
		mAutoPilot = new AutoPilot();
		mTouchHandler = new TouchHandler(this);
		mCollisionManager = new CollisionManager(this);
		mPowerUps = new ArrayList<PowerUp>();
		mSpeedTimer = 0;
		mFreezeTimer = 0;
		mLureTimer = 0;
		mDebugPanelVisible = false;
		mDebugGameSpeed = 1.0f;
		mCompanionFishList = new ArrayList<CompanionFish>();
		mCompanionCharge = 0;
		mModeRules = ModeRules.forMode(mEndlessMode);
		Assets.playMusic("backgoundsound.mid", true);

		mMyFish = new MyFish();
		mMyFish.mSpeedMultiplier = 1.0f;
		mMyFish.mHasShield = false;
		mFloatGrassR = new Sprite(Assets.floatgrass, 76, 62);
		mFloatGrassR.setFrameSequence(new int[] {1, 0, 2, 0});
		mFloatGrassR.setPosition(980, 648);

		mFloatGrassL = new Sprite(mFloatGrassR);
		mFloatGrassL.setFrameSequence(new int[] {2, 0, 1, 0});
		mFloatGrassL.setPosition(180, 648);

		mAirBubbleR = new Sprite(Assets.airbubble, 34, 34);
		mAirBubbleR.setFrameSequence(new int[] {0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2,
				3, 3, 3, 3, 4, 4, 4, 4});
		mAirBubbleR.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);

		mAirBubbleL = new Sprite(mAirBubbleR);

		mAirBubbleList = new ArrayList<Sprite>();
		for (int i=0;i<10;i++) {
			Sprite s  = new Sprite(mAirBubbleR);
			mAirBubbleList.add(s);
		}

		mOtherFish = new ArrayList<Fish>();
		for (int i=0;i<mLevelConfig.enemyCount;i++) {
			Fish f  = new Fish(Assets.suergeonfish, 42, 24, Fish.SMALL, Fish.SWIML);
			f.setSpeedBonus(mLevelConfig.speedBonus);
			f.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
			mOtherFish.add(f);
		}


		mLayerManager = new LayerManager();
		mLayerManager.append(mMyFish);
		mLayerManager.append(mFloatGrassR);
		mLayerManager.append(mFloatGrassL);

		mLayerManager.append(mAirBubbleR);
		mLayerManager.append(mAirBubbleL);
		//for (Sprite s : mAirBubbleList)
		//	mLayerManager.append(s);

		for (Fish f : mOtherFish)
			mLayerManager.append(f);
	}

	@Override
	public void update(float delta) {
		if (mGamePaused || isRoundFinished())
			return;
		float effectiveDelta = delta * mDebugGameSpeed;
		mStats.survivalTime += effectiveDelta;
		// Power-up timers
		if (mSpeedTimer > 0) {
			mSpeedTimer -= effectiveDelta;
			if (mSpeedTimer <= 0) {
				mSpeedTimer = 0;
				mMyFish.mSpeedMultiplier = 1.0f;
			}
		}
		if (mFreezeTimer > 0) {
			mFreezeTimer -= effectiveDelta;
			if (mFreezeTimer <= 0) {
				mFreezeTimer = 0;
			}
		}
		if (mLureTimer > 0) {
			mLureTimer -= effectiveDelta;
			if (mLureTimer <= 0) {
				mLureTimer = 0;
			}
			// Attract smaller on-screen fish toward the player
			float strength = 180f; // px/s pull speed
			int myCx = mMyFish.getX() + mMyFish.getWidth() / 2;
			int myCy = mMyFish.getY() + mMyFish.getHeight() / 2;
			for (Fish f : mOtherFish) {
				if (!isOnScreen(f) || f.mSize >= mMyFish.mSize) {
					continue;
				}
				int dx = myCx - (f.getX() + f.getWidth() / 2);
				int dy = myCy - (f.getY() + f.getHeight() / 2);
				double dist = Math.sqrt(dx * dx + dy * dy);
				if (dist > 0 && dist < 600) {
					float move = Math.min(strength * effectiveDelta, (float)dist);
					f.move((int)(dx / dist * move), (int)(dy / dist * move));
				}
			}
		}

		// Combo timer decay
		if (mComboTimer > 0) {
			mComboTimer -= effectiveDelta;
			if (mComboTimer <= 0) {
				mComboTimer = 0;
				mCombo = 0;
			}
		}

		mFlushTime += effectiveDelta;
		if (mFlushTime > 0.1f) {
			mFlushTime = 0;


			mFloatGrassR.nextFrame();
			mFloatGrassL.nextFrame();


			setAirBubblePosition(mAirBubbleR, 860, 1140);
			setAirBubblePosition(mAirBubbleL, 80, 340);

			for (int i=0;i<10;i++) {
				Sprite s  = mAirBubbleList.get(i);
				int left = 40 + i * 110;
				setAirBubblePosition(s, left, left + 80);
			}

			// Fish AI must update BEFORE setOtherFishPosition so that
			// speed changes (TRACK / FLEE) take effect in the same frame,
			// preventing sudden "teleport" jumps on the next tick.
			updateFishAI();
			setOtherFishPosition();

			// Power-up spawning
			if (!isRoundFinished() && mPowerUps.size() < MAX_POWERUPS
					&& RandomNumberGenerator.getRandInt(60) == 0) {
				spawnPowerUp();
			}

			// Power-up updates
			Iterator<PowerUp> pIt = mPowerUps.iterator();
			while (pIt.hasNext()) {
				PowerUp p = pIt.next();
				p.update();
				if (p.isOutOfScreen()) {
					pIt.remove();
					mLayerManager.remove(p);
				}
			}

		mMyFish.update();
		updateCompanion();
		if (mMyFish.mStartTime > 10) {
			mCollisionManager.checkCollides();
			mCollisionManager.checkCompanionCollides();
			mCollisionManager.checkPowerUpCollision();
		}
		}
		if (mAutoMode) {
			mAutoPilot.update(effectiveDelta, mMyFish, mOtherFish, mPowerUps,
					mFreezeTimer, mSpeedTimer, mLureTimer, mMyFish.mHasShield);
		}else if (mTouchHandler.mTouchDown) {
			mMyFish.movePress(mTouchHandler.mDX, mTouchHandler.mDY);
		}
	}

	private void setOtherFishPosition() {
		for (Fish f : mOtherFish) {
			if (mFreezeTimer > 0) {
				continue;
			}
			f.update();
		if (!isOnScreen(f)) {
				f.setSize(randomFishSize());
				f.setSpeedBonus(mLevelConfig.speedBonus);
				assignFishBehavior(f);
				if (RandomNumberGenerator.getRandInt(10) > 5) {
					f.setPosition(-f.getWidth(),
							RandomNumberGenerator.getRandIntBetween(GameMainActivity.getPlayTop(),
									GameMainActivity.getPlayBottom() - f.getHeight()));

					f.setNonceState(Fish.SWIMR);
				}else {
					f.setPosition(GameMainActivity.GAME_WIDTH,
							RandomNumberGenerator.getRandIntBetween(GameMainActivity.getPlayTop(),
									GameMainActivity.getPlayBottom() - f.getHeight()));

					f.setNonceState(Fish.SWIML);
				}
			}
		}
	}

	private void setAirBubblePosition(Sprite s,int left,int right) {
		if (!isOnScreen(s)
				&& RandomNumberGenerator.getRandInt(50) > 40) {
			int randx = RandomNumberGenerator.getRandIntBetween(left, right);
			int randy = RandomNumberGenerator.getRandIntBetween(
					GameMainActivity.GAME_HEIGHT - 110,
					GameMainActivity.GAME_HEIGHT - 40);
			s.setPosition(randx, randy);
			s.setFrame(0);
		}
		s.move(0, -4);
		if (s.getFrame() == 19) {
			s.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
		}
		s.nextFrame();
	}

	private boolean isOnScreen(Sprite s) {
		return s.getX() > -s.getWidth() 	//左
				&& s.getX() < GameMainActivity.GAME_WIDTH	//右
				&& s.getY() > -s.getHeight()	//上
				&& s.getY() < GameMainActivity.GAME_HEIGHT;	//下
	}

	@Override
	public void render(Painter g) {
		g.drawImage(Assets.background, 0, 0);

		//mFloatGrassR.paint(g.getCanvas());
		mLayerManager.setViewWindow(0, 0,
				GameMainActivity.GAME_WIDTH,
				GameMainActivity.GAME_HEIGHT);

		mLayerManager.paint(g.getCanvas(), 0, 0);
		drawCompanionMarker(g);

		if (mTouchHandler.mTouchDown) {
			g.drawImage(Assets.virjoy_outter, mTouchHandler.mTouchX-OUT_W, mTouchHandler.mTouchY-OUT_W, OUT_W*2, OUT_W*2);

			if (mTouchHandler.mDX == 0 && mTouchHandler.mDY == 0)
				g.drawImage(Assets.virjoy_inner, mTouchHandler.mTouchX-IN_W, mTouchHandler.mTouchY-IN_W, IN_W*2, IN_W*2);
			if (mTouchHandler.mDX > 0) {
				if (mTouchHandler.mDY > 0)
					g.drawImage(Assets.virjoy_inner, mTouchHandler.mTouchX-IN_W+IN_45, mTouchHandler.mTouchY-IN_W+IN_45, IN_W*2, IN_W*2);
				else if (mTouchHandler.mDY < 0)
					g.drawImage(Assets.virjoy_inner, mTouchHandler.mTouchX-IN_W+IN_45, mTouchHandler.mTouchY-IN_W-IN_45, IN_W*2, IN_W*2);
				else
					g.drawImage(Assets.virjoy_inner, mTouchHandler.mTouchX-IN_W+IN_R, mTouchHandler.mTouchY-IN_W, IN_W*2, IN_W*2);
			}
			else if (mTouchHandler.mDX < 0){
				if (mTouchHandler.mDY > 0)
					g.drawImage(Assets.virjoy_inner, mTouchHandler.mTouchX-IN_W-IN_45, mTouchHandler.mTouchY-IN_W+IN_45, IN_W*2, IN_W*2);
				else if (mTouchHandler.mDY < 0)
					g.drawImage(Assets.virjoy_inner, mTouchHandler.mTouchX-IN_W-IN_45, mTouchHandler.mTouchY-IN_W-IN_45, IN_W*2, IN_W*2);
				else
					g.drawImage(Assets.virjoy_inner, mTouchHandler.mTouchX-IN_W-IN_R, mTouchHandler.mTouchY-IN_W, IN_W*2, IN_W*2);
			}else if (mTouchHandler.mDY < 0 ) {
				g.drawImage(Assets.virjoy_inner, mTouchHandler.mTouchX-IN_W, mTouchHandler.mTouchY-IN_W-IN_R, IN_W*2, IN_W*2);
			}else if (mTouchHandler.mDY > 0 ) {
				g.drawImage(Assets.virjoy_inner, mTouchHandler.mTouchX-IN_W, mTouchHandler.mTouchY-IN_W+IN_R, IN_W*2, IN_W*2);
			}
		}

		drawHud(g);
		drawCombo(g);

		drawElement(g);

		if (mLureTimer > 0) {
			drawLureAura(g);
		}

		if (mGamePaused) {
			OverlayRenderer.drawPauseOverlay(g);
		}else if (isRoundFinished()) {
			boolean cleared = didClearLevel();
			boolean hasNext = hasNextLevel();
			String subtitle = RoundTextFormatter.buildRoundEndSubtitle(
					mModeRules, mScore, mLevelConfig.index + 1, cleared, hasNext, mStats);

			String[] stats = RoundTextFormatter.buildRoundEndStats(mScore, mStats);

			OverlayRenderer.drawRoundEndOverlay(g,
					mModeRules.getRoundEndTitle(cleared, hasNext),
					subtitle,
					stats,
					getRoundEndButtonLabels());
		}

		drawDebugButton(g);
		if (mDebugPanelVisible) {
			drawDebugPanel(g);
		}
	}

	private void drawCompanionMarker(Painter g) {
		if (mCompanionFishList == null || mCompanionFishList.isEmpty()) {
			return;
		}
		for (CompanionFish companion : mCompanionFishList) {
			int centerX = companion.getX() + companion.getWidth() / 2;
			int baseY = companion.getY() - 10;

			// Smaller green inverted triangle marker.
			g.setColor(Color.argb(228, 26, 220, 114));
			g.fillRect(centerX - 6, baseY, 12, 3);
			g.fillRect(centerX - 4, baseY + 3, 8, 3);
			g.fillRect(centerX - 2, baseY + 6, 4, 3);

			// Subtle dark outline for readability on bright backgrounds.
			g.setColor(Color.argb(180, 8, 62, 40));
			g.fillRect(centerX - 7, baseY - 1, 1, 10);
			g.fillRect(centerX + 6, baseY - 1, 1, 10);
		}
	}

	private void drawDebugButton(Painter g) {
		int left = GameMainActivity.GAME_WIDTH - DEBUG_BTN_SIZE - DEBUG_BTN_MARGIN;
		int top = GameMainActivity.GAME_HEIGHT - DEBUG_BTN_SIZE - DEBUG_BTN_MARGIN;
		g.setColor(Color.argb(220, 15, 44, 72));
		g.fillRoundRect(left, top, DEBUG_BTN_SIZE, DEBUG_BTN_SIZE, 14);
		g.setColor(Color.rgb(255, 214, 96));
		g.fillRoundRect(left + 4, top + 4, DEBUG_BTN_SIZE - 8, DEBUG_BTN_SIZE - 8, 12);
		g.setFont(Typeface.DEFAULT_BOLD, 18);
		g.setColor(Color.rgb(12, 58, 93));
		g.drawString("调", left + 20, top + 37);
	}

	private void drawDebugPanel(Painter g) {
		g.setColor(Color.argb(188, 0, 0, 0));
		g.fillRect(0, 0, GameMainActivity.GAME_WIDTH, GameMainActivity.GAME_HEIGHT);

		g.setColor(Color.argb(170, 10, 40, 74));
		g.fillRoundRect(DEBUG_PANEL_X, DEBUG_PANEL_Y, DEBUG_PANEL_W, DEBUG_PANEL_H, 22);

		g.setColor(Color.argb(108, 255, 255, 255));
		g.fillRoundRect(DEBUG_PANEL_X + 16, DEBUG_PANEL_Y + 16, DEBUG_PANEL_W - 32, 76, 16);
		g.setColor(Color.WHITE);
		g.setFont(Typeface.DEFAULT_BOLD, 36);
		g.drawString("调试面板", DEBUG_PANEL_X + 26, DEBUG_PANEL_Y + 66);

		g.setFont(Typeface.SANS_SERIF, 28);
		g.setColor(Color.argb(255, 229, 245, 255));
		g.drawString("游戏速度", DEBUG_PANEL_X + 26, DEBUG_PANEL_Y + 134);
		g.drawString(String.format("%.2fx", mDebugGameSpeed), DEBUG_PANEL_X + 210, DEBUG_PANEL_Y + 134);

		int decX = DEBUG_PANEL_X + 26;
		int decY = DEBUG_PANEL_Y + 154;
		int incX = decX + DEBUG_SPEED_DEC_W + 12;
		int incY = decY;
		g.setColor(Color.rgb(255, 197, 81));
		g.fillRoundRect(decX, decY, DEBUG_SPEED_DEC_W, DEBUG_SPEED_DEC_H, 12);
		g.fillRoundRect(incX, incY, DEBUG_SPEED_INC_W, DEBUG_SPEED_INC_H, 12);
		g.setColor(Color.rgb(16, 56, 90));
		g.setFont(Typeface.DEFAULT_BOLD, 44);
		g.drawString("-", decX + 32, decY + 44);
		g.drawString("+", incX + 28, incY + 46);

		int spawnX = DEBUG_PANEL_X + 26;
		int spawnBtnH = 54;
		int spawnGap = 8;

		// "生成1条基础鱼"
		int fishY = DEBUG_PANEL_Y + 230;
		drawSpawnButton(g, spawnX, fishY, DEBUG_SPAWN_W, spawnBtnH, "生成1条基础鱼");

		// "生成一个泡泡"
		int powerUpY = fishY + spawnBtnH + spawnGap;
		drawSpawnButton(g, spawnX, powerUpY, DEBUG_SPAWN_W, spawnBtnH, "生成一个泡泡");

		// "生成一个同伴"
		int companionY = powerUpY + spawnBtnH + spawnGap;
		drawSpawnButton(g, spawnX, companionY, DEBUG_SPAWN_W, spawnBtnH, "生成一个同伴");

		int closeX = DEBUG_PANEL_X + DEBUG_PANEL_W - DEBUG_CLOSE_W - 18;
		int closeY = DEBUG_PANEL_Y + DEBUG_PANEL_H - DEBUG_CLOSE_H - 16;
		g.setColor(Color.rgb(255, 197, 81));
		g.fillRoundRect(closeX, closeY, DEBUG_CLOSE_W, DEBUG_CLOSE_H, 12);
		g.setColor(Color.rgb(16, 56, 90));
		g.setFont(Typeface.DEFAULT_BOLD, 28);
		float closeTextW = g.measureText("关闭");
		g.drawString("关闭", closeX + (int)((DEBUG_CLOSE_W - closeTextW) / 2), closeY + 37);
	}

	private void drawSpawnButton(Painter g, int x, int y, int w, int h, String label) {
		g.setColor(Color.rgb(106, 191, 245));
		g.fillRoundRect(x, y, w, h, 12);
		g.setColor(Color.rgb(16, 56, 90));
		g.setFont(Typeface.DEFAULT_BOLD, 26);
		float textW = g.measureText(label);
		g.drawString(label, x + (int)((w - textW) / 2), y + (int)(h * 0.72f));
	}

	private void drawLureAura(Painter g) {
		int cx = mMyFish.getX() + mMyFish.getWidth() / 2;
		int cy = mMyFish.getY() + mMyFish.getHeight() / 2;
		float pulse = (float)(Math.sin(System.nanoTime() / 200_000_000.0) * 0.25 + 0.75);
		int alpha = (int)(60 * pulse);
		int baseR = 90 + (int)(20 * pulse);
		Canvas canvas = g.getCanvas();
		android.graphics.Paint p = new android.graphics.Paint(
				android.graphics.Paint.ANTI_ALIAS_FLAG);
		p.setStyle(android.graphics.Paint.Style.STROKE);
		p.setStrokeWidth(2.5f);
		for (int i = 0; i < 3; i++) {
			p.setColor(Color.argb(alpha - i * 12, 255, 105, 180));
			canvas.drawCircle(cx, cy, baseR + i * 25, p);
		}
	}

	private void drawElement(Painter g) {
		if (mMyFish.mNonceState == Fish.DIE)
			g.drawImage(Assets.sorry, 555, 310);
		if (mLife <= 0)
			g.drawImage(Assets.gameover, 520, 310);
		if (didClearLevel()) {
			for (int i=0;i<4;i++) {
				if (mMyFish.mStartTime % 4 != i)
					g.drawImage(Assets.pass, i*25, 0, 554+45*i, 310, 25, 25);
				else
					g.drawImage(Assets.pass, i*25, 0, 554+45*i, 320, 25, 25);
			}
		}
	}

	private void drawHud(Painter g) {
		g.setColor(Color.argb(150, 4, 29, 58));
		g.fillRoundRect(HUD_LEFT, HUD_TOP, 1240, 74, 28);

		g.setFont(Typeface.SANS_SERIF, 28);
		g.setColor(Color.WHITE);
		if (mEndlessMode) {
			g.drawString(getModeLabel(), 48, 65);
			g.drawString("最高分：" + mEndlessHighScore, 220, 65);
			g.drawString("生命 " + mLife, 1010, 65);

			g.setFont(Typeface.SANS_SERIF, 24);
			g.setColor(Color.rgb(12, 58, 93));
			float scoreWidth = g.measureText(getScoreLabel());
			g.drawString(getScoreLabel(), (GameMainActivity.GAME_WIDTH - (int)scoreWidth) / 2, 56);
		}else {
			g.drawString(getModeLabel(), 48, 65);
			g.drawString("生命 " + mLife, 1010, 65);

			g.setColor(Color.argb(120, 255, 255, 255));
			g.fillRoundRect(430, 34, 420, 24, 12);
			int cappedScore = Math.min(mScore, mLevelConfig.targetScore);
			int progressWidth = mLevelConfig.targetScore > 0
					? (int)(420f * cappedScore / mLevelConfig.targetScore)
					: 0;
			g.setColor(Color.rgb(255, 195, 82));
			g.fillRoundRect(430, 34, progressWidth, 24, 12);

			g.setFont(Typeface.SANS_SERIF, 24);
			g.setColor(Color.rgb(12, 58, 93));
			g.drawString(getScoreLabel(), 500, 56);
		}

		g.setColor(Color.argb(220, 255, 255, 255));
		g.fillRoundRect(PAUSE_BTN_X, PAUSE_BTN_Y, PAUSE_BTN_SIZE, PAUSE_BTN_SIZE, 16);
		g.setColor(Color.rgb(20, 72, 116));
		g.fillRect(PAUSE_BTN_X + 14, PAUSE_BTN_Y + 10, 6, 28);
		g.fillRect(PAUSE_BTN_X + 28, PAUSE_BTN_Y + 10, 6, 28);

		drawPowerUpIndicators(g);
		drawCompanionIndicators(g);
	}

	private void drawCompanionIndicators(Painter g) {
		int cardX = 40;
		int cardY = 88;
		g.setColor(Color.argb(168, 6, 34, 66));
		g.fillRoundRect(cardX, cardY, 320, 54, 16);

		g.setFont(Typeface.SANS_SERIF, 22);
		g.setColor(Color.WHITE);
		g.drawString("同伴 " + mCompanionFishList.size() + " 只", cardX + 14, cardY + 35);
		g.setColor(Color.argb(120, 255, 255, 255));
		g.fillRoundRect(cardX + 168, cardY + 20, 136, 14, 8);
		int w = (int)(136f * mCompanionCharge / COMPANION_CHARGE_TARGET);
		g.setColor(Color.rgb(255, 198, 84));
		g.fillRoundRect(cardX + 168, cardY + 20, w, 14, 8);
	}

	// ================================================================
	//  Combo rendering
	// ================================================================

	private void drawCombo(Painter g) {
		if (mCombo < 2) return;

		int[] comboColors = {
			Color.rgb(255, 215, 0),    // gold  — combo 2
			Color.rgb(255, 165, 0),    // orange — combo 3
			Color.rgb(255, 69, 0),     // red-orange — combo 4
			Color.rgb(200, 50, 255)    // purple — combo 5
		};
		int ci = Math.min(mCombo - 2, comboColors.length - 1);

		// Combo indicator between score area and life indicator
		int comboX = 800;
		int comboY = 65;

		g.setFont(Typeface.DEFAULT_BOLD, 28);
		// Shadow for readability
		g.setColor(Color.argb(120, 0, 0, 0));
		g.drawString("连击 x" + mCombo, comboX + 2, comboY + 2);
		// Glow-ish outline
		g.setColor(Color.argb(80, 255, 255, 255));
		g.drawString("连击 x" + mCombo, comboX + 1, comboY + 1);
		// Main text
		g.setColor(comboColors[ci]);
		g.drawString("连击 x" + mCombo, comboX, comboY);
	}

	private void updateCompanion() {
		if (mCompanionFishList == null || mCompanionFishList.isEmpty()) {
			return;
		}
		for (CompanionFish companion : mCompanionFishList) {
			Fish target = findCompanionTarget(companion);
			if (target != null) {
				companion.dashToward(target);
			} else {
				companion.follow(mMyFish);
			}
		}
	}

	private Fish findCompanionTarget(CompanionFish companion) {
		if (companion == null) {
			return null;
		}
		Fish target = null;
		double nearest = Double.MAX_VALUE;
		int cx = companion.getX() + companion.getWidth() / 2;
		int cy = companion.getY() + companion.getHeight() / 2;
		for (Fish fish : mOtherFish) {
			if (!isOnScreen(fish) || fish.mSize >= mMyFish.mSize) {
				continue;
			}
			int tx = fish.getX() + fish.getWidth() / 2;
			int ty = fish.getY() + fish.getHeight() / 2;
			double dis = distance(cx, cy, tx, ty);
			if (dis < nearest && dis < 260) {
				nearest = dis;
				target = fish;
			}
		}
		return target;
	}

	void spawnCompanionIfReady() {
		if (mCompanionCharge < COMPANION_CHARGE_TARGET || isRoundFinished()) {
			return;
		}
		mCompanionCharge -= COMPANION_CHARGE_TARGET;
		CompanionFish companion = new CompanionFish();
		int offsetIndex = mCompanionFishList.size() % 4;
		int spawnOffsetX = -90 - offsetIndex * 28;
		int spawnOffsetY = 30 + (offsetIndex % 2) * 22;
		companion.setPosition(mMyFish.getX() + spawnOffsetX, mMyFish.getY() + spawnOffsetY);
		mCompanionFishList.add(companion);
		mLayerManager.append(companion);
	}

	private void despawnCompanion() {
		for (CompanionFish companion : mCompanionFishList) {
			mLayerManager.remove(companion);
		}
		mCompanionFishList.clear();
	}

	private void assignFishBehavior(Fish f) {
		f.mBehavior = Fish.Behavior.PATROL;
	}

	private void updateFishAI() {
		int myCx = mMyFish.getX() + mMyFish.getWidth() / 2;
		int myCy = mMyFish.getY() + mMyFish.getHeight() / 2;

		for (Fish f : mOtherFish) {
			if (!isOnScreen(f)) continue;
			if (f.mNonceState == Fish.DIE
					|| f.mNonceState == Fish.EATL
					|| f.mNonceState == Fish.EATR) continue;

			int fCx = f.getX() + f.getWidth() / 2;
			int fCy = f.getY() + f.getHeight() / 2;
			int dx = myCx - fCx;  // >0 : player is to the right
			int dy = myCy - fCy;
			double dist = Math.sqrt(dx * dx + dy * dy);

			// All fish use PATROL behavior — no TRACK/FLEE overrides
		}
	}

	// ================================================================
	//  Combo system
	// ================================================================

	/**
	 * Register an eat by the player fish: increments combo, resets timer,
	 * returns current score multiplier.
	 */
	float registerEat() {
		mCombo++;
		if (mCombo > GameplayTuning.MAX_COMBO) {
			mCombo = GameplayTuning.MAX_COMBO;
		}
		mComboTimer = GameplayTuning.COMBO_WINDOW_SECONDS;
		if (mCombo > mStats.comboPeak) {
			mStats.comboPeak = mCombo;
		}
		return getComboMultiplier();
	}

	/** Reset combo to zero (called when player gets eaten). */
	void resetCombo() {
		mCombo = 0;
		mComboTimer = 0;
	}

	float getComboMultiplier() {
		if (mCombo <= 1) return 1.0f;
		return 1.0f + (mCombo - 1) * 0.5f;
	}

	void addScore(int points) {
		if (!mModeRules.canGainScore(mScore, mLevelConfig.targetScore)) {
			return;
		}
		mScore += points;
		mScore = mModeRules.clampScore(mScore, mLevelConfig.targetScore);
		if (mEndlessMode && mScore > mEndlessHighScore) {
			mEndlessHighScore = mScore;
			GameMainActivity.saveEndlessHighScore(mEndlessHighScore);
		} else if (mScore > mHighScore) {
			mHighScore = mScore;
			GameMainActivity.saveHighScore(mHighScore);
		}
	}

	void onPlayerDamaged(Fish attacker) {
		if (attacker.mNonceState == Fish.SWIML || attacker.mNonceState == Fish.SWERVE_L) {
			attacker.setNonceState(Fish.EATL);
		} else if (attacker.mNonceState == Fish.SWIMR || attacker.mNonceState == Fish.SWERVE_R) {
			attacker.setNonceState(Fish.EATR);
		}
		mLife--;
		resetCombo();
		if (mLife > 0) {
			mMyFish.setNonceState(Fish.DIE);
		}
		mMyFish.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	void onPlayerEatFish(Fish fish, int points) {
		mStats.fishEaten++;
		if (mMyFish.mNonceState == Fish.SWIML || mMyFish.mNonceState == Fish.SWERVE_L) {
			mMyFish.setNonceState(Fish.EATL);
		} else if (mMyFish.mNonceState == Fish.SWIMR || mMyFish.mNonceState == Fish.SWERVE_R) {
			mMyFish.setNonceState(Fish.EATR);
		}

		if (mModeRules.canGainScore(mScore, mLevelConfig.targetScore)) {
			addScore(points);
			mCompanionCharge = Math.min(COMPANION_CHARGE_TARGET, mCompanionCharge + 1);
			spawnCompanionIfReady();
		}

		if (didClearLevel()) {
			mMyFish.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
		} else if (mScore >= 70) {
			mMyFish.setSize(Fish.SUPER);
		} else if (mScore >= 30) {
			mMyFish.setSize(Fish.BIG);
		}
		fish.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	void onCompanionEatFish(CompanionFish companion, Fish fish, int points) {
		addScore(points);
		mStats.companionAssists++;
		companion.recordAssistEat();
		fish.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	void onCollectPowerUp(PowerUp powerUp) {
		mStats.powerUpsCollected++;
		switch (powerUp.type) {
			case SPEED:
				mSpeedTimer = PowerUpType.SPEED.duration;
				mMyFish.mSpeedMultiplier = 2.0f;
				break;
			case SHIELD:
				mMyFish.mHasShield = true;
				break;
			case FREEZE:
				mFreezeTimer = PowerUpType.FREEZE.duration;
				break;
			case BOMB:
				for (Fish f : mOtherFish) {
					if (f.mSize >= mMyFish.mSize && isOnScreen(f)) {
						f.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
					}
				}
				break;
			case LURE:
				mLureTimer = PowerUpType.LURE.duration;
				break;
		}
	}

	private void drawPowerUpIndicators(Painter g) {
		int x = 460;
		int y = 20;
		int barH = 32;

		if (mSpeedTimer > 0) {
			float ratio = mSpeedTimer / PowerUpType.SPEED.duration;
			int w = (int) (140 * ratio);
			g.setColor(Color.argb(180, 255, 215, 0));
			g.fillRoundRect(x, y, w, barH, 6);
			g.setColor(Color.argb(220, 255, 255, 255));
			g.setFont(Typeface.SANS_SERIF, 22);
			g.drawString("加速", x + 8, y + 24);
			x += 158;
		}

		if (mFreezeTimer > 0) {
			float ratio = mFreezeTimer / PowerUpType.FREEZE.duration;
			int w = (int) (140 * ratio);
			g.setColor(Color.argb(180, 0, 200, 255));
			g.fillRoundRect(x, y, w, barH, 6);
			g.setColor(Color.argb(220, 255, 255, 255));
			g.setFont(Typeface.SANS_SERIF, 22);
			g.drawString("冰冻", x + 8, y + 24);
			x += 158;
		}

		if (mMyFish.mHasShield) {
			g.setColor(Color.argb(180, 40, 130, 255));
			g.fillRoundRect(x, y, 100, barH, 6);
			g.setColor(Color.argb(220, 255, 255, 255));
			g.setFont(Typeface.SANS_SERIF, 22);
			g.drawString("护盾", x + 8, y + 24);
			x += 118;
		}

		if (mLureTimer > 0) {
			float ratio = mLureTimer / PowerUpType.LURE.duration;
			int w = (int) (140 * ratio);
			g.setColor(Color.argb(180, 255, 105, 180));
			g.fillRoundRect(x, y, w, barH, 6);
			g.setColor(Color.argb(220, 255, 255, 255));
			g.setFont(Typeface.SANS_SERIF, 22);
			g.drawString("吸引", x + 8, y + 24);
		}
	}

	double distance(int x1, int y1, int x2, int y2) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		return Math.sqrt(dx * dx + dy * dy);
	}

	boolean isRoundFinished() {
		return mModeRules.isRoundFinished(mLife, mScore, mLevelConfig.targetScore);
	}

	boolean didClearLevel() {
		return mModeRules.didClearLevel(mScore, mLevelConfig.targetScore);
	}

	boolean hasNextLevel() {
		return mLevelIndex < LevelRepository.getLevelCount() - 1;
	}

	String[] getRoundEndButtonLabels() {
		return mModeRules.getRoundEndButtonLabels(didClearLevel(), hasNextLevel());
	}

	void restartGame() {
		Assets.stopMusic();
		setCurrentState(new PlayState(mLevelIndex, mEndlessMode));
	}

	void nextLevel() {
		Assets.stopMusic();
		setCurrentState(new PlayState(mLevelIndex + 1));
	}

	void returnToMenu() {
		Assets.stopMusic();
		setCurrentState(new MenuState());
	}

	void handleRoundEndAction(ModeRules.RoundEndAction action) {
		if (action == null) {
			return;
		}
		switch (action) {
			case NEXT_LEVEL:
				nextLevel();
				break;
			case RETURN_MENU:
				returnToMenu();
				break;
			case RESTART:
			default:
				restartGame();
				break;
		}
	}

	void spawnPowerUp() {
		PowerUpType[] types = PowerUpType.values();
		PowerUpType type = types[RandomNumberGenerator.getRandInt(types.length)];
		PowerUp p = new PowerUp(type);
		int x = RandomNumberGenerator.getRandIntBetween(60,
				GameMainActivity.GAME_WIDTH - 60);
		int y = RandomNumberGenerator.getRandIntBetween(
				GameMainActivity.getPlayTop() + 40,
				GameMainActivity.getPlayBottom() - 40);
		p.setPosition(x, y);
		mPowerUps.add(p);
		mLayerManager.append(p);
	}

	void spawnDebugSmallFish() {
		Fish f = new Fish(Assets.suergeonfish, 42, 24, Fish.SMALL, Fish.DIE);
		f.setSize(Fish.SMALL);
		f.setSpeedBonus(mLevelConfig.speedBonus);
		int y = RandomNumberGenerator.getRandIntBetween(
				GameMainActivity.getPlayTop(),
				GameMainActivity.getPlayBottom() - f.getHeight());
		if (RandomNumberGenerator.getRandInt(10) > 4) {
			f.setPosition(-f.getWidth(), y);
			f.setNonceState(Fish.SWIMR);
		}else {
			f.setPosition(GameMainActivity.GAME_WIDTH, y);
			f.setNonceState(Fish.SWIML);
		}
		mOtherFish.add(f);
		mLayerManager.append(f);
	}

	void spawnDebugCompanion() {
		mCompanionCharge = COMPANION_CHARGE_TARGET;
		spawnCompanionIfReady();
	}

	@Override
	public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
		return mTouchHandler.onTouch(e, scaleX, scaleY);
	}



	private byte randomFishSize() {
		int roll = RandomNumberGenerator.getRandInt(100);
		if (roll < mLevelConfig.superChance) {
			return Fish.SUPER;
		}
		if (roll < mLevelConfig.superChance + mLevelConfig.bigChance) {
			return Fish.BIG;
		}
		if (roll < mLevelConfig.superChance + mLevelConfig.bigChance + mLevelConfig.normalChance) {
			return Fish.NORMAL;
		}
		return Fish.SMALL;
	}

	private String getModeLabel() {
		return mModeRules.getModeLabel(mLevelConfig.index);
	}

	private String getScoreLabel() {
		return mModeRules.getScoreLabel(mScore, mLevelConfig.targetScore);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mGamePaused = true;
		Assets.stopMusic();
	}
}

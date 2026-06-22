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

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;
import com.teacher.game.state.L10n;

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
	int mHighScore;
	int mEndlessHighScore;
	boolean mEndlessMode;
	private android.graphics.Bitmap mPlayBackground;

	private static final int IN_R = GameplayTuning.JOYSTICK_INNER_RADIUS;
	private static final int IN_45 = GameplayTuning.JOYSTICK_INNER_DIAGONAL;
	private static final int OUT_W = GameplayTuning.JOYSTICK_OUTER_RADIUS;
	private static final int IN_W = GameplayTuning.JOYSTICK_INNER_HALF;
	private static final int MAX_POWERUPS = 3;
	static final int COMPANION_CHARGE_TARGET = 6;

	// ---------- Combo system ----------

	int mCombo;
	private float mComboTimer;
	float mComboScale;
	RoundStats mStats;

	// ---- Time limit ----
	float mTimeLimitTimer;

	// ---- Achievement stats (saved at round end) ----
	private int mRoundFishEaten;
	private int mRoundPowerUpsCollected;

	// ---- Visual effects ----
	ArrayList<ScorePopup> mScorePopups;
	ArrayList<Particle> mParticles;
	private float mHitFlashTimer;

	// ---- Collection discovery notification ----
	private String mDiscoveryText;
	private float mDiscoveryTimer;

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
		mComboScale = 1.0f;
		mStats = new RoundStats();
		mScorePopups = new ArrayList<ScorePopup>();
		mParticles = new ArrayList<Particle>();
		mHitFlashTimer = 0;
		mRoundFishEaten = 0;
		mRoundPowerUpsCollected = 0;
		mDiscoveryText = null;
		mDiscoveryTimer = 0;
		mHighScore = GameMainActivity.getHighScore();
		mEndlessHighScore = GameMainActivity.getEndlessHighScore();
		mLevelConfig = LevelRepository.getLevel(mLevelIndex);
		mTimeLimitTimer = mLevelConfig.timeLimit;
		mPlayBackground = Assets.getPlayBackground(mLevelIndex, mEndlessMode);
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
		mMyFish = new MyFish();
		mMyFish.mSpeedMultiplier = 1.0f;
		mMyFish.mHasShield = false;
		Assets.stopMusic();
		if (mEndlessMode) {
			Assets.playEndlessBGM();
		} else {
			Assets.playLevelBGM(mLevelIndex);
		}

		// ---- Statistics ----
		GameMainActivity.incrementStatGamesPlayed();

		// ---- Apply shop items ----
		if (GameMainActivity.hasShopShield()) {
			mMyFish.mHasShield = true;
			GameMainActivity.setShopShield(false); // consume
		}
		if (GameMainActivity.hasShopExtraLife()) {
			mLife++;
			if (mLife > 99) mLife = 99;
			GameMainActivity.setShopExtraLife(false); // consume
		}
		if (GameMainActivity.hasShopSpeed()) {
			mSpeedTimer = PowerUpType.SPEED.duration;
			mMyFish.mSpeedMultiplier = 2.0f;
			GameMainActivity.setShopSpeed(false); // consume
		}
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
			prepareEnemyFish(f, Fish.SMALL);
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
			setAirBubblePosition(mAirBubbleL, 0, 100);

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

			// Power-up spawning (drop rate from level config)
			if (!isRoundFinished() && mPowerUps.size() < MAX_POWERUPS
					&& mLevelConfig.powerUpDropChance > 0
					&& RandomNumberGenerator.getRandInt(mLevelConfig.powerUpDropChance) == 0) {
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

		// ---- Visual effects update ----
		mComboScale += (1.0f - mComboScale) * 0.12f;
		for (int i = mScorePopups.size() - 1; i >= 0; i--) {
			mScorePopups.get(i).update();
			if (mScorePopups.get(i).isDead()) {
				mScorePopups.remove(i);
			}
		}
		for (int i = mParticles.size() - 1; i >= 0; i--) {
			mParticles.get(i).update();
			if (mParticles.get(i).isDead()) {
				mParticles.remove(i);
			}
		}

		if (mHitFlashTimer > 0) {
			mHitFlashTimer -= effectiveDelta;
			if (mHitFlashTimer < 0) mHitFlashTimer = 0;
		}

		// Collection discovery notification timer
		if (mDiscoveryTimer > 0) {
			mDiscoveryTimer -= effectiveDelta;
			if (mDiscoveryTimer <= 0) {
				mDiscoveryTimer = 0;
				mDiscoveryText = null;
			}
		}

		// Time limit
		if (mLevelConfig.timeLimit > 0 && !isRoundFinished()) {
			mTimeLimitTimer -= effectiveDelta;
			if (mTimeLimitTimer < 0) {
				mTimeLimitTimer = 0;
			}
		}
	}

	private void setOtherFishPosition() {
		for (Fish f : mOtherFish) {
			if (mFreezeTimer > 0) {
				continue;
			}
			f.update();
		if (!isOnScreen(f)) {
				prepareEnemyFish(f, randomFishSize());
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
		g.drawImage(mPlayBackground != null ? mPlayBackground : Assets.background, 0, 0);

		//mFloatGrassR.paint(g.getCanvas());
		mLayerManager.setViewWindow(0, 0,
				GameMainActivity.GAME_WIDTH,
				GameMainActivity.GAME_HEIGHT);

		mLayerManager.paint(g.getCanvas(), 0, 0);
		HudRenderer.drawCompanionMarker(g, this);

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

		HudRenderer.drawHud(g, this);
		HudRenderer.drawCombo(g, this);

		HudRenderer.drawElement(g, this);

		if (mLureTimer > 0) {
			HudRenderer.drawLureAura(g, this);
		}

		if (mGamePaused) {
			OverlayRenderer.drawPauseOverlay(g);
		}else if (isRoundFinished()) {
			boolean cleared = didClearLevel();
			boolean hasNext = hasNextLevel();
			String subtitle = RoundTextFormatter.buildRoundEndSubtitle(
					mModeRules, mScore, mLevelConfig.index + 1, cleared, hasNext, mStats, mLevelConfig);

			String[] stats = RoundTextFormatter.buildRoundEndStats(mScore, mStats, mEndlessMode, mLevelConfig);

			int stars = 0;
			if (mEndlessMode) {
				// Endless mode: 1 star for playing, 2 for score>1000, 3 for >5000
				if (mScore > 0) stars = 1;
				if (mScore > 1000) stars = 2;
				if (mScore > 5000) stars = 3;
			} else if (mLevelConfig.targetScore > 0) {
				// Level mode: 1 for attempting, 2 for target met, 3 for 1.5x target
				if (mScore > 0) stars = 1;
				if (mScore >= mLevelConfig.targetScore) stars = 2;
				if (mScore >= mLevelConfig.targetScore * 1.5f) stars = 3;
			}

			OverlayRenderer.drawRoundEndOverlay(g,
					mModeRules.getRoundEndTitle(cleared, hasNext),
					subtitle,
					stats,
					getRoundEndButtonLabels(),
					stars);
		}

		if (mHitFlashTimer > 0) {
			int alpha = (int)(180 * Math.min(mHitFlashTimer / 0.3f, 1.0f));
			g.setColor(Color.argb(alpha, 230, 30, 30));
			g.fillRect(0, 0, GameMainActivity.GAME_WIDTH, GameMainActivity.GAME_HEIGHT);
		}

		HudRenderer.drawDebugButton(g);
		if (mDebugPanelVisible) {
			drawDebugPanel(g);
		}

		// ---- Collection discovery notification ----
		if (mDiscoveryText != null && mDiscoveryTimer > 0) {
			drawDiscoveryNotification(g);
		}

		// ---- Visual effects render (on top of everything) ----
		for (Particle p : mParticles) {
			p.render(g);
		}
		for (ScorePopup popup : mScorePopups) {
			popup.render(g);
		}
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
		g.drawString(L10n.get("debug_title"), DEBUG_PANEL_X + 26, DEBUG_PANEL_Y + 66);

		g.setFont(Typeface.SANS_SERIF, 28);
		g.setColor(Color.argb(255, 229, 245, 255));
		g.drawString(L10n.get("debug_speed"), DEBUG_PANEL_X + 26, DEBUG_PANEL_Y + 134);
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
		drawSpawnButton(g, spawnX, fishY, DEBUG_SPAWN_W, spawnBtnH, L10n.get("debug_spawn_fish"));

		// "生成一个泡泡"
		int powerUpY = fishY + spawnBtnH + spawnGap;
		drawSpawnButton(g, spawnX, powerUpY, DEBUG_SPAWN_W, spawnBtnH, L10n.get("debug_spawn_powerup"));

		// "生成一个同伴"
		int companionY = powerUpY + spawnBtnH + spawnGap;
		drawSpawnButton(g, spawnX, companionY, DEBUG_SPAWN_W, spawnBtnH, L10n.get("debug_spawn_companion"));

		int closeX = DEBUG_PANEL_X + DEBUG_PANEL_W - DEBUG_CLOSE_W - 18;
		int closeY = DEBUG_PANEL_Y + DEBUG_PANEL_H - DEBUG_CLOSE_H - 16;
		g.setColor(Color.rgb(255, 197, 81));
		g.fillRoundRect(closeX, closeY, DEBUG_CLOSE_W, DEBUG_CLOSE_H, 12);
		g.setColor(Color.rgb(16, 56, 90));
		g.setFont(Typeface.DEFAULT_BOLD, 28);
		String closeLabel = L10n.get("debug_close");
		float closeTextW = g.measureText(closeLabel);
		g.drawString(closeLabel, closeX + (int)((DEBUG_CLOSE_W - closeTextW) / 2), closeY + 37);
	}

	private void drawSpawnButton(Painter g, int x, int y, int w, int h, String label) {
		g.setColor(Color.rgb(106, 191, 245));
		g.fillRoundRect(x, y, w, h, 12);
		g.setColor(Color.rgb(16, 56, 90));
		g.setFont(Typeface.DEFAULT_BOLD, 26);
		float textW = g.measureText(label);
		g.drawString(label, x + (int)((w - textW) / 2), y + (int)(h * 0.72f));
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

	/**
	 * Track power-up collection for the encyclopedia.
	 */
	private void recordPowerUpCollected(PowerUpType type) {
		String id;
		String nameKey;
		switch (type) {
			case SPEED:  id = "coll_power_SPEED"; nameKey = "powerup_speed"; break;
			case SHIELD: id = "coll_power_SHIELD"; nameKey = "powerup_shield"; break;
			case FREEZE: id = "coll_power_FREEZE"; nameKey = "powerup_freeze"; break;
			case BOMB:   id = "coll_power_BOMB"; nameKey = "powerup_bomb"; break;
			case LURE:   id = "coll_power_LURE"; nameKey = "powerup_lure"; break;
			default:     return;
		}
			if (GameMainActivity.markCollectionDiscovered(id)) {
			setDiscoveryNotification(L10n.get("collection_new") + type.getName());
		}
	}

	void spawnCompanionIfReady() {
		if (mCompanionCharge < COMPANION_CHARGE_TARGET || isRoundFinished()) {
			return;
		}
		mCompanionCharge -= COMPANION_CHARGE_TARGET;
		if (GameMainActivity.markCollectionDiscovered("coll_companion")) {
			setDiscoveryNotification(L10n.get("collection_new") + L10n.get("coll_companion"));
		}
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
		if (mCombo >= 2) {
			mComboScale = 1.45f;
			Assets.playSound(Assets.sfxCombo);
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
		Assets.playSound(Assets.sfxHit);

		// Hit visual effect: screen flash + red particles
		mHitFlashTimer = 0.3f;
		int cx = mMyFish.getX() + mMyFish.getWidth() / 2;
		int cy = mMyFish.getY() + mMyFish.getHeight() / 2;
		spawnHitParticles(cx, cy);

		if (mLife <= 0) {
			Assets.playSound(Assets.sfxLose);
		}
		if (mLife > 0) {
			mMyFish.setNonceState(Fish.DIE);
		}
		mMyFish.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	void onPlayerEatFish(Fish fish, int points) {
		mStats.fishEaten++;
		mRoundFishEaten++;
		// Award coins for shop
		int coinReward = (fish.mSize + 1) * 2;
		GameMainActivity.addCoins(coinReward);
		GameMainActivity.addStatTotalCoinsEarned(coinReward);
		if (mMyFish.mNonceState == Fish.SWIML || mMyFish.mNonceState == Fish.SWERVE_L) {
			mMyFish.setNonceState(Fish.EATL);
		} else if (mMyFish.mNonceState == Fish.SWIMR || mMyFish.mNonceState == Fish.SWERVE_R) {
			mMyFish.setNonceState(Fish.EATR);
		}

		// Play eat sound based on fish size
		if (fish.mSize >= Fish.BIG) {
			Assets.playSound(Assets.sfxEatBig);
		} else {
			Assets.playSound(Assets.sfxEat);
		}

		// ---- Visual effects: particles + score popup ----
		int fishCx = fish.getX() + fish.getWidth() / 2;
		int fishCy = fish.getY() + fish.getHeight() / 2;
		spawnEatParticles(fishCx, fishCy, fish.mSize);

		float mult = getComboMultiplier();
		String popupText = (mult > 1.0f) ? "+" + points + " x" + String.format("%.1f", mult)
				: "+" + points;
		mScorePopups.add(new ScorePopup(popupText, fishCx, fishCy - 10));

		if (mModeRules.canGainScore(mScore, mLevelConfig.targetScore)) {
			addScore(points);
			mCompanionCharge = Math.min(COMPANION_CHARGE_TARGET, mCompanionCharge + 1);
			spawnCompanionIfReady();
		}

		if (didClearLevel()) {
			Assets.playSound(Assets.sfxWin);
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
		GameMainActivity.addCoins(1);
		GameMainActivity.addStatTotalCoinsEarned(1);
		fish.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	void onCollectPowerUp(PowerUp powerUp) {
		mStats.powerUpsCollected++;
		mRoundPowerUpsCollected++;
		recordPowerUpCollected(powerUp.type);
		switch (powerUp.type) {
			case SPEED:
				mSpeedTimer = PowerUpType.SPEED.duration;
				mMyFish.mSpeedMultiplier = 2.0f;
				Assets.playSound(Assets.sfxPowerup);
				break;
			case SHIELD:
				mMyFish.mHasShield = true;
				Assets.playSound(Assets.sfxShield);
				break;
			case FREEZE:
				mFreezeTimer = PowerUpType.FREEZE.duration;
				Assets.playSound(Assets.sfxPowerup);
				break;
			case BOMB:
				for (Fish f : mOtherFish) {
					if (f.mSize >= mMyFish.mSize && isOnScreen(f)) {
						f.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
					}
				}
				Assets.playSound(Assets.sfxBomb);
				break;
			case LURE:
				mLureTimer = PowerUpType.LURE.duration;
				Assets.playSound(Assets.sfxLure);
				break;
		}
	}

	double distance(int x1, int y1, int x2, int y2) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		return Math.sqrt(dx * dx + dy * dy);
	}

	boolean isRoundFinished() {
		if (mLevelConfig.timeLimit > 0 && mTimeLimitTimer <= 0) {
			return true;
		}
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

	void saveRoundStats() {
		// Level progress unlock
		if (!mEndlessMode && didClearLevel()) {
			int nextLevel = mLevelIndex + 1;
			if (nextLevel > GameMainActivity.getUnlockedLevel()) {
				GameMainActivity.setUnlockedLevel(nextLevel);
			}
		}
		// Cumulative achievement stats
		GameMainActivity.addFishEaten(mRoundFishEaten);
		GameMainActivity.addPowerUpsCollected(mRoundPowerUpsCollected);
		GameMainActivity.updateComboPeak(mStats.comboPeak);

		// Statistics tracking
		int survivalSec = (int) mStats.survivalTime;
		GameMainActivity.addStatSurvival(survivalSec);
		GameMainActivity.updateStatLongestSurvival(survivalSec);
	}

	void restartGame() {
		saveRoundStats();
		Assets.stopMusic();
		setCurrentState(new PlayState(mLevelIndex, mEndlessMode));
	}

	void nextLevel() {
		saveRoundStats();
		Assets.stopMusic();
		setCurrentState(new PlayState(mLevelIndex + 1));
	}

	void returnToMenu() {
		saveRoundStats();
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
		PowerUpType[] types = mLevelConfig.allowedPowerUps != null
				? mLevelConfig.allowedPowerUps
				: PowerUpType.values();
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
		prepareEnemyFish(f, Fish.SMALL);
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

	private void spawnEatParticles(int cx, int cy, byte fishSize) {
		int count = 8 + fishSize * 2;
		int[] colors = {
				Color.rgb(255, 215, 0),
				Color.rgb(255, 165, 0),
				Color.rgb(255, 200, 50),
				Color.rgb(255, 240, 150)
		};
		for (int i = 0; i < count; i++) {
			float angle = (float) (Math.random() * Math.PI * 2);
			float speed = 2.0f + (float) (Math.random() * 4.0f);
			float vx = (float) Math.cos(angle) * speed;
			float vy = (float) Math.sin(angle) * speed - 1.5f;
			int color = colors[RandomNumberGenerator.getRandInt(colors.length)];
			float life = 0.4f + (float) (Math.random() * 0.4f);
			mParticles.add(new Particle(cx, cy, vx, vy, color, life));
		}
	}

	private void spawnHitParticles(int cx, int cy) {
		int count = 14;
		int[] colors = {
				Color.rgb(255, 50, 50),
				Color.rgb(230, 30, 30),
				Color.rgb(255, 100, 60),
				Color.rgb(200, 20, 20)
		};
		for (int i = 0; i < count; i++) {
			float angle = (float) (Math.random() * Math.PI * 2);
			float speed = 3.0f + (float) (Math.random() * 5.0f);
			float vx = (float) Math.cos(angle) * speed;
			float vy = (float) Math.sin(angle) * speed - 2.0f;
			int color = colors[RandomNumberGenerator.getRandInt(colors.length)];
			float life = 0.35f + (float) (Math.random() * 0.3f);
			mParticles.add(new Particle(cx, cy, vx, vy, color, life));
		}
	}

	@Override
	public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
		return mTouchHandler.onTouch(e, scaleX, scaleY);
	}

	// ================================================================
	//  Collection discovery notification
	// ================================================================

	private void setDiscoveryNotification(String text) {
		mDiscoveryText = text;
		mDiscoveryTimer = 2.5f;
	}

	private void drawDiscoveryNotification(Painter g) {
		float alpha = Math.min(1.0f, mDiscoveryTimer / 0.5f);
		int a = (int)(200 * alpha);
		if (a < 2) return;

		String text = mDiscoveryText;
		if (text == null) return;

		// Background bar at top
		int barY = 0;
		int barH = 48;
		g.setColor(Color.argb(a, 6, 34, 70));
		g.fillRect(0, barY, GameMainActivity.GAME_WIDTH, barH);

		// Accent line
		g.setColor(Color.argb(a, 255, 215, 0));
		g.fillRect(0, barY + barH - 3, GameMainActivity.GAME_WIDTH, 3);

		// Text
		g.setFont(Typeface.DEFAULT_BOLD, 26);
		g.setColor(Color.argb(a, 255, 255, 255));
		float tw = g.measureText(text);
		g.drawString(text, (GameMainActivity.GAME_WIDTH - (int)tw) / 2, barY + 34);
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

	private void prepareEnemyFish(Fish fish, byte size) {
		Fish.Species species = randomFishSpecies(size);
		fish.setSpecies(species);
		fish.setSpeedBonus(mLevelConfig.speedBonus);
		recordFishSpeciesEncountered(species);
	}

	/**
	 * Track fish species for the collection encyclopedia.
	 * Shows a notification on first discovery.
	 */
	private void recordFishSpeciesEncountered(Fish.Species species) {
		String id;
		String nameKey;
		switch (species) {
			case SURGEON:      id = "coll_fish_SURGEON"; nameKey = "fish_SURGEON"; break;
			case GLOW_SURGEON: id = "coll_fish_GLOW_SURGEON"; nameKey = "fish_GLOW_SURGEON"; break;
			case TUNA:         id = "coll_fish_TUNA"; nameKey = "fish_TUNA"; break;
			case SUN_TUNA:     id = "coll_fish_SUN_TUNA"; nameKey = "fish_SUN_TUNA"; break;
			case LION:         id = "coll_fish_LION"; nameKey = "fish_LION"; break;
			case ROYAL_LION:   id = "coll_fish_ROYAL_LION"; nameKey = "fish_ROYAL_LION"; break;
			case SHARK:        id = "coll_fish_SHARK"; nameKey = "fish_SHARK"; break;
			case REEF_SHARK:   id = "coll_fish_REEF_SHARK"; nameKey = "fish_REEF_SHARK"; break;
			default:           return;
		}
		if (GameMainActivity.markCollectionDiscovered(id)) {
			setDiscoveryNotification(L10n.get("collection_new") + L10n.get(nameKey));
		}
	}

	private Fish.Species randomFishSpecies(byte size) {
		boolean useVariant = RandomNumberGenerator.getRandInt(100) < getVariantSpawnChance();
		switch (size) {
			case Fish.NORMAL:
				return useVariant ? Fish.Species.SUN_TUNA : Fish.Species.TUNA;
			case Fish.BIG:
				return useVariant ? Fish.Species.ROYAL_LION : Fish.Species.LION;
			case Fish.SUPER:
				return useVariant ? Fish.Species.REEF_SHARK : Fish.Species.SHARK;
			case Fish.SMALL:
			default:
				return useVariant ? Fish.Species.GLOW_SURGEON : Fish.Species.SURGEON;
		}
	}

	private int getVariantSpawnChance() {
		if (mEndlessMode) {
			return 55;
		}
		switch (mLevelConfig.index) {
			case 1:
				return 20;
			case 2:
				return 35;
			default:
				return 50;
		}
	}

	String getModeLabel() {
		return mModeRules.getModeLabel(mLevelConfig.index);
	}

	String getScoreLabel() {
		return mModeRules.getScoreLabel(mScore, mLevelConfig.targetScore);
	}

	@Override
	public void onPause() {
		super.onPause();
		mGamePaused = true;
		Assets.stopMusic();
	}
}

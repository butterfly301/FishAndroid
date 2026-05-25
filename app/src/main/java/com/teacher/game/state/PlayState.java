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
import com.teacher.game.model.PowerUp;
import com.teacher.game.model.PowerUpType;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class PlayState extends State {

	private enum AutoPilotState {
		ESCAPE_BOUNDARY,
		EVADE_THREAT,
		ALIGN_TARGET,
		CHASE_TARGET,
		CRUISE
	}

	private Sprite mFloatGrassR, mFloatGrassL;

	private Sprite mAirBubbleR, mAirBubbleL;

	private float mFlushTime;

	private LayerManager mLayerManager;

	private ArrayList<Sprite> mAirBubbleList;

	private ArrayList<Fish> mOtherFish;

	private MyFish mMyFish;

	private static final int IN_R = 24, IN_45 = 17, OUT_W = 48, IN_W = 24;
	private static final int HUD_LEFT = 20;
	private static final int HUD_TOP = 18;
	private static final int PAUSE_BTN_X = 1182;
	private static final int PAUSE_BTN_Y = 26;
	private static final int PAUSE_BTN_SIZE = 48;
	private static final int OVERLAY_CARD_X = 300;
	private static final int OVERLAY_CARD_Y = 180;
	private static final int OVERLAY_CARD_W = 680;
	private static final int OVERLAY_CARD_H = 320;
	private static final int OVERLAY_BUTTON_W = 180;
	private static final int OVERLAY_BUTTON_H = 64;
	private static final int OVERLAY_BUTTON_GAP = 18;
	private static final int OVERLAY_BUTTON_Y = 376;
	private static final int DEBUG_BTN_SIZE = 66;
	private static final int DEBUG_BTN_MARGIN = 22;
	private static final int DEBUG_PANEL_W = 560;
	private static final int DEBUG_PANEL_H = 330;
	private static final int DEBUG_PANEL_X = GameMainActivity.GAME_WIDTH - DEBUG_PANEL_W - 24;
	private static final int DEBUG_PANEL_Y = GameMainActivity.GAME_HEIGHT - DEBUG_PANEL_H - 24;
	private static final int DEBUG_CLOSE_W = 150;
	private static final int DEBUG_CLOSE_H = 56;
	private static final int DEBUG_SPEED_DEC_W = 84;
	private static final int DEBUG_SPEED_DEC_H = 64;
	private static final int DEBUG_SPEED_INC_W = 84;
	private static final int DEBUG_SPEED_INC_H = 64;
	private static final int DEBUG_SPAWN_W = 400;
	private static final int DEBUG_SPAWN_H = 72;

	private int mLife;

	private int mScore;

	private int mHighScore;
	
	private int mEndlessHighScore;
	
	private int mLevelIndex;
	
	private LevelConfig mLevelConfig;
	
	private boolean mEndlessMode;
	
	private boolean mAutoMode;
	
	private float mAutoDecisionTimer;
	
	private int mAutoInputX;
	
	private int mAutoInputY;
	
	private Fish mAutoTargetFish;
	
	private float mAutoTargetLockTime;
	
	private AutoPilotState mAutoPilotState;

	private ArrayList<PowerUp> mPowerUps;

	private float mSpeedTimer;

	private float mFreezeTimer;

	private static final int MAX_POWERUPS = 3;

	private static final int POWERUP_SIZE = 36;

	private boolean mDebugPanelVisible;

	private float mDebugGameSpeed;

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
		mHighScore = GameMainActivity.getHighScore();
		mEndlessHighScore = GameMainActivity.getEndlessHighScore();
		mLevelConfig = LevelRepository.getLevel(mLevelIndex);
		mAutoMode = GameMainActivity.isAutoMode();
		mAutoDecisionTimer = 0;
		mAutoInputX = 0;
		mAutoInputY = 0;
		mAutoTargetFish = null;
		mAutoTargetLockTime = 0;
		mAutoPilotState = AutoPilotState.CRUISE;
		mPowerUps = new ArrayList<PowerUp>();
		mSpeedTimer = 0;
		mFreezeTimer = 0;
		mDebugPanelVisible = false;
		mDebugGameSpeed = 1.0f;
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
		if (mGamePaused)
			return;
		float effectiveDelta = delta * mDebugGameSpeed;
		if (mAutoMode) {
			mAutoDecisionTimer += effectiveDelta;
			mAutoTargetLockTime += effectiveDelta;
		}

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
			if (mMyFish.mStartTime > 10) {
				checkCollides();
				checkPowerUpCollision();
			}
		}
		if (mAutoMode) {
			updateAutoControl();
		}else if (mTouchDown) {
			mMyFish.movePress(mDX, mDY);
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

		if (mTouchDown) {
			g.drawImage(Assets.virjoy_outter, mTouchX-OUT_W, mTouchY-OUT_W);

			if (mDX == 0 && mDY == 0)
				g.drawImage(Assets.virjoy_inner, mTouchX-IN_W, mTouchY-IN_W);
			if (mDX > 0) {
				if (mDY > 0)
					g.drawImage(Assets.virjoy_inner, mTouchX-IN_W+IN_45, mTouchY-IN_W+IN_45);
				else if (mDY < 0)
					g.drawImage(Assets.virjoy_inner, mTouchX-IN_W+IN_45, mTouchY-IN_W-IN_45);
				else
					g.drawImage(Assets.virjoy_inner, mTouchX-IN_W+IN_R, mTouchY-IN_W);
			}
			else if (mDX < 0){
				if (mDY > 0)
					g.drawImage(Assets.virjoy_inner, mTouchX-IN_W-IN_45, mTouchY-IN_W+IN_45);
				else if (mDY < 0)
					g.drawImage(Assets.virjoy_inner, mTouchX-IN_W-IN_45, mTouchY-IN_W-IN_45);
				else
					g.drawImage(Assets.virjoy_inner, mTouchX-IN_W-IN_R, mTouchY-IN_W);
			}else if (mDY < 0 ) {
				g.drawImage(Assets.virjoy_inner, mTouchX-IN_W, mTouchY-IN_W-IN_R);
			}else if (mDY > 0 ) {
				g.drawImage(Assets.virjoy_inner, mTouchX-IN_W, mTouchY-IN_W+IN_R);
			}
		}

		drawHud(g);

		drawElement(g);

		if (mGamePaused) {
			drawPauseOverlay(g);
		}else if (isRoundFinished()) {
			drawRoundEndOverlay(g);
		}

		drawDebugButton(g);
		if (mDebugPanelVisible) {
			drawDebugPanel(g);
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
		int spawnY = DEBUG_PANEL_Y + 236;
		g.setColor(Color.rgb(106, 191, 245));
		g.fillRoundRect(spawnX, spawnY, DEBUG_SPAWN_W, DEBUG_SPAWN_H, 12);
		g.setColor(Color.rgb(16, 56, 90));
		g.setFont(Typeface.DEFAULT_BOLD, 30);
		g.drawString("生成1条基础鱼", spawnX + 100, spawnY + 47);

		int closeX = DEBUG_PANEL_X + DEBUG_PANEL_W - DEBUG_CLOSE_W - 18;
		int closeY = DEBUG_PANEL_Y + DEBUG_PANEL_H - DEBUG_CLOSE_H - 16;
		g.setColor(Color.rgb(255, 197, 81));
		g.fillRoundRect(closeX, closeY, DEBUG_CLOSE_W, DEBUG_CLOSE_H, 12);
		g.setColor(Color.rgb(16, 56, 90));
		g.setFont(Typeface.DEFAULT_BOLD, 28);
		g.drawString("关闭", closeX + 48, closeY + 37);
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
	}

	private void drawPowerUpIndicators(Painter g) {
		int x = 460;
		int y = 22;

		if (mSpeedTimer > 0) {
			float ratio = mSpeedTimer / PowerUpType.SPEED.duration;
			int w = (int) (60 * ratio);
			g.setColor(Color.argb(180, 255, 215, 0));
			g.fillRoundRect(x, y, w, 12, 6);
			g.setColor(Color.argb(200, 255, 255, 255));
			g.setFont(Typeface.SANS_SERIF, 11);
			g.drawString("速", x + 4, y + 10);
			x += 68;
		}

		if (mFreezeTimer > 0) {
			float ratio = mFreezeTimer / PowerUpType.FREEZE.duration;
			int w = (int) (60 * ratio);
			g.setColor(Color.argb(180, 0, 200, 255));
			g.fillRoundRect(x, y, w, 12, 6);
			g.setColor(Color.argb(200, 255, 255, 255));
			g.setFont(Typeface.SANS_SERIF, 11);
			g.drawString("冰", x + 4, y + 10);
			x += 68;
		}

		if (mMyFish.mHasShield) {
			g.setColor(Color.argb(180, 40, 130, 255));
			g.fillRoundRect(x, y, 40, 12, 6);
			g.setColor(Color.argb(200, 255, 255, 255));
			g.setFont(Typeface.SANS_SERIF, 11);
			g.drawString("盾", x + 4, y + 10);
		}
	}

	private void updateAutoControl() {
		if (mMyFish.mStartTime <= 10 || mMyFish.mNonceState == Fish.DIE) {
			mAutoInputX = 0;
			mAutoInputY = 0;
			mAutoTargetFish = null;
			mAutoTargetLockTime = 0;
			mAutoPilotState = AutoPilotState.CRUISE;
			return;
		}
		
		if (mAutoDecisionTimer < 0.16f) {
			return;
		}
		mAutoDecisionTimer = 0;

		int myCenterX = mMyFish.getX() + mMyFish.getWidth() / 2;
		int myCenterY = mMyFish.getY() + mMyFish.getHeight() / 2;
		int escapeDx = getBoundaryEscapeDx(myCenterX);
		int escapeDy = getBoundaryEscapeDy(myCenterY);
		boolean escapingBoundary = escapeDx != 0 || escapeDy != 0;

		Fish nearestThreat = null;
		double nearestThreatDistance = Double.MAX_VALUE;
		Fish bestFood = null;
		double bestFoodScore = Double.MAX_VALUE;

		for (Fish fish : mOtherFish) {
			if (!isOnScreen(fish)) {
				continue;
			}
			int fishCenterX = fish.getX() + fish.getWidth() / 2;
			int fishCenterY = fish.getY() + fish.getHeight() / 2;
			double distance = distance(myCenterX, myCenterY, fishCenterX, fishCenterY);

			if (fish.mSize >= mMyFish.mSize) {
				if (distance < nearestThreatDistance) {
					nearestThreatDistance = distance;
					nearestThreat = fish;
				}
			}else {
				double candidateScore = scoreFoodTarget(fish, myCenterX, myCenterY);
				if (candidateScore < bestFoodScore) {
					bestFoodScore = candidateScore;
					bestFood = fish;
				}
			}
		}

		int targetDx = 0;
		int targetDy = 0;
		boolean hardRamMode = false;
		if (escapingBoundary) {
			mAutoPilotState = AutoPilotState.ESCAPE_BOUNDARY;
			mAutoTargetFish = null;
			mAutoTargetLockTime = 0;
			targetDx = escapeDx;
			targetDy = escapeDy;
		}else if (nearestThreat != null && nearestThreatDistance < 220) {
			mAutoPilotState = AutoPilotState.EVADE_THREAT;
			mAutoTargetFish = null;
			mAutoTargetLockTime = 0;
			int threatCenterX = nearestThreat.getX() + nearestThreat.getWidth() / 2;
			int threatCenterY = nearestThreat.getY() + nearestThreat.getHeight() / 2;
			targetDx = myCenterX - threatCenterX;
			targetDy = myCenterY - threatCenterY;
		}else {
			Fish chaseFish = chooseAutoTarget(bestFood, myCenterX, myCenterY);
			if (chaseFish != null) {
				int liveCenterX = chaseFish.getX() + chaseFish.getWidth() / 2;
				int liveCenterY = chaseFish.getY() + chaseFish.getHeight() / 2;
				int liveDx = liveCenterX - myCenterX;
				int liveDy = liveCenterY - myCenterY;

				// New chase framework:
				// 1) far-range: approach with mild vertical correction
				// 2) close-range: force straight ram, bypass smoothing
				if (Math.abs(liveDx) <= 280) {
					targetDx = liveDx;
					targetDy = liveDy;
					mAutoPilotState = AutoPilotState.CHASE_TARGET;
					hardRamMode = true;
				}else {
					int[] predicted = predictFishCenter(chaseFish);
					targetDx = predicted[0] - myCenterX;
					targetDy = predicted[1] - myCenterY;
					mAutoPilotState = AutoPilotState.CHASE_TARGET;
				}
			}else {
				mAutoPilotState = AutoPilotState.CRUISE;
				int centerX = GameMainActivity.GAME_WIDTH / 2;
				int centerY = (GameMainActivity.getPlayTop() + GameMainActivity.getPlayBottom()) / 2;
				targetDx = centerX - myCenterX;
				targetDy = centerY - myCenterY;
			}
		}

		int desiredX = normalizeControl(targetDx, true);
		int desiredY = normalizeControl(targetDy, false);
		if (hardRamMode) {
			// Force straight bite path to avoid hovering around the target.
			desiredX = targetDx >= 0 ? 160 : -160;
			int absDy = Math.abs(targetDy);
			if (absDy < 80) {
				desiredY = 0;
			}else if (absDy < 170) {
				desiredY = targetDy > 0 ? 40 : -40;
			}else {
				desiredY = targetDy > 0 ? 70 : -70;
			}
			mAutoInputX = desiredX;
			mAutoInputY = desiredY;
			mMyFish.movePress(mAutoInputX, mAutoInputY);
			return;
		}
		mAutoInputX = smoothAutoAxis(mAutoInputX, desiredX, targetDx, 170);
		mAutoInputY = smoothAutoAxis(mAutoInputY, desiredY, targetDy, 220);
		mMyFish.movePress(mAutoInputX, mAutoInputY);
	}

	private Fish chooseAutoTarget(Fish nearestFood, int myCenterX, int myCenterY) {
		if (mAutoTargetFish != null) {
			if (!isOnScreen(mAutoTargetFish) || mAutoTargetFish.mSize >= mMyFish.mSize) {
				mAutoTargetFish = null;
				mAutoTargetLockTime = 0;
			}else {
				int[] predicted = predictFishCenter(mAutoTargetFish);
				int targetCenterX = predicted[0];
				int targetCenterY = predicted[1];
				double distance = distance(myCenterX, myCenterY, targetCenterX, targetCenterY);
				if (distance < 260 || mAutoTargetLockTime < 0.8f) {
					return mAutoTargetFish;
				}
			}
		}

		mAutoTargetFish = nearestFood;
		mAutoTargetLockTime = 0;
		return mAutoTargetFish;
	}

	private double scoreFoodTarget(Fish fish, int myCenterX, int myCenterY) {
		int[] predicted = predictFishCenter(fish);
		int fishCenterX = predicted[0];
		int fishCenterY = predicted[1];
		double distance = distance(myCenterX, myCenterY, fishCenterX, fishCenterY);
		double score = distance;

		// Prefer safer, smaller targets.
		score -= (mMyFish.mSize - fish.mSize) * 26;

		// Slightly prefer fish that are ahead of our current motion direction.
		if ((mMyFish.mMoveX >= 0 && fishCenterX >= myCenterX) || (mMyFish.mMoveX <= 0 && fishCenterX <= myCenterX)) {
			score -= 18;
		}

		// Avoid targets that sit too close to bigger fish.
		for (Fish other : mOtherFish) {
			if (other == fish || !isOnScreen(other) || other.mSize < mMyFish.mSize) {
				continue;
			}
			int otherCenterX = other.getX() + other.getWidth() / 2;
			int otherCenterY = other.getY() + other.getHeight() / 2;
			double dangerDistance = distance(fishCenterX, fishCenterY, otherCenterX, otherCenterY);
			if (dangerDistance < 170) {
				score += 220 - dangerDistance;
			}
		}
		return score;
	}

	private int[] predictFishCenter(Fish fish) {
		int leadFrames = 10;
		int predictedX = fish.getX() + fish.getWidth() / 2 + fish.mMoveX * leadFrames;
		int predictedY = fish.getY() + fish.getHeight() / 2 + fish.mMoveY * leadFrames;
		int minY = GameMainActivity.getPlayTop() + fish.getHeight() / 2;
		int maxY = GameMainActivity.getPlayBottom() - fish.getHeight() / 2;
		if (predictedY < minY) {
			predictedY = minY;
		}else if (predictedY > maxY) {
			predictedY = maxY;
		}
		return new int[] {predictedX, predictedY};
	}

	private int keepForwardChase(int targetDx) {
		if (targetDx > 0) {
			return Math.max(targetDx, 80);
		}
		if (targetDx < 0) {
			return Math.min(targetDx, -80);
		}
		if (mAutoInputX > 0 || mMyFish.mMoveX > 0) {
			return 80;
		}
		if (mAutoInputX < 0 || mMyFish.mMoveX < 0) {
			return -80;
		}
		return 80;
	}

	private AutoPilotState resolveChaseState(int targetDx, int targetDy) {
		int absDx = Math.abs(targetDx);
		int absDy = Math.abs(targetDy);
		if (absDx < 90 && absDy > 48) {
			return AutoPilotState.ALIGN_TARGET;
		}
		if (absDx < 140 && absDy < 58) {
			return AutoPilotState.CHASE_TARGET;
		}
		return AutoPilotState.CHASE_TARGET;
	}

	private int[] shapeChaseVector(int targetDx, int targetDy, AutoPilotState state) {
		int absDx = Math.abs(targetDx);
		int absDy = Math.abs(targetDy);
		if (state == AutoPilotState.ALIGN_TARGET) {
			int adjustedDx = keepForwardChase(targetDx);
			int adjustedDy = targetDy > 0 ? Math.max(targetDy, 220) : Math.min(targetDy, -220);
			return new int[] {adjustedDx, adjustedDy};
		}

		if (state == AutoPilotState.CHASE_TARGET && absDx < 220) {
			int adjustedDx = keepForwardChase(targetDx);
			int adjustedDy;
			if (absDy < 70) {
				adjustedDy = 0;
			}else if (absDy < 120) {
				adjustedDy = targetDy > 0 ? 55 : -55;
			}else {
				adjustedDy = targetDy;
			}
			return new int[] {adjustedDx, adjustedDy};
		}

		if (state == AutoPilotState.CHASE_TARGET && absDx < 140 && absDy < 58) {
			int adjustedDx = keepForwardChase(targetDx);
			int adjustedDy = absDy < 22 ? 0 : (targetDy > 0 ? 55 : -55);
			return new int[] {adjustedDx, adjustedDy};
		}

		return new int[] {targetDx, targetDy};
	}

	private int getBoundaryEscapeDx(int myCenterX) {
		int leftSafe = 160;
		int rightSafe = GameMainActivity.GAME_WIDTH - 160;
		if (myCenterX < leftSafe) {
			return Math.max(220, (GameMainActivity.GAME_WIDTH / 2) - myCenterX);
		}
		if (myCenterX > rightSafe) {
			return Math.min(-220, (GameMainActivity.GAME_WIDTH / 2) - myCenterX);
		}
		return 0;
	}

	private int getBoundaryEscapeDy(int myCenterY) {
		int topSafe = GameMainActivity.getPlayTop() + 100;
		int bottomSafe = GameMainActivity.getPlayBottom() - 100;
		int centerY = (GameMainActivity.getPlayTop() + GameMainActivity.getPlayBottom()) / 2;
		if (myCenterY < topSafe) {
			return Math.max(180, centerY - myCenterY);
		}
		if (myCenterY > bottomSafe) {
			return Math.min(-180, centerY - myCenterY);
		}
		return 0;
	}

	private double distance(int x1, int y1, int x2, int y2) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		return Math.sqrt(dx * dx + dy * dy);
	}

	private int normalizeControl(int delta, boolean horizontal) {
		if (horizontal) {
			if (delta > 160) {
				return 160;
			}
			if (delta < -160) {
				return -160;
			}
			if (delta > 70) {
				return 100;
			}
			if (delta < -70) {
				return -100;
			}
			return 0;
		}
		if (delta > 120) {
			return 140;
		}
		if (delta < -120) {
			return -140;
		}
		if (delta > 56) {
			return 80;
		}
		if (delta < -56) {
			return -80;
		}
		return 0;
	}

	private int smoothAutoAxis(int currentInput, int desiredInput, int delta, int flipThreshold) {
		if (desiredInput == 0) {
			if (Math.abs(delta) < flipThreshold / 2) {
				return 0;
			}
			return currentInput;
		}
		if (currentInput == 0) {
			return desiredInput;
		}
		if ((currentInput > 0 && desiredInput > 0) || (currentInput < 0 && desiredInput < 0)) {
			return desiredInput;
		}
		if (Math.abs(delta) < flipThreshold) {
			return 0;
		}
		return desiredInput;
	}

	private void drawPauseOverlay(Painter g) {
		g.setColor(Color.argb(168, 0, 0, 0));
		g.fillRect(0, 0, GameMainActivity.GAME_WIDTH,GameMainActivity.GAME_HEIGHT);

		drawOverlayCard(g, "游戏已暂停", "可以继续挑战，也可以重新开始");
		drawOverlayButtons(g, new String[] {"继续游戏", "重新开始", "返回菜单"});
	}

	private void drawRoundEndOverlay(Painter g) {
		g.setColor(Color.argb(168, 0, 0, 0));
		g.fillRect(0, 0, GameMainActivity.GAME_WIDTH,GameMainActivity.GAME_HEIGHT);

		if (mEndlessMode) {
			drawOverlayCard(g, "无尽结束", "本次得分 " + mScore + "，再来挑战更高分");
		}else if (didClearLevel()) {
			if (hasNextLevel()) {
				drawOverlayCard(g, "本关完成", "准备进入第 " + (mLevelConfig.index + 1) + " 关");
			}else {
				drawOverlayCard(g, "全部通关", "恭喜完成全部关卡挑战");
			}
		}else {
			drawOverlayCard(g, "挑战失败", "再试一次，看看能不能拿到更高分");
		}
		drawOverlayButtons(g, getRoundEndButtonLabels());
	}

	private void drawOverlayCard(Painter g, String title, String subtitle) {
		g.setColor(Color.argb(228, 8, 37, 74));
		g.fillRoundRect(OVERLAY_CARD_X, OVERLAY_CARD_Y, OVERLAY_CARD_W, OVERLAY_CARD_H, 34);
		g.setColor(Color.argb(120, 255, 255, 255));
		g.fillRoundRect(OVERLAY_CARD_X + 16, OVERLAY_CARD_Y + 16, OVERLAY_CARD_W - 32, 96, 28);

		g.setFont(Typeface.DEFAULT_BOLD, 44);
		g.setColor(Color.WHITE);
		drawCenteredText(g, title, OVERLAY_CARD_X, OVERLAY_CARD_W, OVERLAY_CARD_Y + 96);

		g.setFont(Typeface.SANS_SERIF, 24);
		g.setColor(Color.argb(255, 223, 241, 255));
		drawCenteredText(g, subtitle, OVERLAY_CARD_X, OVERLAY_CARD_W, OVERLAY_CARD_Y + 156);
	}

	private void drawOverlayButtons(Painter g, String[] labels) {
		for (int i = 0; i < labels.length; i++) {
			int x = getOverlayButtonX(labels.length, i);
			int color = i == labels.length - 1 && labels.length == 3
					? Color.rgb(106, 191, 245)
					: Color.rgb(255, 197, 81);
			if (labels.length == 2 && i == 1) {
				color = Color.rgb(106, 191, 245);
			}
			if (labels.length == 3 && i == 1) {
				color = Color.rgb(255, 197, 81);
			}

			g.setColor(Color.argb(100, 0, 0, 0));
			g.fillRoundRect(x + 4, OVERLAY_BUTTON_Y + 4, OVERLAY_BUTTON_W, OVERLAY_BUTTON_H, 18);
			g.setColor(color);
			g.fillRoundRect(x, OVERLAY_BUTTON_Y, OVERLAY_BUTTON_W, OVERLAY_BUTTON_H, 18);

			g.setFont(Typeface.DEFAULT_BOLD, 26);
			g.setColor(Color.rgb(14, 52, 88));
			drawCenteredText(g, labels[i], x, OVERLAY_BUTTON_W, OVERLAY_BUTTON_Y + 41);
		}
	}

	private void drawCenteredText(Painter g, String text, int left, int width, int baselineY) {
		float textWidth = g.measureText(text);
		int textX = left + (int)((width - textWidth) / 2f);
		g.drawString(text, textX, baselineY);
	}

	private int getOverlayButtonX(int buttonCount, int index) {
		int totalWidth = buttonCount * OVERLAY_BUTTON_W + (buttonCount - 1) * OVERLAY_BUTTON_GAP;
		return (GameMainActivity.GAME_WIDTH - totalWidth) / 2 + index * (OVERLAY_BUTTON_W + OVERLAY_BUTTON_GAP);
	}

	private boolean isRoundFinished() {
		return mLife <= 0 || didClearLevel();
	}

	private boolean didClearLevel() {
		return !mEndlessMode && mScore >= mLevelConfig.targetScore;
	}

	private boolean hasNextLevel() {
		return mLevelIndex < LevelRepository.getLevelCount() - 1;
	}

	private String[] getRoundEndButtonLabels() {
		if (mEndlessMode) {
			return new String[] {"重新开始", "返回菜单"};
		}
		if (didClearLevel()) {
			if (hasNextLevel()) {
				return new String[] {"下一关", "重新开始", "返回菜单"};
			}
			return new String[] {"重新开始", "返回菜单"};
		}
		return new String[] {"重试本关", "返回菜单"};
	}

	private boolean isPointInside(int x, int y, int left, int top, int width, int height) {
		return x >= left && x <= left + width && y >= top && y <= top + height;
	}

	private void restartGame() {
		Assets.stopMusic();
		setCurrentState(new PlayState(mLevelIndex, mEndlessMode));
	}

	private void nextLevel() {
		Assets.stopMusic();
		setCurrentState(new PlayState(mLevelIndex + 1));
	}

	private void returnToMenu() {
		Assets.stopMusic();
		setCurrentState(new MenuState());
	}

	private boolean handlePauseButtonTap(int scaleX, int scaleY) {
		if (!mGamePaused && !isRoundFinished()
				&& isPointInside(scaleX, scaleY, PAUSE_BTN_X, PAUSE_BTN_Y, PAUSE_BTN_SIZE, PAUSE_BTN_SIZE)) {
			mGamePaused = true;
			mTouchDown = false;
			mDX = 0;
			mDY = 0;
			Assets.stopMusic();
			return true;
		}
		return false;
	}

	private boolean handleOverlayTap(int scaleX, int scaleY) {
		if (mGamePaused) {
			if (isPointInside(scaleX, scaleY, getOverlayButtonX(3, 0), OVERLAY_BUTTON_Y, OVERLAY_BUTTON_W, OVERLAY_BUTTON_H)) {
				mGamePaused = false;
				Assets.playMusic("backgoundsound.mid", true);
				return true;
			}
			if (isPointInside(scaleX, scaleY, getOverlayButtonX(3, 1), OVERLAY_BUTTON_Y, OVERLAY_BUTTON_W, OVERLAY_BUTTON_H)) {
				restartGame();
				return true;
			}
			if (isPointInside(scaleX, scaleY, getOverlayButtonX(3, 2), OVERLAY_BUTTON_Y, OVERLAY_BUTTON_W, OVERLAY_BUTTON_H)) {
				returnToMenu();
				return true;
			}
			return true;
		}

		if (isRoundFinished()) {
			String[] labels = getRoundEndButtonLabels();
			for (int i = 0; i < labels.length; i++) {
				if (isPointInside(scaleX, scaleY, getOverlayButtonX(labels.length, i), OVERLAY_BUTTON_Y, OVERLAY_BUTTON_W, OVERLAY_BUTTON_H)) {
					if (didClearLevel() && hasNextLevel()) {
						if (i == 0) {
							nextLevel();
						}else if (i == 1) {
							restartGame();
						}else {
							returnToMenu();
						}
					}else if (i == 0) {
						restartGame();
					}else {
						returnToMenu();
					}
					return true;
				}
			}
			return true;
		}

		return false;
	}

	private boolean handleDebugTap(int scaleX, int scaleY) {
		int btnLeft = GameMainActivity.GAME_WIDTH - DEBUG_BTN_SIZE - DEBUG_BTN_MARGIN;
		int btnTop = GameMainActivity.GAME_HEIGHT - DEBUG_BTN_SIZE - DEBUG_BTN_MARGIN;
		if (!mDebugPanelVisible) {
			if (isPointInside(scaleX, scaleY, btnLeft, btnTop, DEBUG_BTN_SIZE, DEBUG_BTN_SIZE)) {
				mDebugPanelVisible = true;
				mTouchDown = false;
				mDX = 0;
				mDY = 0;
				return true;
			}
			return false;
		}

		int closeX = DEBUG_PANEL_X + DEBUG_PANEL_W - DEBUG_CLOSE_W - 18;
		int closeY = DEBUG_PANEL_Y + DEBUG_PANEL_H - DEBUG_CLOSE_H - 16;
		if (isPointInside(scaleX, scaleY, closeX, closeY, DEBUG_CLOSE_W, DEBUG_CLOSE_H)) {
			mDebugPanelVisible = false;
			return true;
		}

		int decX = DEBUG_PANEL_X + 26;
		int decY = DEBUG_PANEL_Y + 154;
		int incX = decX + DEBUG_SPEED_DEC_W + 12;
		int incY = decY;
		if (isPointInside(scaleX, scaleY, decX, decY, DEBUG_SPEED_DEC_W, DEBUG_SPEED_DEC_H)) {
			mDebugGameSpeed = Math.max(0.25f, mDebugGameSpeed - 0.25f);
			return true;
		}
		if (isPointInside(scaleX, scaleY, incX, incY, DEBUG_SPEED_INC_W, DEBUG_SPEED_INC_H)) {
			mDebugGameSpeed = Math.min(5.0f, mDebugGameSpeed + 0.25f);
			return true;
		}

		int spawnX = DEBUG_PANEL_X + 26;
		int spawnY = DEBUG_PANEL_Y + 236;
		if (isPointInside(scaleX, scaleY, spawnX, spawnY, DEBUG_SPAWN_W, DEBUG_SPAWN_H)) {
			spawnDebugSmallFish();
			return true;
		}

		// Panel opened: consume input so gameplay controls are blocked.
		return true;
	}

	private int mTouchX,mTouchY;
	private boolean mTouchDown;
	private int mDX,mDY;

	@Override
	public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			if (handleDebugTap(scaleX, scaleY)) {
				return true;
			}
			if (handlePauseButtonTap(scaleX, scaleY) || handleOverlayTap(scaleX, scaleY)) {
				return true;
			}
			if (mAutoMode) {
				return true;
			}
			mTouchX = scaleX;
			mTouchY = scaleY;
			mTouchDown = true;
		}
		else if (e.getAction() == MotionEvent.ACTION_MOVE) {
			if (mAutoMode) {
				return true;
			}
			//mMyFish.movePress(scaleX - mTouchX, scaleY - mTouchY);
			//mTouchX = scaleX;
			//mTouchY = scaleY;
			if (scaleX - mTouchX > 10)
				mDX = 10;
			else if(scaleX - mTouchX < -10)
				mDX = -10;
			else
				mDX = 0;
			if (scaleY - mTouchY > 10)
				mDY = 10;
			else if (scaleY - mTouchY < -10)
				mDY = -10;
			else
				mDY = 0;
		}
		else if (e.getAction() == MotionEvent.ACTION_UP) {
			if (mDebugPanelVisible || mGamePaused || isRoundFinished()) {
				return true;
			}
			if (mAutoMode) {
				return true;
			}
			mTouchDown = false;
		}
		return true;
	}

	private void spawnPowerUp() {
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

	private void spawnDebugSmallFish() {
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

	private void checkPowerUpCollision() {
		Iterator<PowerUp> it = mPowerUps.iterator();
		while (it.hasNext()) {
			PowerUp p = it.next();
			if (mMyFish.collidesWith(p, false)) {
				applyPowerUp(p);
				it.remove();
				mLayerManager.remove(p);
			}
		}
	}

	private void applyPowerUp(PowerUp p) {
		switch (p.type) {
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
		}
	}

	public void checkCollides() {

		for (Fish f : mOtherFish) {
			if (f.collidesWith(mMyFish, false)) {

				if (f.mSize >= mMyFish.mSize) {

					// Shield blocks one hit
					if (mMyFish.mHasShield) {
						mMyFish.mHasShield = false;
						f.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
						continue;
					}

					if (f.mNonceState == Fish.SWIML || f.mNonceState == Fish.SWERVE_L) {
						f.setNonceState(Fish.EATL);
					}else if (f.mNonceState == Fish.SWIMR || f.mNonceState == Fish.SWERVE_R) {
						f.setNonceState(Fish.EATR);
					}
					mLife--;
					if (mLife > 0){
						mMyFish.setNonceState(Fish.DIE);
					}

					mMyFish.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);

				}else {
					if (mMyFish.mNonceState == Fish.SWIML || mMyFish.mNonceState == Fish.SWERVE_L) {
						mMyFish.setNonceState(Fish.EATL);
					}else if (mMyFish.mNonceState == Fish.SWIMR || mMyFish.mNonceState == Fish.SWERVE_R) {
						mMyFish.setNonceState(Fish.EATR);
					}

					if (mEndlessMode || mScore < mLevelConfig.targetScore) {
						mScore += (f.mSize + 1)* 20;
						if (mEndlessMode && mScore > mEndlessHighScore) {
							mEndlessHighScore = mScore;
							GameMainActivity.saveEndlessHighScore(mEndlessHighScore);
						}else if (mScore > mHighScore) {
							mHighScore = mScore;
							GameMainActivity.saveHighScore(mHighScore);
						}
					}
					if (!mEndlessMode && mScore >= mLevelConfig.targetScore) {
						mScore = mLevelConfig.targetScore;
						if (mScore > mHighScore) {
							mHighScore = mScore;
							GameMainActivity.saveHighScore(mHighScore);
						}
						mMyFish.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
					}
					else if (mScore >= 70)
						mMyFish.setSize(Fish.SUPER);
					else if (mScore >= 30)
						mMyFish.setSize(Fish.BIG);
					f.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
				}

			}
		}

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
		if (mEndlessMode) {
			return "无尽模式";
		}
		return "第" + mLevelConfig.index + "关";
	}

	private String getScoreLabel() {
		if (mEndlessMode) {
			return "分数 " + mScore;
		}
		return "分数 " + mScore + " / " + mLevelConfig.targetScore;
	}

	private boolean mGamePaused = false;

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mGamePaused = true;
		Assets.stopMusic();
	}
}

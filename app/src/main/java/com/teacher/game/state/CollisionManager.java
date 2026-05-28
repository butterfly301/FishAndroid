package com.teacher.game.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.teacher.fish.GameMainActivity;
import com.teacher.game.model.CompanionFish;
import com.teacher.game.model.Fish;
import com.teacher.game.model.MyFish;
import com.teacher.game.model.PowerUp;
import com.teacher.game.model.PowerUpType;

import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;

/**
 * Collision detection and damage / eat response for the gameplay loop.
 * Reads mutable state from PlayState and mutates it via package-private access.
 */
public class CollisionManager {

    private static final int COMPANION_CHARGE_TARGET = 6;

    private final PlayState mState;

    public CollisionManager(PlayState state) {
        mState = state;
    }

    // ================================================================
    //  Main fish vs other-fish collision (eat / get eaten)
    // ================================================================

    public void checkCollides() {
        for (Fish f : mState.mOtherFish) {
            if (mState.isRoundFinished()) {
                break;
            }
            if (f.collidesWith(mState.mMyFish, false)) {

                if (f.mSize >= mState.mMyFish.mSize) {
                    // Shield blocks one hit
                    if (mState.mMyFish.mHasShield) {
                        mState.mMyFish.mHasShield = false;
                        f.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
                        continue;
                    }

                    if (f.mNonceState == Fish.SWIML || f.mNonceState == Fish.SWERVE_L) {
                        f.setNonceState(Fish.EATL);
                    } else if (f.mNonceState == Fish.SWIMR || f.mNonceState == Fish.SWERVE_R) {
                        f.setNonceState(Fish.EATR);
                    }
                    mState.mLife--;
                    mState.resetCombo();
                    if (mState.mLife > 0) {
                        mState.mMyFish.setNonceState(Fish.DIE);
                    }

                    mState.mMyFish.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
                    break;

				} else {
					// Player eats the smaller fish
					mState.mFishEaten++;
					if (mState.mMyFish.mNonceState == Fish.SWIML || mState.mMyFish.mNonceState == Fish.SWERVE_L) {
						mState.mMyFish.setNonceState(Fish.EATL);
					} else if (mState.mMyFish.mNonceState == Fish.SWIMR || mState.mMyFish.mNonceState == Fish.SWERVE_R) {
						mState.mMyFish.setNonceState(Fish.EATR);
					}

					if (mState.mModeRules.canGainScore(mState.mScore, mState.mLevelConfig.targetScore)) {
                        int points = (f.mSize + 1) * 20;
                        if (f.mBehavior == Fish.Behavior.FLEE) points = points * 3 / 2;
                        float comboMult = mState.registerEat();
                        points = (int) (points * comboMult);
                        mState.addScore(points);
                        mState.mCompanionCharge = Math.min(COMPANION_CHARGE_TARGET, mState.mCompanionCharge + 1);
                        mState.spawnCompanionIfReady();
                    }
                    if (mState.didClearLevel()) {
                        mState.mMyFish.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
                        break;
                    }
                    else if (mState.mScore >= 70)
                        mState.mMyFish.setSize(Fish.SUPER);
                    else if (mState.mScore >= 30)
                        mState.mMyFish.setSize(Fish.BIG);
                    f.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
                }

            }
        }
    }

    // ================================================================
    //  Companion-fish vs other-fish collision
    // ================================================================

    public void checkCompanionCollides() {
        if (mState.mCompanionFishList == null || mState.mCompanionFishList.isEmpty()) {
            return;
        }
        for (CompanionFish companion : mState.mCompanionFishList) {
            for (Fish f : mState.mOtherFish) {
                if (!isOnScreen(f) || !companion.collidesWith(f, false)) {
                    continue;
                }
				if (f.mSize < mState.mMyFish.mSize) {
					int points = (f.mSize + 1) * 10;
					if (f.mBehavior == Fish.Behavior.FLEE) points = points * 3 / 2;
					mState.addScore(points);
					mState.mCompanionAssists++;
					companion.recordAssistEat();
                    f.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
                }
            }
        }
    }

    // ================================================================
    //  Player-fish vs power-up collision
    // ================================================================

    public void checkPowerUpCollision() {
        Iterator<PowerUp> it = mState.mPowerUps.iterator();
        while (it.hasNext()) {
            PowerUp p = it.next();
			if (mState.mMyFish.collidesWith(p, false)) {
				mState.mPowerUpsCollected++;
				applyPowerUp(p);
				if (mState.mAutoPilot != null) {
					mState.mAutoPilot.onPowerUpEaten(p);
				}
				it.remove();
                mState.mLayerManager.remove(p);
            }
        }
    }

    // ================================================================
    //  Power-up application
    // ================================================================

    private void applyPowerUp(PowerUp p) {
        switch (p.type) {
            case SPEED:
                mState.mSpeedTimer = PowerUpType.SPEED.duration;
                mState.mMyFish.mSpeedMultiplier = 2.0f;
                break;
            case SHIELD:
                mState.mMyFish.mHasShield = true;
                break;
            case FREEZE:
                mState.mFreezeTimer = PowerUpType.FREEZE.duration;
                break;
            case BOMB:
                for (Fish f : mState.mOtherFish) {
                    if (f.mSize >= mState.mMyFish.mSize && isOnScreen(f)) {
                        f.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
                    }
                }
                break;
            case LURE:
                mState.mLureTimer = PowerUpType.LURE.duration;
                break;
        }
    }

    // ================================================================
    //  Utility
    // ================================================================

    private boolean isOnScreen(Sprite s) {
        return s.getX() > -s.getWidth()
                && s.getX() < GameMainActivity.GAME_WIDTH
                && s.getY() > -s.getHeight()
                && s.getY() < GameMainActivity.GAME_HEIGHT;
    }
}

package com.teacher.game.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.teacher.fish.Assets;
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
            boolean headCollision = f.collidesWith(mState.mMyFish, false);
            boolean bodyFallback = f.mSize >= mState.mMyFish.mSize && bodyOverlap(f, mState.mMyFish);
            if (headCollision || bodyFallback) {

				if (f.mSize >= mState.mMyFish.mSize) {
					// Shield blocks one hit
					if (mState.mMyFish.mHasShield) {
						mState.mMyFish.mHasShield = false;
						Assets.playSound(Assets.sfxShield);
						f.setPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
						continue;
					}
					mState.onPlayerDamaged(f);
					break;

				} else {
					// Player eats the smaller fish
					int points = (f.mSize + 1) * 20;
					float comboMult = mState.registerEat();
					points = (int) (points * comboMult);
					mState.onPlayerEatFish(f, points);
					if (mState.didClearLevel()) {
						break;
					}
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
					Assets.playSound(Assets.sfxEat);
					mState.onCompanionEatFish(companion, f, points);
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
				mState.onCollectPowerUp(p);
				if (mState.mAutoPilot != null) {
					mState.mAutoPilot.onPowerUpEaten(p);
				}
				it.remove();
                mState.mLayerManager.remove(p);
            }
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

	private boolean bodyOverlap(Sprite a, Sprite b) {
		int aInsetX = a.getWidth() / 5;
		int aInsetY = a.getHeight() / 5;
		int bInsetX = b.getWidth() / 5;
		int bInsetY = b.getHeight() / 5;

		int aLeft = a.getX() + aInsetX;
		int aTop = a.getY() + aInsetY;
		int aRight = a.getX() + a.getWidth() - aInsetX;
		int aBottom = a.getY() + a.getHeight() - aInsetY;

		int bLeft = b.getX() + bInsetX;
		int bTop = b.getY() + bInsetY;
		int bRight = b.getX() + b.getWidth() - bInsetX;
		int bBottom = b.getY() + b.getHeight() - bInsetY;

		return aLeft < bRight && aRight > bLeft
				&& aTop < bBottom && aBottom > bTop;
	}
}

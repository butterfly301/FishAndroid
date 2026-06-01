package com.teacher.game.state;

import java.util.List;

import com.teacher.fish.GameMainActivity;
import com.teacher.game.model.Fish;
import com.teacher.game.model.MyFish;
import com.teacher.game.model.PowerUp;
import com.teacher.game.model.PowerUpType;

import javax.microedition.lcdui.game.Sprite;

/**
 * AI auto-pilot that drives the player fish toward food targets,
 * evades threats, seeks power-ups, and avoids screen boundaries.
 */
public class AutoPilot {

    public enum State {
        ESCAPE_BOUNDARY,
        ALIGN_TARGET,
        CRUISE
    }

    // --- auto-pilot state ---

    private float mDecisionTimer;
    private Fish mTargetFish;
    private PowerUp mTargetPowerUp;
    private float mTargetLockTime;
    private float mPowerUpLockTime;
    private State mPilotState;

    public AutoPilot() {
        reset();
    }

    public void reset() {
        mDecisionTimer = 0;
        mTargetFish = null;
        mTargetPowerUp = null;
        mTargetLockTime = 0;
        mPowerUpLockTime = 0;
        mPilotState = State.CRUISE;
    }

    // --- public accessors (for CollisionManager / PlayState) ---

    public State getPilotState() {
        return mPilotState;
    }

    public Fish getTargetFish() {
        return mTargetFish;
    }

    public PowerUp getTargetPowerUp() {
        return mTargetPowerUp;
    }

    /** Called by CollisionManager when a power-up the auto-pilot was chasing gets eaten. */
    public void onPowerUpEaten(PowerUp p) {
        if (mTargetPowerUp == p) {
            mTargetPowerUp = null;
            mPowerUpLockTime = 0;
        }
    }

    // ================================================================
    //  Main update — called every frame from PlayState.update()
    // ================================================================

    public void update(float effectiveDelta,
                       MyFish myFish,
                       List<Fish> otherFish,
                       List<PowerUp> powerUps,
                       float freezeTimer,
                       float speedTimer,
                       float lureTimer,
                       boolean hasShield) {

        if (myFish.mStartTime <= 10 || myFish.mNonceState == Fish.DIE) {
            mTargetFish = null;
            mTargetPowerUp = null;
            mTargetLockTime = 0;
            mPowerUpLockTime = 0;
            mPilotState = State.CRUISE;
            return;
        }

        mDecisionTimer += effectiveDelta;
        mTargetLockTime += effectiveDelta;
        mPowerUpLockTime += effectiveDelta;

		if (mDecisionTimer < GameplayTuning.AUTOPILOT_DECISION_INTERVAL) {
			return;
		}
        mDecisionTimer = 0;

        int myCenterX = myFish.getX() + myFish.getWidth() / 2;
        int myCenterY = myFish.getY() + myFish.getHeight() / 2;
        int escapeDx = getBoundaryEscapeDx(myCenterX);
        int escapeDy = getBoundaryEscapeDy(myCenterY);
        boolean escapingBoundary = escapeDx != 0 || escapeDy != 0;

        Fish nearestThreat = null;
        double nearestThreatDistance = Double.MAX_VALUE;
        Fish bestFood = null;
        double bestFoodScore = Double.MAX_VALUE;
        PowerUp bestPowerUp = null;
        double bestPowerUpScore = Double.MAX_VALUE;

        for (Fish fish : otherFish) {
            if (!isOnScreen(fish)) {
                continue;
            }
            int fishCenterX = fish.getX() + fish.getWidth() / 2;
            int fishCenterY = fish.getY() + fish.getHeight() / 2;
            double distance = distance(myCenterX, myCenterY, fishCenterX, fishCenterY);

            if (fish.mSize >= myFish.mSize) {
                if (distance < nearestThreatDistance) {
                    nearestThreatDistance = distance;
                    nearestThreat = fish;
                }
            } else {
                double candidateScore = scoreFoodTarget(fish, myCenterX, myCenterY, myFish, otherFish);
                if (candidateScore < bestFoodScore) {
                    bestFoodScore = candidateScore;
                    bestFood = fish;
                }
            }
        }

        for (PowerUp powerUp : powerUps) {
            if (!isOnScreen(powerUp)) {
                continue;
            }
            double candidateScore = scorePowerUpTarget(powerUp, myCenterX, myCenterY,
                    hasShield, speedTimer, freezeTimer, lureTimer, otherFish);
            if (candidateScore < bestPowerUpScore) {
                bestPowerUpScore = candidateScore;
                bestPowerUp = powerUp;
            }
        }

        // --- Decision tree ---

        int targetDx = 0;
        int targetDy = 0;

        if (escapingBoundary) {
            mPilotState = State.ESCAPE_BOUNDARY;
            mTargetFish = null;
            mTargetLockTime = 0;
            targetDx = escapeDx;
            targetDy = escapeDy;
        } else if (shouldSeekPowerUp(bestPowerUp, nearestThreatDistance,
                hasShield, speedTimer, freezeTimer, lureTimer, powerUps.size())) {
            PowerUp chasePowerUp = choosePowerUpTarget(bestPowerUp, myCenterX, myCenterY);
            if (chasePowerUp != null) {
                int powerUpCenterX = chasePowerUp.getX() + chasePowerUp.getWidth() / 2;
                int powerUpCenterY = chasePowerUp.getY() + chasePowerUp.getHeight() / 2;
                targetDx = powerUpCenterX - myCenterX;
                targetDy = powerUpCenterY - myCenterY;
                mPilotState = State.ALIGN_TARGET;
                mTargetFish = null;
            } else {
                mTargetPowerUp = null;
                mPowerUpLockTime = 0;
            }
        } else {
            mTargetPowerUp = null;
            mPowerUpLockTime = 0;
            Fish chaseFish = chooseTarget(bestFood, myCenterX, myCenterY, myFish, otherFish);
            if (chaseFish != null) {
                int liveCenterX = chaseFish.getX() + chaseFish.getWidth() / 2;
                int liveCenterY = chaseFish.getY() + chaseFish.getHeight() / 2;
                int liveDx = liveCenterX - myCenterX;
                int liveDy = liveCenterY - myCenterY;

                if (Math.abs(liveDx) <= 420 || (Math.abs(liveDx) <= 520 && Math.abs(liveDy) <= 150)) {
                    targetDx = liveDx;
                    targetDy = liveDy;
                    mPilotState = State.ALIGN_TARGET;
                } else {
                    int[] predicted = predictFishCenter(chaseFish);
                    targetDx = predicted[0] - myCenterX;
                    targetDy = predicted[1] - myCenterY;
                    mPilotState = State.ALIGN_TARGET;
                }
            } else {
                mPilotState = State.CRUISE;
                int centerX = GameMainActivity.GAME_WIDTH / 2;
                int centerY = (GameMainActivity.getPlayTop() + GameMainActivity.getPlayBottom()) / 2;
                targetDx = centerX - myCenterX;
                targetDy = centerY - myCenterY;
            }
        }

        // --- Apply movement ---

        int desiredX = normalizeControl(targetDx, true);
        int desiredY = normalizeControl(targetDy, false);
        int prevInputX = myFish.mMoveX;
        int prevInputY = myFish.mMoveY;
        int newInputX = smoothAutoAxis(prevInputX, desiredX, targetDx, 170);
        int newInputY = smoothAutoAxis(prevInputY, desiredY, targetDy, 220);
        myFish.movePress(newInputX, newInputY);
    }

    // ================================================================
    //  Target selection helpers
    // ================================================================

    private Fish chooseTarget(Fish nearestFood, int myCenterX, int myCenterY,
                              MyFish myFish, List<Fish> otherFish) {
        if (mTargetFish != null) {
            if (!isOnScreen(mTargetFish) || mTargetFish.mSize >= myFish.mSize) {
                mTargetFish = null;
                mTargetLockTime = 0;
            } else {
                int[] predicted = predictFishCenter(mTargetFish);
                int targetCenterX = predicted[0];
                int targetCenterY = predicted[1];
                double distance = distance(myCenterX, myCenterY, targetCenterX, targetCenterY);
				if (distance < 260 || mTargetLockTime < GameplayTuning.AUTOPILOT_TARGET_LOCK_SECONDS) {
                    return mTargetFish;
                }
            }
        }

        mTargetFish = nearestFood;
        mTargetLockTime = 0;
        return mTargetFish;
    }

    private PowerUp choosePowerUpTarget(PowerUp nearestPowerUp, int myCenterX, int myCenterY) {
        if (mTargetPowerUp != null) {
            if (!isOnScreen(mTargetPowerUp)) {
                mTargetPowerUp = null;
                mPowerUpLockTime = 0;
            } else {
                int targetCenterX = mTargetPowerUp.getX() + mTargetPowerUp.getWidth() / 2;
                int targetCenterY = mTargetPowerUp.getY() + mTargetPowerUp.getHeight() / 2;
                double distance = distance(myCenterX, myCenterY, targetCenterX, targetCenterY);
				if (distance < 320 || mPowerUpLockTime < GameplayTuning.AUTOPILOT_TARGET_LOCK_SECONDS) {
                    return mTargetPowerUp;
                }
            }
        }

        mTargetPowerUp = nearestPowerUp;
        mPowerUpLockTime = 0;
        return mTargetPowerUp;
    }

    private boolean shouldSeekPowerUp(PowerUp bestPowerUp, double nearestThreatDistance,
                                      boolean hasShield, float speedTimer, float freezeTimer,
                                      float lureTimer, int powerUpCount) {
        if (bestPowerUp == null) {
            return false;
        }
        if (nearestThreatDistance < 260) {
            return false;
        }
        if (bestPowerUp.type == PowerUpType.SHIELD && !hasShield) {
            return true;
        }
        if (bestPowerUp.type == PowerUpType.SPEED && speedTimer <= 0) {
            return true;
        }
        if (bestPowerUp.type == PowerUpType.FREEZE && freezeTimer <= 0) {
            return true;
        }
        if (bestPowerUp.type == PowerUpType.BOMB) {
            return true;
        }
        if (bestPowerUp.type == PowerUpType.LURE && lureTimer <= 0) {
            return true;
        }
        return powerUpCount >= 2;
    }

    private double scorePowerUpTarget(PowerUp powerUp, int myCenterX, int myCenterY,
                                      boolean hasShield, float speedTimer, float freezeTimer,
                                      float lureTimer, List<Fish> otherFish) {
        int powerUpCenterX = powerUp.getX() + powerUp.getWidth() / 2;
        int powerUpCenterY = powerUp.getY() + powerUp.getHeight() / 2;
        double score = distance(myCenterX, myCenterY, powerUpCenterX, powerUpCenterY);

        switch (powerUp.type) {
            case SHIELD:
                score += hasShield ? 120 : -180;
                break;
            case SPEED:
                score += speedTimer > 0 ? 90 : -120;
                break;
            case FREEZE:
                score += freezeTimer > 0 ? 110 : -100;
                break;
            case BOMB:
                score -= 60;
                break;
            case LURE:
                score += lureTimer > 0 ? 130 : -110;
                break;
        }

        for (Fish fish : otherFish) {
            if (!isOnScreen(fish) || fish.mSize < (byte)0) { // only threats matter here
                continue;
            }
            int fishCenterX = fish.getX() + fish.getWidth() / 2;
            int fishCenterY = fish.getY() + fish.getHeight() / 2;
            double dangerDistance = distance(powerUpCenterX, powerUpCenterY, fishCenterX, fishCenterY);
            if (dangerDistance < 170) {
                score += 260 - dangerDistance;
            }
        }

        return score;
    }

    private double scoreFoodTarget(Fish fish, int myCenterX, int myCenterY,
                                   MyFish myFish, List<Fish> otherFish) {
        int[] predicted = predictFishCenter(fish);
        int fishCenterX = predicted[0];
        int fishCenterY = predicted[1];
        double distance = distance(myCenterX, myCenterY, fishCenterX, fishCenterY);
        double score = distance;

        score -= (myFish.mSize - fish.mSize) * 26;

        if ((myFish.mMoveX >= 0 && fishCenterX >= myCenterX) || (myFish.mMoveX <= 0 && fishCenterX <= myCenterX)) {
            score -= 18;
        }

        for (Fish other : otherFish) {
            if (other == fish || !isOnScreen(other) || other.mSize < myFish.mSize) {
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

    // ================================================================
    //  Movement helpers
    // ================================================================

    private int[] predictFishCenter(Fish fish) {
        int leadFrames = 10;
        int predictedX = fish.getX() + fish.getWidth() / 2 + fish.mMoveX * leadFrames;
        int predictedY = fish.getY() + fish.getHeight() / 2 + fish.mMoveY * leadFrames;
        int minY = GameMainActivity.getPlayTop() + fish.getHeight() / 2;
        int maxY = GameMainActivity.getPlayBottom() - fish.getHeight() / 2;
        if (predictedY < minY) {
            predictedY = minY;
        } else if (predictedY > maxY) {
            predictedY = maxY;
        }
        return new int[]{predictedX, predictedY};
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

    // ================================================================
    //  Utility
    // ================================================================

    private double distance(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private int normalizeControl(int delta, boolean horizontal) {
        if (horizontal) {
            if (delta > 160) return 160;
            if (delta < -160) return -160;
            if (delta > 70) return 100;
            if (delta < -70) return -100;
            return 0;
        }
        if (delta > 120) return 140;
        if (delta < -120) return -140;
        if (delta > 56) return 80;
        if (delta < -56) return -80;
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

    private boolean isOnScreen(Sprite s) {
        return s.getX() > -s.getWidth()
                && s.getX() < GameMainActivity.GAME_WIDTH
                && s.getY() > -s.getHeight()
                && s.getY() < GameMainActivity.GAME_HEIGHT;
    }
}

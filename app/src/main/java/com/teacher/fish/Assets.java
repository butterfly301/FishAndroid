package com.teacher.fish;

import java.io.IOException;
import java.io.InputStream;

import com.teacher.framework.animation.Animation;
import com.teacher.framework.animation.Frame;
import com.teacher.game.model.PowerUpType;

import android.content.res.AssetFileDescriptor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class Assets {

	private enum VariantDecoration {
		GLOW_SURGEON,
		SUN_TUNA,
		ROYAL_LION,
		REEF_SHARK
	}
	
	private static SoundPool soundPool;
	
	public static Bitmap welcome, logo, menu, menuText, menuItem, bgimg7,
						background, backgroundLevel2, backgroundLevel3, backgroundEndless,
						floatgrass, airbubble,
						suergeonfish, suergeonfishVariant, tuna, tunaVariant, lion, lionVariant, shark, sharkVariant,
						angelfishnormal,angelfishbig,angelfishsuper,
						virjoy_outter, virjoy_inner,
						gameover,sorry,pass;
	
	public static Animation runAnim;
	
	public static int selectedID;

	// ---- Sound effect IDs ----
	public static int sfxEat, sfxEatBig, sfxHit, sfxPowerup, sfxCombo,
			sfxWin, sfxLose, sfxShield, sfxBomb, sfxLure;

	// ---- Sound/Music control flags ----
	public static boolean sSoundEnabled = true;
	public static boolean sMusicEnabled = true;
	private static String sPendingMusicFile;
	private static boolean sPendingMusicLoop;

	public static void load() {
		welcome = loadBitmap("welcome.png", false);
		logo = loadBitmap("logo.png", false);
		menu = loadBitmap("menu.png", false);
		menuText = loadBitmap("menutext.png", true);
		menuItem = loadBitmap("menuitem.png", true);
		bgimg7 = loadBitmap("bgimg7.jpg", false);
		background = loadBitmap("background.png", false);
		backgroundLevel2 = loadBitmap("background_level2.png", false);
		backgroundLevel3 = loadBitmap("background_level3.png", false);
		backgroundEndless = createTintedBackground(background,
				Color.argb(132, 48, 18, 76),
				Color.argb(42, 255, 180, 96));
		floatgrass = loadBitmap("floatgrass.png", true);
		airbubble = loadBitmap("airbubble.png", true);
		suergeonfish = loadBitmap("suergeonfish.png", true);
		suergeonfishVariant = createVariantFish(suergeonfish,
				new float[] {
						1.08f, 0.04f, 0.08f, 0, 8,
						0.02f, 1.12f, 0.12f, 0, 18,
						0.08f, 0.12f, 1.16f, 0, 26,
						0, 0, 0, 1, 0
				}, 42, 24, VariantDecoration.GLOW_SURGEON);
		tuna = loadBitmap("tuna.png", true);
		tunaVariant = createVariantFish(tuna,
				new float[] {
						1.12f, 0.10f, 0.00f, 0, 22,
						0.06f, 1.08f, 0.02f, 0, 8,
						0.00f, 0.05f, 0.92f, 0, -6,
						0, 0, 0, 1, 0
				}, 78, 40, VariantDecoration.SUN_TUNA);
		lion = loadBitmap("lion.png", true);
		lionVariant = createVariantFish(lion,
				new float[] {
						1.02f, 0.08f, 0.18f, 0, 12,
						0.02f, 0.96f, 0.12f, 0, -4,
						0.14f, 0.10f, 1.18f, 0, 30,
						0, 0, 0, 1, 0
				}, 110, 86, VariantDecoration.ROYAL_LION);
		shark = loadBitmap("shark.png", true);
		sharkVariant = createVariantFish(shark,
				new float[] {
						0.92f, 0.16f, 0.18f, 0, 6,
						0.08f, 1.02f, 0.14f, 0, 12,
						0.18f, 0.10f, 1.08f, 0, 24,
						0, 0, 0, 1, 0
				}, 220, 96, VariantDecoration.REEF_SHARK);
		angelfishnormal = loadBitmap("angelfishnormal.png", true);
		angelfishbig = loadBitmap("angelfishbig.png", true);
		angelfishsuper = loadBitmap("angelfishsuper.png", true);
		virjoy_outter = loadBitmap("virjoy_outter.png", true);
		virjoy_inner = loadBitmap("virjoy_inner.png", true);
		gameover = loadBitmap("gameover.png", true);
		sorry = loadBitmap("sorry.png", true);
		pass = loadBitmap("pass.png", true);
		
		selectedID = loadSound("select.mid");

		sfxEat = loadSound("sfx_eat.wav");
		sfxEatBig = loadSound("sfx_eat_big.wav");
		sfxHit = loadSound("sfx_hit.wav");
		sfxPowerup = loadSound("sfx_powerup.wav");
		sfxCombo = loadSound("sfx_combo.wav");
		sfxWin = loadSound("sfx_win.wav");
		sfxLose = loadSound("sfx_lose.wav");
		sfxShield = loadSound("sfx_shield.wav");
		sfxBomb = loadSound("sfx_bomb.wav");
		sfxLure = loadSound("sfx_lure.wav");
		
		Frame f1 = new Frame(loadBitmap("run_anim1.png", true), 0.1);
		Frame f2 = new Frame(loadBitmap("run_anim2.png", true), 0.1);
		Frame f3 = new Frame(loadBitmap("run_anim3.png", true), 0.1);
		Frame f4 = new Frame(loadBitmap("run_anim4.png", true), 0.1);
		Frame f5 = new Frame(loadBitmap("run_anim5.png", true), 0.1);
		
		runAnim = new Animation(f1, f2, f3, f4, f5, f3, f2);
	}

	public static Bitmap getPlayBackground(int levelIndex, boolean endlessMode) {
		if (endlessMode) {
			return backgroundEndless != null ? backgroundEndless : background;
		}
		switch (levelIndex) {
			case 1:
				return backgroundLevel2 != null ? backgroundLevel2 : background;
			case 2:
				return backgroundLevel3 != null ? backgroundLevel3 : background;
			default:
				return background;
		}
	}

	
	private static Bitmap loadBitmap(String fileName,boolean alpha) {
		InputStream input = null;		
		try {
			input = GameMainActivity.assets.open(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Options options = new Options();
		if (alpha)
			options.inPreferredConfig = Config.ARGB_8888;
		else
			options.inPreferredConfig = Config.RGB_565;
		
		return BitmapFactory.decodeStream(input, null, options);
	}

	private static Bitmap createTintedBackground(Bitmap source, int shadowColor, int highlightColor) {
		if (source == null) {
			return null;
		}
		Bitmap tinted = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(tinted);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		canvas.drawBitmap(source, 0, 0, paint);

		paint.setStyle(Paint.Style.FILL);
		paint.setColor(shadowColor);
		canvas.drawRect(0, 0, source.getWidth(), source.getHeight(), paint);

		paint.setColor(highlightColor);
		canvas.drawRect(0, 0, source.getWidth(), source.getHeight() / 3, paint);

		paint.setColor(Color.argb(38, 255, 255, 255));
		canvas.drawRect(0, source.getHeight() / 2, source.getWidth(), source.getHeight() / 2 + 56, paint);

		return tinted;
	}

	private static Bitmap createVariantFish(Bitmap source, float[] colorMatrixValues,
			int frameWidth, int frameHeight, VariantDecoration decoration) {
		if (source == null) {
			return null;
		}
		Bitmap variant = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(variant);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		paint.setColorFilter(new android.graphics.ColorMatrixColorFilter(colorMatrixValues));
		canvas.drawBitmap(source, 0, 0, paint);
		applyVariantDecoration(canvas, frameWidth, frameHeight, decoration);
		return variant;
	}

	private static void applyVariantDecoration(Canvas canvas, int frameWidth, int frameHeight,
			VariantDecoration decoration) {
		int frameCount = Math.max(1, canvas.getWidth() / frameWidth);
		for (int i = 0; i < frameCount; i++) {
			int left = i * frameWidth;
			switch (decoration) {
				case GLOW_SURGEON:
					drawGlowSurgeonDecoration(canvas, left, frameWidth, frameHeight);
					break;
				case SUN_TUNA:
					drawSunTunaDecoration(canvas, left, frameWidth, frameHeight);
					break;
				case ROYAL_LION:
					drawRoyalLionDecoration(canvas, left, frameWidth, frameHeight);
					break;
				case REEF_SHARK:
					drawReefSharkDecoration(canvas, left, frameWidth, frameHeight);
					break;
			}
		}
	}

	private static void drawGlowSurgeonDecoration(Canvas canvas, int left, int frameWidth, int frameHeight) {
		Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
		line.setStyle(Paint.Style.STROKE);
		line.setStrokeCap(Paint.Cap.ROUND);
		line.setStrokeWidth(Math.max(2f, frameHeight * 0.08f));
		line.setColor(Color.argb(210, 110, 255, 245));
		canvas.drawLine(left + frameWidth * 0.22f, frameHeight * 0.34f,
				left + frameWidth * 0.70f, frameHeight * 0.18f, line);

		Paint dot = new Paint(Paint.ANTI_ALIAS_FLAG);
		dot.setStyle(Paint.Style.FILL);
		dot.setColor(Color.argb(210, 182, 255, 252));
		canvas.drawCircle(left + frameWidth * 0.44f, frameHeight * 0.42f, frameHeight * 0.10f, dot);
		canvas.drawCircle(left + frameWidth * 0.60f, frameHeight * 0.56f, frameHeight * 0.08f, dot);
	}

	private static void drawSunTunaDecoration(Canvas canvas, int left, int frameWidth, int frameHeight) {
		Paint stripe = new Paint(Paint.ANTI_ALIAS_FLAG);
		stripe.setStyle(Paint.Style.STROKE);
		stripe.setStrokeCap(Paint.Cap.ROUND);
		stripe.setStrokeWidth(Math.max(3f, frameHeight * 0.09f));
		stripe.setColor(Color.argb(220, 255, 244, 120));
		canvas.drawLine(left + frameWidth * 0.18f, frameHeight * 0.52f,
				left + frameWidth * 0.78f, frameHeight * 0.30f, stripe);
		canvas.drawLine(left + frameWidth * 0.22f, frameHeight * 0.66f,
				left + frameWidth * 0.72f, frameHeight * 0.48f, stripe);

		Paint tail = new Paint(Paint.ANTI_ALIAS_FLAG);
		tail.setStyle(Paint.Style.FILL);
		tail.setColor(Color.argb(210, 255, 140, 68));
		canvas.drawRect(left + frameWidth * 0.78f, frameHeight * 0.18f,
				left + frameWidth * 0.92f, frameHeight * 0.80f, tail);
	}

	private static void drawRoyalLionDecoration(Canvas canvas, int left, int frameWidth, int frameHeight) {
		Paint fin = new Paint(Paint.ANTI_ALIAS_FLAG);
		fin.setStyle(Paint.Style.FILL);
		fin.setColor(Color.argb(210, 255, 224, 110));
		Path crown = new Path();
		crown.moveTo(left + frameWidth * 0.24f, frameHeight * 0.22f);
		crown.lineTo(left + frameWidth * 0.34f, frameHeight * 0.02f);
		crown.lineTo(left + frameWidth * 0.44f, frameHeight * 0.24f);
		crown.lineTo(left + frameWidth * 0.54f, frameHeight * 0.04f);
		crown.lineTo(left + frameWidth * 0.64f, frameHeight * 0.25f);
		crown.close();
		canvas.drawPath(crown, fin);

		Paint dots = new Paint(Paint.ANTI_ALIAS_FLAG);
		dots.setStyle(Paint.Style.FILL);
		dots.setColor(Color.argb(195, 255, 247, 212));
		canvas.drawCircle(left + frameWidth * 0.42f, frameHeight * 0.46f, frameHeight * 0.055f, dots);
		canvas.drawCircle(left + frameWidth * 0.55f, frameHeight * 0.58f, frameHeight * 0.05f, dots);
		canvas.drawCircle(left + frameWidth * 0.70f, frameHeight * 0.46f, frameHeight * 0.05f, dots);
	}

	private static void drawReefSharkDecoration(Canvas canvas, int left, int frameWidth, int frameHeight) {
		Paint scar = new Paint(Paint.ANTI_ALIAS_FLAG);
		scar.setStyle(Paint.Style.STROKE);
		scar.setStrokeCap(Paint.Cap.ROUND);
		scar.setStrokeWidth(Math.max(3f, frameHeight * 0.05f));
		scar.setColor(Color.argb(210, 245, 246, 255));
		canvas.drawLine(left + frameWidth * 0.46f, frameHeight * 0.24f,
				left + frameWidth * 0.56f, frameHeight * 0.42f, scar);
		canvas.drawLine(left + frameWidth * 0.54f, frameHeight * 0.22f,
				left + frameWidth * 0.64f, frameHeight * 0.40f, scar);

		Paint edge = new Paint(Paint.ANTI_ALIAS_FLAG);
		edge.setStyle(Paint.Style.FILL);
		edge.setColor(Color.argb(180, 122, 255, 226));
		Path dorsal = new Path();
		dorsal.moveTo(left + frameWidth * 0.46f, frameHeight * 0.18f);
		dorsal.lineTo(left + frameWidth * 0.56f, frameHeight * 0.04f);
		dorsal.lineTo(left + frameWidth * 0.66f, frameHeight * 0.18f);
		dorsal.close();
		canvas.drawPath(dorsal, edge);
	}
	
	private static int loadSound(String fileName) {		
		if (soundPool == null)
			soundPool = new SoundPool(25, AudioManager.STREAM_MUSIC, 0);
		int id = 0;
		try {
			id = soundPool.load(GameMainActivity.assets.openFd(fileName), 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
	
	public static void playSound(int id) {
		if (!sSoundEnabled) return;
		soundPool.play(id, 1, 1, 1, 0, 1);
	}

	public static void setSoundEnabled(boolean enabled) {
		sSoundEnabled = enabled;
	}

	public static void setMusicEnabled(boolean enabled) {
		sMusicEnabled = enabled;
		if (enabled && sPendingMusicFile != null) {
			playMusic(sPendingMusicFile, sPendingMusicLoop);
		} else if (!enabled) {
			stopMusic();
		}
	}
	
	private static MediaPlayer mp;
	
	public static void playMusic(String fileName, boolean b) {
		sPendingMusicFile = fileName;
		sPendingMusicLoop = b;
		if (!sMusicEnabled) return;
		if (mp == null)
			mp = new MediaPlayer();
		
		try {
			AssetFileDescriptor afd =  GameMainActivity.assets.openFd(fileName);
			mp.setDataSource(afd.getFileDescriptor(), 
					afd.getStartOffset(), afd.getLength());
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.prepare();
			mp.setLooping(b);
			mp.start();			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void stopMusic() {
		if (mp != null) {
			mp.stop();
			mp.release();
			mp = null;
		}
	}

	public static Bitmap createPowerUpBitmap(PowerUpType type, int size) {
		Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

		int cx = size / 2;
		int cy = size / 2;
		int r = size / 2 - 2;

		// Background colored circle
		p.setStyle(Paint.Style.FILL);
		switch (type) {
			case SPEED:  p.setColor(Color.rgb(255, 215, 0));  break; // Gold
			case SHIELD: p.setColor(Color.rgb(40, 130, 255)); break; // Blue
			case FREEZE: p.setColor(Color.rgb(0, 200, 255));  break; // Cyan
			case BOMB:   p.setColor(Color.rgb(255, 60, 60));  break; // Red
			case LURE:   p.setColor(Color.rgb(255, 105, 180)); break; // Pink
		}
		canvas.drawCircle(cx, cy, r, p);

		// White border
		p.setColor(Color.WHITE);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(2.5f);
		canvas.drawCircle(cx, cy, r, p);

		// Inner symbol
		p.setStrokeWidth(3f);
		int hr = (int) (r * 0.5f);
		switch (type) {
			case SPEED:
				// Right-pointing triangle (>)
				canvas.drawLine(cx - hr + 2, cy - hr, cx + hr - 2, cy, p);
				canvas.drawLine(cx - hr + 2, cy + hr, cx + hr - 2, cy, p);
				canvas.drawLine(cx - hr + 2, cy - hr, cx - hr + 2, cy + hr, p);
				break;
			case SHIELD:
				// Semi-circle arch
				canvas.drawArc(cx - hr, cy - hr + 4, cx + hr, cy + hr + 4,
						0, 180, false, p);
				canvas.drawLine(cx - hr, cy + 4, cx - hr, cy + hr - 4, p);
				canvas.drawLine(cx + hr, cy + 4, cx + hr, cy + hr - 4, p);
				break;
			case FREEZE:
				// Diamond (rotated square)
				canvas.drawLine(cx, cy - hr, cx + hr, cy, p);
				canvas.drawLine(cx + hr, cy, cx, cy + hr, p);
				canvas.drawLine(cx, cy + hr, cx - hr, cy, p);
				canvas.drawLine(cx - hr, cy, cx, cy - hr, p);
				break;
			case BOMB:
				// Filled small circle + fuse line
				p.setStyle(Paint.Style.FILL);
				canvas.drawCircle(cx, cy + 2, hr - 4, p);
				p.setStyle(Paint.Style.STROKE);
				canvas.drawLine(cx, cy - hr + 4, cx, cy - hr - 4, p);
				canvas.drawCircle(cx, cy - hr - 4, 3, p);
				break;
			case LURE:
				// Three concentric arcs (magnetic / attraction symbol)
				p.setStyle(Paint.Style.STROKE);
				p.setStrokeWidth(2.5f);
				canvas.drawCircle(cx, cy, hr - 2, p);
				canvas.drawCircle(cx, cy, hr - 6, p);
				canvas.drawCircle(cx, cy, hr - 10, p);
				break;
		}

		return bmp;
	}
}

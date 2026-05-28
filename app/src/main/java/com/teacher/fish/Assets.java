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
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class Assets {
	
	private static SoundPool soundPool;
	
	public static Bitmap welcome, logo, menu, menuText, menuItem, bgimg7,
						background, floatgrass, airbubble,
						suergeonfish, tuna, lion, shark,
						angelfishnormal,angelfishbig,angelfishsuper,
						virjoy_outter, virjoy_inner,
						gameover,sorry,pass;
	
	public static Animation runAnim;
	
	public static int selectedID;
	
	public static void load() {
		welcome = loadBitmap("welcome.png", false);
		logo = loadBitmap("logo.png", false);
		menu = loadBitmap("menu.png", false);
		menuText = loadBitmap("menutext.png", true);
		menuItem = loadBitmap("menuitem.png", true);
		bgimg7 = loadBitmap("bgimg7.jpg", false);
		background = loadBitmap("background.png", false);
		floatgrass = loadBitmap("floatgrass.png", true);
		airbubble = loadBitmap("airbubble.png", true);
		suergeonfish = loadBitmap("suergeonfish.png", true);
		tuna = loadBitmap("tuna.png", true);
		lion = loadBitmap("lion.png", true);
		shark = loadBitmap("shark.png", true);
		angelfishnormal = loadBitmap("angelfishnormal.png", true);
		angelfishbig = loadBitmap("angelfishbig.png", true);
		angelfishsuper = loadBitmap("angelfishsuper.png", true);
		virjoy_outter = loadBitmap("virjoy_outter.png", true);
		virjoy_inner = loadBitmap("virjoy_inner.png", true);
		gameover = loadBitmap("gameover.png", true);
		sorry = loadBitmap("sorry.png", true);
		pass = loadBitmap("pass.png", true);
		
		selectedID = loadSound("select.mid");
		
		Frame f1 = new Frame(loadBitmap("run_anim1.png", true), 0.1);
		Frame f2 = new Frame(loadBitmap("run_anim2.png", true), 0.1);
		Frame f3 = new Frame(loadBitmap("run_anim3.png", true), 0.1);
		Frame f4 = new Frame(loadBitmap("run_anim4.png", true), 0.1);
		Frame f5 = new Frame(loadBitmap("run_anim5.png", true), 0.1);
		
		runAnim = new Animation(f1, f2, f3, f4, f5, f3, f2);
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
		soundPool.play(id, 1, 1, 1, 0, 1);
	}
	
	private static MediaPlayer mp;
	
	public static void playMusic(String fileName, boolean b) {
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

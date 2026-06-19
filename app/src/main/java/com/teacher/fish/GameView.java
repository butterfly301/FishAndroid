package com.teacher.fish;


import com.teacher.framework.util.InputHandler;
import com.teacher.framework.util.Painter;
import com.teacher.game.state.LoadState;
import com.teacher.game.state.State;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class GameView extends SurfaceView  implements Runnable {

	private static final int RENDER_SCALE = 1;

	private Thread gameThread;
	private volatile boolean running;

	private Rect gameImageSrc,gameImageDst;
	private Bitmap gameImage;
	private Canvas gameCanvas;
	private Painter g;

	private InputHandler inputHandler;

	private void initInput() {
		if (inputHandler == null)
			inputHandler = new InputHandler();
		setOnTouchListener(inputHandler);
	}

	public GameView(Context context,int gameWidth, int gameHeight) {
		super(context);

		int renderWidth = gameWidth * RENDER_SCALE;
		int renderHeight = gameHeight * RENDER_SCALE;
		gameImage = Bitmap.createBitmap(renderWidth, renderHeight, Bitmap.Config.ARGB_8888);
		gameImageSrc = new Rect(0, 0, renderWidth, renderHeight);
		gameImageDst = new Rect();
		gameCanvas = new Canvas(gameImage);
		gameCanvas.scale(RENDER_SCALE, RENDER_SCALE);
		g = new Painter(gameCanvas);

		SurfaceHolder holder = getHolder();
		holder.addCallback(new Callback() {

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
									   int width, int height) {

			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Log.e("GameView", "视图创建");
				initInput();
				if (currentState == null)
					setCurrentState(new LoadState());
				initGame();
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				Log.e("GameView", "视图销毁");
				pauseGame();
			}

		});

	}

	public GameView(Context context) {
		super(context);
	}

	private volatile State currentState;

	public void setCurrentState(State newState) {
		System.gc();
		newState.init();
		currentState = newState;
		inputHandler.setCurrentState(currentState);
	}

	private void initGame() {
		running = true;
		gameThread = new Thread(this, "游戏线程");
		gameThread.start();
	}

	private void pauseGame() {
		running = false;
		while (gameThread.isAlive()) {
			try {
				gameThread.join();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		long updateDurationMillis = 0;
		long sleepDurationMills = 0;
		while(running) {
			long beforeUpdateRender = System.nanoTime();

			long deltaMillis = updateDurationMillis + sleepDurationMills;
			updateAndRender(deltaMillis);

			updateDurationMillis = (System.nanoTime() - beforeUpdateRender)
					/1000000L;
			sleepDurationMills = Math.max(2, 17 - updateDurationMillis);
			try {
				Thread.sleep(sleepDurationMills);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void exit() {
		((Activity)this.getContext()).finish();
	}

	private void updateAndRender(long deltaMillis) {
		currentState.update(deltaMillis / 1000.0f);
		currentState.render(g);
		renderGameImage();
	}

	private void renderGameImage() {
		Canvas canvas = getHolder().lockCanvas();
		if (canvas != null) {
			canvas.getClipBounds(gameImageDst);
			canvas.drawBitmap(gameImage, gameImageSrc, gameImageDst, null);
			getHolder().unlockCanvasAndPost(canvas);
		}

	}

	public void onPause()	{
		if (currentState != null)
			currentState.onPause();
	}

	public void onResume()	{
		if (currentState != null)
			currentState.onResume();
	}

}

package com.teacher.framework.util;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

public class Painter {
	private Canvas canvas;		//画布对象
	private Paint paint;		//画笔对象
	private Rect srcRect;		//缩放处理时的保存原始矩形
	private Rect dstRect;		//缩放处理时的保存目标矩形
	private RectF dstRectF;		//float格式的目标矩形

	public Painter(Canvas canvas) {
		this.canvas = canvas;
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		paint.setSubpixelText(true);
		srcRect = new Rect();
		dstRect = new Rect();
		dstRectF = new RectF();
	}

	//设置颜色
	public void setColor(int color) {
		paint.setColor(color);
	}

	//设置字体
	public void setFont(Typeface typeface, float textSize) {
		paint.setTypeface(typeface);
		paint.setTextSize(textSize);
	}

	//显示字符串
	public void drawString(String str, int x, int y) {
		canvas.drawText(str, x, y, paint);
	}

	public float measureText(String str) {
		return paint.measureText(str);
	}

	//画实心矩形
	public void fillRect(int x, int y, int width, int height) {
		dstRect.set(x, y, x + width, y + height);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(dstRect, paint);
	}

	public void fillRoundRect(int x, int y, int width, int height, int radius) {
		paint.setStyle(Paint.Style.FILL);
		dstRectF.set(x, y, x + width, y + height);
		canvas.drawRoundRect(dstRectF, radius, radius, paint);
	}

	//画图不缩放
	public void drawImage(Bitmap bitmap, int x, int y) {
		canvas.drawBitmap(bitmap, x, y, paint);
	}

	//画图缩放
	public void drawImage(Bitmap bitmap, int x, int y, int width, int height) {
		srcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
		dstRect.set(x, y, x + width, y + height);
		canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
	}

	//画图并可以对原图进行裁剪
	public void drawImage(Bitmap bitmap, int srcx, int srcy, int dstx, int dsty,
						  int width, int height) {
		srcRect.set(srcx, srcy, srcx+width, srcy+height);
		dstRect.set(dstx, dsty, dstx + width, dsty + height);
		canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
	}

	//画实心椭圆
	public void fillOval(int x, int y, int width, int height) {
		paint.setStyle(Paint.Style.FILL);
		dstRectF.set(x, y, x + width, y + height);
		canvas.drawOval(dstRectF, paint);
	}

	public Canvas getCanvas() {
		return canvas;
	}

}

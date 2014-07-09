package sample.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CanvasView extends View {

	private int mColor;
	private Paint mPaint;
	private Paint mBitmapPaint;
	private Path mPath;
	private float xPos, yPos;
	private Canvas mCanvas;
	private Bitmap mBitmap;

	public CanvasView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mColor = Color.BLACK;

		mPaint = new Paint();
		mPaint.setColor(mColor);
		mPaint.setStrokeWidth(8);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStyle(Paint.Style.STROKE);

		mBitmapPaint = new Paint();

		mPath = new Path();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mBitmap != null) {
			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		}
		mPaint.setColor(mColor);
		canvas.drawPath(mPath, mPaint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mPath.moveTo(x, y);
			xPos = x;
			yPos = y;
			break;
		case MotionEvent.ACTION_MOVE:
			mPath.quadTo(xPos, yPos, (x + xPos) / 2, (y + yPos) / 2);
			xPos = x;
			yPos = y;
			break;
		case MotionEvent.ACTION_UP:
			mCanvas.drawPath(mPath, mPaint);
			mPath.reset();
			break;
		}
		invalidate();
		return true;
	}

	public void setColor(int color) {
		mColor = color;
	}

	public void clear() {
		mBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);
		mPath.reset();
		invalidate();
	}
	public Bitmap getBitmap(){
		Bitmap output = Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		BitmapDrawable bd = (BitmapDrawable)getBackground();
		canvas.drawBitmap(bd.getBitmap(),0,0,(Paint)null);
		canvas.drawBitmap(mBitmap, 0, 0,(Paint)null);
		return output;
	}

}
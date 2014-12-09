package it.keyorchestra.registrowebapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

@SuppressLint("DrawAllocation")
public class KeyOrchestraView extends View {

	Bitmap greenBall;
	float changingY;
	Typeface font;

	public KeyOrchestraView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		greenBall = BitmapFactory.decodeResource(getResources(),
				R.drawable.greenball);
		changingY = 0;
		try {
			font = Typeface.createFromAsset(context.getAssets(),
					"font/G-Unit.ttf");
//			font = Typeface.createFromFile("G-Unit.ttf");
		} catch (Exception e) {
			Log.e("Typefaces:", e.getMessage());
			font = null;
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		canvas.drawColor(Color.WHITE);

		Paint textPaint = new Paint();
		textPaint.setARGB(50, 255, 10, 50);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setTextSize(50);
		textPaint.setTypeface(font);

		canvas.drawText("keyOrchestra", canvas.getWidth() / 2, 200, textPaint);

		canvas.drawBitmap(greenBall, (getWidth() / 2), changingY, null);

		if (changingY <= canvas.getHeight()) {
			changingY += 10;
		} else {
			changingY = 0;
		}

		Rect middleRect = new Rect();
		middleRect.set(0, 400, canvas.getWidth(), 550);
		Paint ourBlue = new Paint();
		ourBlue.setColor(Color.BLUE);
		canvas.drawRect(middleRect, ourBlue);
		invalidate();
	}

}

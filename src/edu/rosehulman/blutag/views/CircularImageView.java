package edu.rosehulman.blutag.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircularImageView extends ImageView {
	private Paint maskPaint;
	private Paint imagePaint;
	
	public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		maskPaint = new Paint();
		maskPaint.setAntiAlias(true);
		maskPaint.setDither(true);
		maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		
		imagePaint = new Paint();
		imagePaint.setAntiAlias(true);
		imagePaint.setDither(true);
		imagePaint.setColor(Color.BLACK);
	}

	public CircularImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public CircularImageView(Context context) {
		this(context, null);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		Bitmap image = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
		
		Canvas canvas = new Canvas(image);
		
		canvas.drawCircle(bm.getWidth() / 2, bm.getHeight() / 2, Math.min(bm.getWidth(), bm.getHeight()) / 2, imagePaint);
		canvas.drawBitmap(bm, 0, 0, maskPaint);
		
		image.prepareToDraw();
		
		super.setImageBitmap(image);
	}
}

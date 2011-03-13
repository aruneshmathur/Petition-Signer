package com.android.petition;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.android.petition.db.Petition_Details_db;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Signature extends Activity implements OnClickListener {

	protected SignatureView view;

	public static final String SIGNATURE_FILE_PATH = "SIGNATURE_FILE_PATH";
	RelativeLayout llSign;
	String file_path;

	private Paint mPaint;
	private int light_gray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = getIntent().getExtras();
		file_path = getIntent().getStringExtra(SIGNATURE_FILE_PATH);

		view = new SignatureView(this);

		light_gray = getResources().getColor(R.color.light_gray);

		setContentView(R.layout.sign);
		llSign = (RelativeLayout) findViewById(R.id.ll_sign);
		llSign.addView(view, 0);
		llSign.setBackgroundColor(light_gray);

		((Button) findViewById(R.id.done)).setOnClickListener(this);
		((Button) findViewById(R.id.clear)).setOnClickListener(this);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(6);
		mPaint.setColor(0xFF000000);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.done:

			byte[] signature_byte = view.getBitmapBytes();
			Intent intent = new Intent();
			intent.putExtra("Signature", signature_byte);
			/*try {
				DataOutputStream oStream = new DataOutputStream(
						new FileOutputStream(file_path));
				oStream.write(signature_byte);
				oStream.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
*/
			setResult(RESULT_OK, intent);
			finish();

			// clear = false;
			// finish();

		case R.id.clear:
			llSign.removeView(view);
			view = new SignatureView(Signature.this);
			llSign.addView(view, 0);
			llSign.setBackgroundColor(light_gray);
			// clear = true;
			break;

		default:
			break;
		}

	}

	public class SignatureView extends View {

		private Bitmap mBitmap;
		private Canvas mCanvas;
		private Path mPath;
		private Paint mBitmapPaint;

		public SignatureView(Context c) {

			super(c);

			Display d = getWindowManager().getDefaultDisplay();

			mBitmap = Bitmap.createBitmap(d.getWidth(), d.getHeight(),
					Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);

		}

		public SignatureView(Context c, byte[] bitmapData) {

			super(c);

			mBitmap = BitmapFactory.decodeByteArray(bitmapData, 0,
					bitmapData.length).copy(Config.ARGB_8888, true);
			mCanvas = new Canvas(mBitmap);
			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);

		}

		public byte[] getBitmapBytes() {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			mBitmap.compress(CompressFormat.PNG, 100, out);
			return out.toByteArray();
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(0x00000000);
			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
			canvas.drawPath(mPath, mPaint);
		}

		private float mX, mY;
		private static final float TOUCH_TOLERANCE = 4;

		private void touch_start(float x, float y) {
			mPath.reset();
			mPath.moveTo(x, y);
			mX = x;
			mY = y;
		}

		private void touch_move(float x, float y) {
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			}
		}

		private void touch_up() {
			mPath.lineTo(mX, mY);
			mCanvas.drawPath(mPath, mPaint);
			mPath.reset();
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				break;
			}
			return true;
		}
	}
}
package com.davidballa.galleryapp;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewImage extends Activity {
	
	private ImageView bigImageView;
	private TextView textView;
	private SortableImage item;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* set activity to full screen */
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_view_image);
		
		String f = getIntent().getStringExtra("img");
		String[] data = f.split(";");
		bigImageView = (ImageView) findViewById(R.id.bigImageView);
		bigImageView.setImageURI(Uri.parse(data[0]));
		textView = (TextView) findViewById(R.id.imagePathText);
		textView.setText("Location: " + data[0]);
		textView = (TextView) findViewById(R.id.imageDateText);
		textView.setText("Last mod. date: " + data[1]);
		
		item = new SortableImage(data[0]);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_image_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.action_rotate) {
			//rotateImageToPlace(); //this method needs to be rethinked
			return true;
		}
		if (id == R.id.action_share) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void rotateImageToPlace() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(item.getPath(), options);
		
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(item.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

		Bitmap bmRotated = rotateBitmap(bitmap, orientation);
		
		bigImageView.setImageBitmap(bmRotated);
	}

	/**
	 * Rotating image
	 * @param bitmap
	 * @param orientation
	 * @return
	 */
	private Bitmap rotateBitmap(Bitmap bmp, int orientation) {

		Matrix matrix = new Matrix();
		switch (orientation) {
		case ExifInterface.ORIENTATION_NORMAL:
			return bmp;
		case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
			matrix.setScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			matrix.setRotate(180);
			break;
		case ExifInterface.ORIENTATION_FLIP_VERTICAL:
			matrix.setRotate(180);
			matrix.postScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_TRANSPOSE:
			matrix.setRotate(90);
			matrix.postScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_ROTATE_90:
			matrix.setRotate(90);
			break;
		case ExifInterface.ORIENTATION_TRANSVERSE:
			matrix.setRotate(-90);
			matrix.postScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			matrix.setRotate(-90);
			break;
		default:
			return bmp;
		}
		try {
			Bitmap bmRotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
			bmp.recycle();
			return bmRotated;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

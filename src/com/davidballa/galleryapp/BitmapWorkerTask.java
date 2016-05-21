package com.davidballa.galleryapp;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
	private final WeakReference<ImageView> imageViewReference;
	int data = 0;
	private SortableImage item;

	public BitmapWorkerTask(ImageView imageView, MainActivity activity, SortableImage imageItem) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		imageViewReference = new WeakReference<ImageView>(imageView);
		this.item = imageItem;
	}

	// Decode image in background.
	@Override
	protected Bitmap doInBackground(Integer... params) {
		data = params[0];
		Bitmap bitmap = null;

		/* Attempt to read the image */
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		bitmap = BitmapFactory.decodeFile(item.getPath(), options);

		Bitmap bmRotated = null;

		if (bitmap != null) {

			ExifInterface exif = null;
			try {
				exif = new ExifInterface(item.getPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

			bmRotated = rotateBitmap(bitmap, orientation);

			/* Crop */
			if (bmRotated.getWidth() >= bmRotated.getHeight()) {
				bmRotated = Bitmap.createBitmap(bmRotated, bmRotated.getWidth() / 2 - bmRotated.getHeight() / 2, 0,
						bmRotated.getHeight(), bmRotated.getHeight());
			} else {
				bmRotated = Bitmap.createBitmap(bmRotated, 0, bmRotated.getHeight() / 2 - bmRotated.getWidth() / 2,
						bmRotated.getWidth(), bmRotated.getWidth());
			}

		}
		return bmRotated;
	}

	// Once complete, see if ImageView is still around and set bitmap.
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}

		if (imageViewReference != null && bitmap != null) {
			final ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				imageView.setImageBitmap(bitmap);
			}
		}

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

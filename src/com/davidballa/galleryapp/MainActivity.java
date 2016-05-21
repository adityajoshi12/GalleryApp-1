package com.davidballa.galleryapp;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainActivity extends Activity {

	private GridView gridView;
	private ArrayList<SortableImage> picturesList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* set activity to full screen */
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);

		/* list just the DCIM folder */
		picturesList = readImages(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));

		/* Sort list by date */
		Collections.sort(picturesList, new DateComparator());

		gridView = (GridView) findViewById(R.id.gridView);
		gridView.setAdapter(new GridAdapter(picturesList, this));

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(new Intent(getApplicationContext(), ViewImage.class).putExtra("img",
						picturesList.get(position).getPath() + ";" + picturesList.get(position).getLastModDate()));
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
		return super.onOptionsItemSelected(item);
	}

	private ArrayList<SortableImage> readImages(File root) {
		ArrayList<SortableImage> temp = new ArrayList<SortableImage>();
		SortableImage tempImage;
		File[] files = root.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				temp.addAll(readImages(files[i]));
			} else {
				if (files[i].getName().endsWith(".jpeg") || files[i].getName().endsWith(".jpg")
						|| files[i].getName().endsWith(".png") || files[i].getName().endsWith(".gif")) {
					tempImage = new SortableImage(files[i].getAbsolutePath(), new Date(files[i].lastModified()));
					temp.add(tempImage);
				}
			}
		}
		return temp;
	}

}

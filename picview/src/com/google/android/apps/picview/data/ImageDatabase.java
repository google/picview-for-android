/*
 * Copyright 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.android.apps.picview.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;

import com.google.android.apps.picview.PicView;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

/**
 * A data base that stores image data.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class ImageDatabase {
	private static final String TAG = ImageDatabase.class.getSimpleName();
	private static final String DATABASE_NAME = "photos_cache.db";
	private static final String TABLE_NAME = "photos";

	private static final String COLUMN_URL = "url";
	private static final String COLUMN_MODIFIED = "modified";
	private static final String COLUMN_BITMAP = "bitmap";
	private static final String[] ALL_COLUMNS = { COLUMN_URL, COLUMN_MODIFIED,
			COLUMN_BITMAP };

	private static ImageDatabase imageDb;

	private SQLiteDatabase db;

	protected ImageDatabase(SQLiteDatabase db) {
		this.db = db;
	}

	/**
	 * Returns the singleton instance of the {@link ImageDatabase}.
	 */
	public static ImageDatabase get() {
		if (imageDb == null) {
			imageDb = new ImageDatabase(getUsableDataBase());
		}
		return imageDb;
	}

	/**
	 * Queries for a photo with the given URL.
	 */
	public PhotoCursor query(String url) {
		return new PhotoCursor(db.query(true, TABLE_NAME, ALL_COLUMNS,
				COLUMN_URL + " = '" + url + "'", null, null, null, null, null),
				COLUMN_BITMAP);
	}

	/**
	 * Puts an image into the database.
	 * 
	 * @param url
	 *            The URL of the image.
	 * @param modified
	 *            The version key of the image.
	 * @param image
	 *            The image to store.
	 * @return The row
	 */
	public long put(URL url, String modified, Bitmap image) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_URL, url.toString());
		values.put(COLUMN_MODIFIED, modified);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
		values.put(COLUMN_BITMAP, outputStream.toByteArray());

		return db.replace(TABLE_NAME, COLUMN_BITMAP, values);
	}

	/**
	 * Whether an image with the given URL exists.
	 */
	public boolean exists(URL url) {
		PhotoCursor c = query(url.toString());
		if (c == null) {
			return false;
		}

		boolean exists = c.moveToFirst();
		c.close();
		return exists;
	}

	/**
	 * Returns whether this database is ready to be used.
	 */
	public boolean isReady() {
		return db != null;
	}

	private static SQLiteDatabase getUsableDataBase() {
		File dbFile = getPathToDb();

		File fileDirectory = new File(dbFile.getParent());
		if (!fileDirectory.exists()) {
			// Make sure the path for the file exists, before creating the
			// database.
			fileDirectory.mkdirs();
		}
		Log.d(TAG, "DB Path: " + dbFile.getAbsolutePath());
		boolean initDb = !dbFile.exists();
		try {
			SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(dbFile,
					null);

			if (initDb) {
				result.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COLUMN_URL
						+ " TEXT PRIMARY KEY," + COLUMN_MODIFIED + " TEXT,"
						+ COLUMN_BITMAP + " BLOB);");
			}

			return result;
		} catch (SQLiteException ex) {
			Log.w(TAG, "Could not open or image database.");
			return null;
		}
	}

	private static File getPathToDb() {
		String sdCardPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		return new File(sdCardPath + File.separator + "data" + File.separator
				+ PicView.appNamePath + File.separator + DATABASE_NAME);
	}

}

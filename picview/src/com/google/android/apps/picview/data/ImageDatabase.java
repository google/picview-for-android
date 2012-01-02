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
import java.net.URL;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

/**
 * A data base that stores image data.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class ImageDatabase extends AbstractPicViewDatabase {
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
      imageDb = new ImageDatabase(getUsableDataBase(DATABASE_NAME,
          "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_URL
              + " TEXT PRIMARY KEY," + COLUMN_MODIFIED + " TEXT,"
              + COLUMN_BITMAP + " BLOB);"));
    }
    return imageDb;
  }

  /**
   * Queries for a photo with the given URL.
   */
  public PhotoCursor query(String url) {
    return new PhotoCursor(db.query(true, TABLE_NAME, ALL_COLUMNS, COLUMN_URL
        + " = '" + url + "'", null, null, null, null, null), COLUMN_BITMAP);
  }

  /**
   * Puts an image into the database.
   * 
   * @param url
   *          The URL of the image.
   * @param modified
   *          The version key of the image.
   * @param image
   *          The image to store.
   * @return The row.
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

}

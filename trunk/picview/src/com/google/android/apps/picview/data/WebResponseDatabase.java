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

import java.io.File;
import java.net.URL;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import com.google.android.apps.picview.PicView;

/**
 * A data base that stores responses from HTTP requests, along with their last
 * modified date.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class WebResponseDatabase {
  private static final String TAG = WebResponseDatabase.class.getSimpleName();
  private static final String DATABASE_NAME = "request_cache.db";
  private static final String TABLE_NAME = "requests";

  private static final String COLUMN_URL = "url";
  private static final String COLUMN_MODIFIED = "modified";
  private static final String COLUMN_RESPONSE = "response";
  private static final String[] ALL_COLUMNS = { COLUMN_URL, COLUMN_MODIFIED,
      COLUMN_RESPONSE };

  private static WebResponseDatabase responseDb;

  private SQLiteDatabase db;

  protected WebResponseDatabase(SQLiteDatabase db) {
    this.db = db;
  }

  /**
   * Returns the singleton instance of the {@link ImageDatabase}.
   */
  public static WebResponseDatabase get() {
    if (responseDb == null) {
      responseDb = new WebResponseDatabase(getUsableDataBase());
    }
    return responseDb;
  }

  /**
   * Queries for a photo with the given URL.
   */
  public WebResponseCursor query(String url) {
    return new WebResponseCursor(db.query(true, TABLE_NAME, ALL_COLUMNS,
        COLUMN_URL + " = '" + url + "'", null, null, null, null, null),
        COLUMN_MODIFIED, COLUMN_RESPONSE);
  }

  /**
   * Puts a response into the database.
   * 
   * @param url
   *          the request URL
   * @param modified
   *          the version key of the response, usually a date string
   * @param response
   *          the web response of the request to store
   * @return the row
   */
  public long put(URL url, String modified, String response) {
    ContentValues values = new ContentValues();
    values.put(COLUMN_URL, url.toString());
    values.put(COLUMN_MODIFIED, modified);
    values.put(COLUMN_RESPONSE, response);

    return db.replace(TABLE_NAME, COLUMN_RESPONSE, values);
  }

  /**
   * Whether a response with the given URL exists.
   */
  public boolean exists(URL url) {
    WebResponseCursor c = query(url.toString());
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
      SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

      if (initDb) {
        result.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COLUMN_URL
            + " TEXT PRIMARY KEY," + COLUMN_MODIFIED + " TEXT,"
            + COLUMN_RESPONSE + " TEXT);");
      }

      return result;
    } catch (SQLiteException ex) {
      Log.w(TAG, "Could not open or create web response database.");
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

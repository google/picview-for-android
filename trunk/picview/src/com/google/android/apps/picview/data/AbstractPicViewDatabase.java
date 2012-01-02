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

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import com.google.android.apps.picview.PicViewConfig;

/**
 * Abstract super-class of all PicView databases.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class AbstractPicViewDatabase {
  private static final String TAG = AbstractPicViewDatabase.class
      .getSimpleName();

  /**
   * Returns a usable database with the given name. If a database with this name
   * already exists, it is returned. Otherwise created with the given SQL create
   * query.
   */
  protected static SQLiteDatabase getUsableDataBase(String dbName,
      String sqlCreateQuery) {
    File dbFile = getPathToDb(dbName);

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
        result.execSQL(sqlCreateQuery);
      }

      return result;
    } catch (SQLiteException ex) {
      Log.w(TAG, "Could not open or image database.");
      return null;
    }
  }

  /**
   * Returns a file for the data base with the given name.
   */
  protected static File getPathToDb(String dbName) {
    String sdCardPath = Environment.getExternalStorageDirectory()
        .getAbsolutePath();
    return new File(sdCardPath + File.separator + "data" + File.separator
        + PicViewConfig.APP_NAME_PATH + File.separator + dbName);
  }
}

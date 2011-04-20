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

import java.net.URL;

import android.database.sqlite.SQLiteDiskIOException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

/**
 * A cache which stores image data on the device storage. Uses a
 * {@link ImageDatabase} as the backend.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class FileSystemImageCache {
  private static final String TAG = FileSystemImageCache.class.getSimpleName();

  private ImageDatabase imageDb;

  public FileSystemImageCache() {
    imageDb = ImageDatabase.get();
  }

  /**
   * Gets the photo with the given URL from the database.
   * 
   * @param url
   *          The {@link URL} of the photo to get.
   * @return The {@link Bitmap} object or <code>null</code>, if the database
   *         does not contain a Bitmap with the given URL.
   */
  public Bitmap get(URL url) {
    if (!imageDb.isReady()) {
      return null;
    }
    PhotoCursor c = imageDb.query(url.toString());

    if (c == null) {
      return null;
    }

    if (!c.moveToFirst()) {
      c.close();
      return null;
    }

    Log.i(TAG, "Reading photo from database");
    return c.getBitmapAndClose();
  }

  /**
   * Stores the image with the given URL and the modified/version String.
   * 
   * @param url
   *          The URL of the photo to be put.
   * @param modified
   *          The modified/version string.
   * @param image
   *          The image data.
   */
  private synchronized boolean put(URL url, String modified, Bitmap image) {
    if (!imageDb.isReady()) {
      return false;
    }
    Log.i(TAG, "Attempting to put " + url.toString());
    if (imageDb.exists(url)) {
      Log.i(TAG, "ALREADY EXISTS!");
      return false;
    }

    Log.i(TAG, "Putting photo into DB.");
    try {
      return imageDb.put(url, modified, image) != -1;
    } catch (SQLiteDiskIOException ex) {
      Log.w(TAG, "Unable to put photo in DB, disk full or unavailable.");
      return false;
    }
  }

  /**
   * Same as {@link #put(URL, String, Bitmap)} but returns immediately. The
   * actual putting is done asynchronously.
   * 
   * @param url
   *          The URL of the photo to be put.
   * @param modified
   *          The modified/version string.
   * @param photo
   *          The photo data.
   */
  public void asyncPut(final URL url, final String modified, final Bitmap photo) {
    if (!imageDb.isReady()) {
      return;
    }

    AsyncTask<Void, Integer, Void> task = new AsyncTask<Void, Integer, Void>() {
      @Override
      protected Void doInBackground(Void... params) {
        put(url, modified, photo);
        return null;
      }
    };
    task.execute();
  }
}

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

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.apps.picview.data.WebResponseCursor.CachedWebResponse;

/**
 * A cache that stores web responses on the device storage. It uses a
 * {@link WebResponseDatabase} as the backend.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class FileSystemWebResponseCache {
  private static final String TAG = FileSystemWebResponseCache.class
      .getSimpleName();

  private WebResponseDatabase responseDb;

  public FileSystemWebResponseCache() {
    responseDb = WebResponseDatabase.get();
  }

  /**
   * Gets the response for the request with the given URL from the database.
   * 
   * @param url
   *          the request {@link URL} of the response to get
   * @return the response or <code>null</code>, if the database does not contain
   *         a response for the given URL
   */
  public CachedWebResponse get(URL url) {
    if (!responseDb.isReady()) {
      return null;
    }

    WebResponseCursor c = responseDb.query(url.toString());

    if (c == null) {
      return null;
    }
    if (!c.moveToFirst()) {
      c.close();
      return null;
    }

    Log.i(TAG, "Trying to read web response from database");
    return c.getResponseAndClose();
  }

  /**
   * Stores the response from the given URL and the modified/version String.
   * 
   * @param url
   *          the URL of the request
   * @param modified
   *          the modified/version string
   * @param response
   *          the response contents
   */
  private synchronized boolean put(URL url, String modified, String response) {
    if (!responseDb.isReady()) {
      return false;
    }

    Log.i(TAG, "Putting response into DB.");
    return responseDb.put(url, modified, response) != -1;
  }

  /**
   * Same as {@link #put(URL, String, String)}, but returns immediately. The
   * actual put is done asynchronously.
   * 
   * @param url
   *          the URL of the request
   * @param modified
   *          the modified/version string
   * @param response
   *          the response contents
   */
  public void asyncPut(final URL url, final String modified,
      final String response) {
    if (!responseDb.isReady()) {
      return;
    }

    AsyncTask<Void, Integer, Void> task = new AsyncTask<Void, Integer, Void>() {
      @Override
      protected Void doInBackground(Void... params) {
        put(url, modified, response);
        return null;
      }
    };
    task.execute();
  }
}

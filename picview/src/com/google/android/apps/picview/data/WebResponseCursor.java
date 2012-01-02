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

import android.database.Cursor;
import android.util.Log;

/**
 * Keeps a cursor that is used in the web response database.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class WebResponseCursor {

  /**
   * A response and its modified timestamp.
   */
  public static class CachedWebResponse {
    public final String modified;
    public final String response;

    public CachedWebResponse(String modified, String response) {
      this.modified = modified;
      this.response = response;
    }
  }

  private static final String TAG = WebResponseCursor.class.getSimpleName();
  private final Cursor cursor;
  private final String columnModified;
  private final String columnResponse;

  public WebResponseCursor(Cursor cursor, String columnModified,
      String columnResponse) {
    this.cursor = cursor;
    this.columnModified = columnModified;
    this.columnResponse = columnResponse;
  }

  public boolean moveToFirst() {
    return cursor != null && cursor.moveToFirst();
  }

  public void close() {
    if (cursor != null) {
      cursor.close();
    }
  }

  public CachedWebResponse getResponseAndClose() {
    if (cursor.getCount() <= 0) {
      Log.w(TAG, "Could not find web response in database.");
      return null;
    }
    try {
      String modified = cursor.getString(cursor.getColumnIndex(columnModified));
      String response = cursor.getString(cursor.getColumnIndex(columnResponse));
      return new CachedWebResponse(modified, response);
    } catch (IllegalStateException ex) {
      // TODO(haeberling): This sometimes happens, e.g. with Tray's portfolio,
      // when the reply is really big.
      ex.printStackTrace();
    } finally {
      cursor.close();
    }
    return null;
  }
}

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Keeps a cursor that is used in the image database.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class PhotoCursor {
  private final Cursor cursor;
  private final String columnBitmap;

  public PhotoCursor(Cursor cursor, String columnBitmap) {
    this.cursor = cursor;
    this.columnBitmap = columnBitmap;
  }

  public boolean moveToFirst() {
    return cursor != null && cursor.moveToFirst();
  }

  public void close() {
    if (cursor != null) {
      cursor.close();
    }
  }

  public Bitmap getBitmapAndClose() {
    byte[] data = cursor.getBlob(cursor.getColumnIndex(columnBitmap));
    close();
    return BitmapFactory.decodeByteArray(data, 0, data.length);
  }
}

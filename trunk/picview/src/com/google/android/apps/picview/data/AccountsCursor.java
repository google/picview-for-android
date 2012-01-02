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

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

/**
 * Keeps a cursor that is used in the accounts database.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class AccountsCursor {
  private final Cursor cursor;

  public AccountsCursor(Cursor cursor) {
    this.cursor = cursor;
  }

  /**
   * Moves to the first items, if the cursor is present.
   * 
   * @return Whether the move succeeded.
   */
  public boolean moveToFirst() {
    return cursor != null && cursor.moveToFirst();
  }

  /**
   * Closes the cursors, if present.
   */
  public void close() {
    if (cursor != null) {
      cursor.close();
    }
  }

  /**
   * Returns all accounts and closes the cursor.
   */
  public List<Account> getAllAndClose() {
    List<Account> result = new ArrayList<Account>();
    if (cursor == null) {
      return result;
    }
    if (!moveToFirst()) {
      return result;
    }

    int columnIndexPosition = cursor
        .getColumnIndex(AccountsDatabase.COLUMN_POSITION);
    int columnIndexType = cursor.getColumnIndex(AccountsDatabase.COLUMN_TYPE);
    int columnIndexId = cursor
        .getColumnIndex(AccountsDatabase.COLUMN_ACCOUNT_ID);
    int columnIndexName = cursor
        .getColumnIndex(AccountsDatabase.COLUMN_ACCOUNT_NAME);

    do {
      int position = cursor.getInt(columnIndexPosition);
      int type = cursor.getInt(columnIndexType);
      String id = cursor.getString(columnIndexId);
      String name = cursor.getString(columnIndexName);

      result.add(new Account(position, type, id, name));
    } while (cursor.moveToNext());

    return result;
  }

  /**
   * Return the highest position. If there is no entry in this cursor, -1 will
   * be returned.
   */
  public int getHighestPosAndClose() {
    int highest = -1;
    int columnIndexPosition = cursor
        .getColumnIndex(AccountsDatabase.COLUMN_POSITION);
    if (cursor != null && cursor.getCount() > 0) {
      moveToFirst();
      do {
        int position = cursor.getInt(columnIndexPosition);
        if (position > highest) {
          highest = position;
        }
      } while (cursor.moveToNext());
    }
    cursor.close();
    return highest;
  }
}

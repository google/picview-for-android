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

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * A database that stores accounts being followed by the user.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class AccountsDatabase extends AbstractPicViewDatabase {
  private static final String TAG = AccountsDatabase.class.getSimpleName();
  private static final String DATABASE_NAME = "accounts_followed.db";
  private static final String TABLE_NAME = "accounts";

  /** The (visual) position in the list. */
  static final String COLUMN_POSITION = "position";
  static final String COLUMN_TYPE = "type";
  static final String COLUMN_ACCOUNT_NAME = "account_name";
  static final String COLUMN_ACCOUNT_ID = "account_id";

  private static AccountsDatabase accountsDb;

  private SQLiteDatabase db;

  protected AccountsDatabase(SQLiteDatabase db) {
    this.db = db;
  }

  /**
   * Returns the singleton instance of the {@link ImageDatabase}.
   */
  public static AccountsDatabase get() {
    if (accountsDb == null) {
      accountsDb = new AccountsDatabase(getUsableDataBase(DATABASE_NAME,
          "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_POSITION
              + " INTEGER PRIMARY KEY," + COLUMN_TYPE + " INTEGER,"
              + COLUMN_ACCOUNT_ID + " TEXT," + COLUMN_ACCOUNT_NAME + " TEXT);"));
    }
    return accountsDb;
  }

  /**
   * Returns whether this database is ready to be used.
   */
  public synchronized boolean isReady() {
    return db != null;
  }

  /**
   * Puts an account into the database or replaces the one with the same
   * <code>position</code>.
   * 
   * @param position
   *          The position in of the account in the list. If <code>-1</code> is
   *          given, the position will be <code>currentMaxPosition + 1</code>.
   * @param accountType
   *          The account type.
   * @param accountId
   *          The ID of the account.
   * @param accountName
   *          The name of the account to be displayed.
   * @return The row.
   */
  public synchronized long put(int position, int accountType, String accountId,
      String accountName) {
    // Determine last position + 1.
    if (position == -1) {
      position = queryAll().getHighestPosAndClose() + 1;
    }
    Log.i(TAG, "Putting account into database as position: " + position);

    // If the account name wasn't given, we set the ID as the name. We will
    // later sort accounts in the UI by name, so it make sense that every entry
    // has valid name.
    if (accountName == null || accountName.isEmpty()) {
      Log.i(TAG, "Account name set to account ID as non was given.");
      accountName = accountId;
    }

    ContentValues values = new ContentValues();
    values.put(COLUMN_POSITION, position);
    values.put(COLUMN_TYPE, accountType);
    values.put(COLUMN_ACCOUNT_ID, accountId);
    values.put(COLUMN_ACCOUNT_NAME, accountName);
    return db.replace(TABLE_NAME, COLUMN_ACCOUNT_NAME, values);
  }

  /**
   * Removes the account at the given position.
   */
  public synchronized void remove(int position) {
    db.delete(TABLE_NAME, COLUMN_POSITION + "=?",
        new String[] { String.valueOf(position) });
  }

  /**
   * Returns all entries, ordered by name.
   */
  public synchronized AccountsCursor queryAll() {
    return new AccountsCursor(db.rawQuery("SELECT * FROM " + TABLE_NAME
        + " ORDER BY " + COLUMN_ACCOUNT_NAME + " COLLATE NOCASE ASC", null));
  }
}

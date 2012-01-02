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

package com.google.android.apps.picview.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.google.android.apps.picview.Callback;
import com.google.android.apps.picview.R;
import com.google.android.apps.picview.adapter.AccountsAdapter;
import com.google.android.apps.picview.data.Account;
import com.google.android.apps.picview.data.AccountsDatabase;

/**
 * The starting activity which lets the user manage the photo accounts.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class AccountsActivity extends Activity {
  private static final int MENU_ADD_ACCOUNT = 0;
  private static final int MENU_PREFERENCES = 1;
  private static final int MENU_ABOUT = 2;

  // The order of these must match the array "account_actions" in strings.xml.
  private static final int CONTEXT_MENU_EDIT = 0;
  private static final int CONTEXT_MENU_DELETE = 1;

  private AccountsAdapter adapter;
  private ListView mainList;
  private LayoutInflater inflater;
  private AccountsDatabase accountsDb = AccountsDatabase.get();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.accounts);

    Callback<String> accountSelectedCallback = new Callback<String>() {
      @Override
      public void callback(String accountId) {
        Intent intent = new Intent(AccountsActivity.this,
            AlbumListActivity.class);
        intent.putExtra("accountId", accountId);
        AccountsActivity.this.startActivity(intent);
      }
    };

    mainList = (ListView) findViewById(R.id.accounts_list);
    inflater = LayoutInflater.from(this);
    adapter = new AccountsAdapter(accountsDb, accountSelectedCallback, inflater);
    mainList.setAdapter(adapter);
    registerForContextMenu(mainList);
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    if (v.getId() != R.id.accounts_list) {
      return;
    }

    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
    Account account = (Account) adapter.getItem(info.position);
    menu.setHeaderTitle(account.toString());

    String[] menuItems = getResources().getStringArray(R.array.account_actions);
    for (int i = 0; i < menuItems.length; i++) {
      menu.add(Menu.NONE, i, i, menuItems[i]);
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
        .getMenuInfo();
    Account account = (Account) adapter.getItem(menuInfo.position);

    switch (item.getItemId()) {
    case CONTEXT_MENU_EDIT:
      return true;
    case CONTEXT_MENU_DELETE:
      showAreYouSureDialog(account.position);
      return true;
    }
    return super.onContextItemSelected(item);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case MENU_ADD_ACCOUNT:
      showAddAccountDialog();
      return true;
    case MENU_PREFERENCES:
      Intent intent = new Intent(this, PicViewPreferencesActivity.class);
      startActivity(intent);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, MENU_ADD_ACCOUNT, 0, R.string.add_account).setIcon(
        android.R.drawable.ic_menu_add);
    menu.add(0, MENU_PREFERENCES, 1, R.string.preferences).setIcon(
        android.R.drawable.ic_menu_manage);
    menu.add(0, MENU_ABOUT, 2, R.string.about).setIcon(
        android.R.drawable.ic_menu_info_details);
    return true;
  }

  /**
   * Shows the dialog for adding a new account.
   */
  private void showAddAccountDialog() {
    AddEditAccountFragment.Callback callback = new AddEditAccountFragment.Callback() {
      @Override
      public void onAddAccount(int type, String id, String name) {
        accountsDb.put(-1, type, id, name);
        adapter.notifyDataSetChanged();
      }
    };
    AddEditAccountFragment dialog = new AddEditAccountFragment(callback);
    dialog.show(getFragmentManager(), "accountAddDialog");
  }

  private void showAreYouSureDialog(final int accountPosition) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(R.string.are_you_sure_delete);
    builder.setNegativeButton(R.string.no, null);
    builder.setPositiveButton(R.string.yes, new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        accountsDb.remove(accountPosition);
        adapter.notifyDataSetChanged();
      }
    });
    builder.create().show();
  }
}

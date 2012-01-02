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

package com.google.android.apps.picview.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.picview.Callback;
import com.google.android.apps.picview.R;
import com.google.android.apps.picview.data.Account;
import com.google.android.apps.picview.data.AccountsDatabase;
import com.google.android.apps.picview.data.AccountsUtil;

/**
 * The controller for the accounts list.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class AccountsAdapter extends BaseAdapter {
  private final AccountsDatabase db;
  private final Callback<String> callback;
  private final LayoutInflater inflater;
  private List<Account> accounts;

  /**
   * Instatiates the accounts adapter.
   * 
   * @param db
   *          The database which stores the accounts.
   * @param callback
   *          The callback to be called when a row is clicked.
   * @param inflater
   *          The inflater used to inflate the layouts for the rows.
   */
  public AccountsAdapter(AccountsDatabase db, Callback<String> callback,
      LayoutInflater inflater) {
    this.db = db;
    this.callback = callback;
    this.inflater = inflater;
    refreshData();
  }

  @Override
  public int getCount() {
    return accounts.size();
  }

  @Override
  public Object getItem(int position) {
    return accounts.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.account_entry, null);
    }
    final Account account = accounts.get(position);
    ((ImageView) convertView.findViewById(R.id.service_logo))
        .setImageResource(AccountsUtil.getAccountLogoResource(account.type));
    ((TextView) convertView.findViewById(R.id.account_name))
        .setText(account.name);
    ((TextView) convertView.findViewById(R.id.account_id)).setText(account.id);

    convertView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        callback.callback(account.id);
      }
    });
    convertView.setLongClickable(true);

    return convertView;
  }

  @Override
  public void notifyDataSetChanged() {
    refreshData();
    super.notifyDataSetChanged();
  }

  private void refreshData() {
    this.accounts = db.queryAll().getAllAndClose();
  }
}

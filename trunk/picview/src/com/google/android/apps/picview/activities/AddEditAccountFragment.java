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

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.apps.picview.R;

/**
 * A dialog for adding or editing account information.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class AddEditAccountFragment extends DialogFragment {
  public static interface Callback {
    /**
     * A new account is to be created.
     * 
     * @param type
     *          The account type.
     * @param id
     *          The user ID for that account.
     * @param name
     *          The name for the account.
     */
    public void onAddAccount(int type, String id, String name);
  }

  private final Callback callback;

  /**
   * Instantiates the dialog.
   * 
   * @param callback
   *          The callback will be called when the user clicks either the OK or
   *          cancel button.
   */
  public AddEditAccountFragment(Callback callback) {
    this.callback = callback;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Set dialog title.
    getDialog().setTitle(R.string.account_add_title);

    // Inflate the view we're using for the dialog.
    View view = inflater.inflate(R.layout.add_edit_account, container, false);

    // Add the adapter for the items to the account type drop-down.
    Spinner spinner = (Spinner) view.findViewById(R.id.account_type);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this
        .getDialog().getContext(), R.array.account_type_array,
        android.R.layout.simple_spinner_item);
    adapter
        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);

    // Add listeners to buttons and other UI elements if needed.
    addListeners(view);

    return view;
  }

  /**
   * Adds the UI listeners to the view.
   */
  private void addListeners(View view) {
    final Spinner accountType = (Spinner) view.findViewById(R.id.account_type);
    final EditText accountId = (EditText) view.findViewById(R.id.account_id);
    final EditText accountName = (EditText) view
        .findViewById(R.id.account_name);

    Button okButton = (Button) view.findViewById(R.id.ok);
    okButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        int type = accountType.getSelectedItemPosition();
        String id = accountId.getText().toString();
        String name = accountName.getText().toString();
        callback.onAddAccount(type, id, name);
        dismiss();
      }
    });

    Button cancelButton = (Button) view.findViewById(R.id.cancel);
    cancelButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        AddEditAccountFragment.this.dismiss();
      }
    });
  }
}

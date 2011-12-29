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

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.apps.picview.R;

/**
 * The preferences activity shows common preferences that can be configured by
 * the user.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class PicViewPreferencesActivity extends PreferenceActivity {
  int oldCacheValue;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    addPreferencesFromResource(R.xml.preferences);
  }

  @Override
  protected void onResume() {
    super.onResume();
    oldCacheValue = getCurrentCacheValue();
  }

  @Override
  protected void onPause() {
    super.onPause();
    int currentCacheValue = getCurrentCacheValue();
    if (currentCacheValue != oldCacheValue) {
      Toast.makeText(this,
          "Changing cache: " + (currentCacheValue - oldCacheValue) + " MB",
          Toast.LENGTH_SHORT).show();
    }
  }

  private int getCurrentCacheValue() {
    return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this)
        .getString("cacheSize", "400"));
  }
}

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

import android.util.DisplayMetrics;
import android.view.LayoutInflater;

import com.google.android.apps.picview.data.Album;
import com.google.android.apps.picview.request.CachedImageFetcher;
import com.google.android.apps.picview.ui.ThumbnailItem;

/**
 * The controller for the albums list.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class AlbumsAdapter extends MultiColumnImageAdapter<Album> {

  public AlbumsAdapter(List<ThumbnailItem<Album>> dataItems,
      LayoutInflater inflater, ThumbnailClickListener<Album> listener,
      CachedImageFetcher cachedImageFetcher, DisplayMetrics displayMetrics) {
    super(dataItems, inflater, listener, cachedImageFetcher, displayMetrics);
  }
}

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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.android.apps.picview.R;
import com.google.android.apps.picview.adapter.MultiColumnImageAdapter.ThumbnailClickListener;
import com.google.android.apps.picview.adapter.PhotosAdapter;
import com.google.android.apps.picview.data.FileSystemImageCache;
import com.google.android.apps.picview.data.Photo;
import com.google.android.apps.picview.request.CachedImageFetcher;
import com.google.android.apps.picview.ui.ThumbnailItem;

/**
 * An activity that shows a list of photos. It is used for the album list as
 * well as the list of photos within an album.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class PhotoListActivity extends Activity {
  private static final String TAG = PhotoListActivity.class.getSimpleName();
  private ListView mainList;
  private LayoutInflater inflater;

  private String albumName;
  private List<Photo> photos;
  private CachedImageFetcher cachedImageFetcher;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    int viewId = getIntent().getIntExtra("layout", -1);
    setContentView(viewId);
    mainList = (ListView) findViewById(R.id.photolist);
    inflater = LayoutInflater.from(this);
    albumName = getIntent().getExtras().getString("albumName");
    photos = getIntent().getExtras().getParcelableArrayList("photos");
    cachedImageFetcher = new CachedImageFetcher(new FileSystemImageCache());
    initCurrentConfiguration();
    loadPhotos();
  }

  private void initCurrentConfiguration() {
    CachedImageFetcher savedImageFecther = (CachedImageFetcher) getLastNonConfigurationInstance();
    if (savedImageFecther != null) {
      cachedImageFetcher = savedImageFecther;
    }
  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    return cachedImageFetcher;
  }

  private void loadPhotos() {
    if (photos == null) {
      Log.d(TAG, "No photos!");
      return;
    }

    ThumbnailClickListener<Photo> clickListener = new ThumbnailClickListener<Photo>() {
      @Override
      public void thumbnailClicked(Photo photo) {
        loadPhoto(photo);
      }
    };

    mainList.setAdapter(new PhotosAdapter(wrap(photos), inflater,
        clickListener, cachedImageFetcher, this.getResources()
            .getDisplayMetrics()));
    BaseAdapter adapter = (BaseAdapter) mainList.getAdapter();
    adapter.notifyDataSetChanged();
    adapter.notifyDataSetInvalidated();
    mainList.invalidateViews();
  }

  private void loadPhoto(Photo photo) {
    Intent intent = new Intent(this, PhotoViewActivity.class);
    intent.putParcelableArrayListExtra("photos", (ArrayList<Photo>) photos);
    intent.putExtra("index", photos.indexOf(photo));
    intent.putExtra("albumName", albumName);
    startActivity(intent);
  }

  /**
   * Wraps a list of {@link Photo}s into a list of {@link ThumbnailItem}s, so
   * they can be displayed in the list.
   */
  private static List<ThumbnailItem<Photo>> wrap(List<Photo> photos) {
    List<ThumbnailItem<Photo>> result = new ArrayList<ThumbnailItem<Photo>>();
    for (Photo photo : photos) {
      result.add(new ThumbnailItem<Photo>(photo.getName(), photo
          .getThumbnailUrl(), photo));
    }
    return result;
  }
}

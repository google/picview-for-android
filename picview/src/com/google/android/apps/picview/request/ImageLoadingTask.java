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

package com.google.android.apps.picview.request;

import java.net.URL;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.google.android.apps.picview.R;

/**
 * An asynchronous task that loads an image from the given URL.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class ImageLoadingTask extends AsyncTask<Void, Integer, Void> {

  private final ImageView imageView;
  private final URL url;
  private final CachedImageFetcher cachedImageFetcher;
  private Bitmap bitmap;
  private boolean cached = false;
  private ProgressDialog progressDialog;
  private boolean cancelUiUpdate = false;

  /**
   * Creates a new image loading task.
   * 
   * @param imageView
   *          the view on which to set the image once it is loaded
   * @param url
   *          the URL of the image
   * @param cachedImageFetcher
   *          the image fetcher and cache to use
   * @param progressDialog
   *          optional loading message. Shows a loading message if this is not
   *          null
   */
  public ImageLoadingTask(ImageView imageView, URL url,
      CachedImageFetcher cachedImageFetcher, ProgressDialog progressDialog) {
    this.imageView = imageView;
    this.url = url;
    this.cachedImageFetcher = cachedImageFetcher;
    this.progressDialog = progressDialog;
  }

  /**
   * Creates a new image loading task.
   * 
   * @param imageView
   *          the view on which to set the image once it is loaded
   * @param url
   *          the URL of the image
   * @param cachedImageFetcher
   *          the image fetcher and cache to use
   */
  public ImageLoadingTask(ImageView imageView, URL url,
      CachedImageFetcher cachedImageFetcher) {
    this(imageView, url, cachedImageFetcher, null);
  }

  /**
   * When set to true the {@link ImageLoadingTask} will not set the image bitmap
   * in the image view.
   * <p>
   * This only applies to fetches from the net. When the image is in cache, it
   * is set immediately anyway.
   */
  public void setCancelUiUpdate(boolean cancelUiUpdate) {
    this.cancelUiUpdate = cancelUiUpdate;
  }

  @Override
  protected void onPreExecute() {
    if (cachedImageFetcher.isCached(url)) {
      bitmap = cachedImageFetcher.cachedFetchImage(url);
      imageView.setImageBitmap(bitmap);
      cached = true;
    } else {
      if (progressDialog != null) {
        // TODO: This sometimes throws a window leaked error, when
        // activities are quickly switched around.
        progressDialog.show();
      }
      imageView.setImageResource(R.drawable.loading);
    }
  }

  @Override
  protected Void doInBackground(Void... params) {
    if (!cached) {
      bitmap = cachedImageFetcher.cachedFetchImage(url);
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void result) {
    if (!cached && !cancelUiUpdate) {
      imageView.setImageBitmap(bitmap);
    }
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.hide();
    }
  }
}

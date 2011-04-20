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

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.apps.picview.data.FileSystemImageCache;

/**
 * This class should be use to fetch images. It makes use of the file-system and
 * in-memory cache to load images.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class CachedImageFetcher {
  private static final String TAG = CachedImageFetcher.class.getSimpleName();

  private HashMap<URL, SoftReference<Bitmap>> cache = new HashMap<URL, SoftReference<Bitmap>>();

  /** Used to synchronize access based on URLs. */
  private HashMap<String, URL> urls = new HashMap<String, URL>();

  private FileSystemImageCache fileSystemCache;

  /**
   * Instantiated the {@link CachedImageFetcher}.
   * 
   * @param fileSystemCache
   *          the cache to use as a fallback, if the given value could not be
   *          found in memory
   */
  public CachedImageFetcher(FileSystemImageCache fileSystemCache) {
    this.fileSystemCache = fileSystemCache;
  }

  /**
   * Performs a cached fetch. If the image is in one of the caches (file-system
   * or in-memory), this version is returned. If the image could not be found in
   * cache, it's fetched and automatically put into both caches.
   */
  public Bitmap cachedFetchImage(URL url) {
    // Make sure we have a URL object that we can synchronize on.
    url = getSynchronizableInstance(url);

    // Synchronize per URL.
    synchronized (url) {
      // Get it from memory, if we still have it.
      if (cache.containsKey(url) && cache.get(url).get() != null) {
        return cache.get(url).get();
      }

      // If it's not in memory, try to load it from file system.
      Bitmap bitmap = fileSystemCache.get(url);

      // If it is also not found in the file system cache, try to fetch it
      // from the network.
      if (bitmap == null) {
        bitmap = fetchImageFromWeb(url);
        if (bitmap != null) {
          fileSystemCache.asyncPut(url, "TODO", bitmap);
        }
      }
      if (bitmap != null) {
        cache.put(url, new SoftReference<Bitmap>(bitmap));
      }
      return bitmap;
    }
  }

  /**
   * If the image with the given URL is not already in cache, it is fetched.
   * This can be used to pre-cache images that are likely to be requested soon.
   */
  public void maybePrefetchImageAsync(final URL url) {
    if (isCached(url)) {
      return;
    }
    (new AsyncTask<Void, Integer, Void>() {
      @Override
      protected Void doInBackground(Void... params) {
        cachedFetchImage(url);
        return null;
      }
    }).execute();
  }

  /**
   * Returns whether the image with the given URL exists in cache.
   */
  public boolean isCached(URL url) {
    return cache.containsKey(url);
  }

  private URL getSynchronizableInstance(URL url) {
    if (urls.containsKey(url.toString())) {
      url = urls.get(url.toString());
    } else {
      urls.put(url.toString(), url);
    }
    return url;
  }

  /**
   * Fetches the given image from the web.
   */
  private Bitmap fetchImageFromWeb(URL url) {
    try {
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoInput(true);
      conn.connect();
      InputStream is = conn.getInputStream();
      return BitmapFactory.decodeStream(is);
    } catch (OutOfMemoryError ex) {
      Log.e(TAG, "Out of memory, cannot create bitmap.");
      System.gc();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}

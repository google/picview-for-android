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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.util.Log;

import com.google.android.apps.picview.data.FileSystemWebResponseCache;
import com.google.android.apps.picview.data.WebResponseCursor.CachedWebResponse;

/**
 * Uses the request database and runtime cache to lookup requests. If the
 * responses are not stored in there, they are fetched from the web.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class CachedWebRequestFetcher {
  private static final String TAG = CachedWebRequestFetcher.class
      .getSimpleName();

  private HashMap<URL, String> cache = new HashMap<URL, String>();

  /** Used to synchronize access based on URLs. */
  private HashMap<String, URL> urls = new HashMap<String, URL>();

  private FileSystemWebResponseCache fileSystemCache;

  /**
   * Instantiated the {@link CachedImageFetcher}.
   * 
   * @param fileSystemCache
   *          the cache to use as a fallback, if the given value could not be
   *          found in memory
   */
  public CachedWebRequestFetcher(FileSystemWebResponseCache fileSystemCache) {
    this.fileSystemCache = fileSystemCache;
  }

  /**
   * Performs a cached fetch. If the response is in one of the caches
   * (file-system or in-memory), this version is returned. If the response could
   * not be found in cache, it's fetched and automatically put into both caches.
   * 
   * @param url
   *          the URL to fetch
   * @param forceFetchFromWeb
   *          whether the content should be fetched from the web, regardless of
   *          whether it is present in any of the caches
   */
  public CachedResponse<String> cachedFetch(URL url, boolean forceFetchFromWeb) {

    // Make sure we have a URL object that we can synchronize on.
    url = getSynchronizableInstance(url);

    // Synchronize per URL.
    synchronized (url) {
      String responseText = null;
      boolean fromDatabase = false;

      if (!forceFetchFromWeb) {
        // Get it from in-memory cache, if we have it.
        if (cache.containsKey(url) && cache.get(url) != null) {
          return new CachedResponse<String>(CachedResponse.FROM_MEMORY,
              cache.get(url));
        }

        // If it's not in-memory, try to load it from file system.
        CachedWebResponse response = fileSystemCache.get(url);

        if (response != null) {
          responseText = response.response;
          fromDatabase = true;
        }
      }

      // If it is also not found in the file system cache, or fetching
      // from cache was intentionally skipped, try to fetch it
      // from the network.
      if (responseText == null || forceFetchFromWeb) {
        responseText = fetchFromWeb(url);
        if (responseText != null) {
          fileSystemCache.asyncPut(url, "TODO", responseText);
        }
      }
      if (responseText != null) {
        cache.put(url, responseText);
      }
      return new CachedResponse<String>(fromDatabase ? CachedResponse.FROM_FILE
          : CachedResponse.NOT_CACHED, responseText);
    }
  }

  /**
   * Returns whether a response from a request with the given URL exists in the
   * in-memory cache.
   */
  public boolean isCached(URL url) {
    return cache.containsKey(url);
  }

  /**
   * Fetches the given URL from the web.
   */
  public String fetchFromWeb(URL url) {
    Log.d(TAG, "Fetching from web: " + url.toString());
    try {
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setUseCaches(false);
      conn.setReadTimeout(30000); // 30 seconds.
      conn.setDoInput(true);
      conn.connect();
      InputStream is = conn.getInputStream();
      return readStringFromStream(is);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
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
   * Read the content of an {@link InputStream} as String.
   * 
   * @param stream
   *          the stream to read from
   * @return the content of the stream or an empty string, if an error occurs.
   */
  private static String readStringFromStream(InputStream stream) {
    final int READ_BUFFER = 4096;
    StringBuilder builder = new StringBuilder();
    byte b[] = new byte[READ_BUFFER];
    int l = 0;
    try {
      if (stream == null) {
        return "";
      } else {
        while ((l = stream.read(b)) > 0) {
          builder.append(new String(b, 0, l));
        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();
      return "";
    }
    return builder.toString();
  }
}

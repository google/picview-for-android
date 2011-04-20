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

/**
 * A cached response, that can come from either filesystem, in-memory or was
 * directly fetched because no cached copy was present.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 * 
 * @param <T>
 *          the actual content type
 */
public class CachedResponse<T> {
  public static final int NOT_CACHED = 0;
  public static final int FROM_FILE = 1;
  public static final int FROM_MEMORY = 2;

  public final int cacheStatus;
  public final T content;

  public CachedResponse(int cacheStatus, T content) {
    this.cacheStatus = cacheStatus;
    this.content = content;
  }
}

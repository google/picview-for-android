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

package com.google.android.apps.picview.ui;

/**
 * Classes implementing this interface can be used to be shown e.g. in list
 * views that show thumbnails.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class ThumbnailItem<T> {
  private String title;
  private String thumbnailUrl;
  private T dataObject;

  /**
   * Initializes the thumbnail item with the given values
   */
  public ThumbnailItem(String title, String thumbnailUrl, T dataObject) {
    this.title = title;
    this.thumbnailUrl = thumbnailUrl;
    this.dataObject = dataObject;
  }

  /**
   * Returns the title of the thumbnail item.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Returns the URL to the thumbnail.
   */
  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  /**
   * Returns the data object associated with this thumbnail item.
   */
  public T getDataObject() {
    return dataObject;
  }
}

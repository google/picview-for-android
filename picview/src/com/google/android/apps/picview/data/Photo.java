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

package com.google.android.apps.picview.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Xml;

import com.google.android.apps.picview.data.parser.PicasaPhotosSaxHandler;

/**
 * The Photo data object containing all information about a photo.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class Photo implements Serializable, Parcelable {
  private static final long serialVersionUID = 1L;

  public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
    public Photo createFromParcel(Parcel in) {
      try {
        ObjectInputStream inputStream = new ObjectInputStream(
            new ByteArrayInputStream(in.createByteArray()));
        return (Photo) inputStream.readObject();
      } catch (StreamCorruptedException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      return null;
    }

    public Photo[] newArray(int size) {
      return new Photo[size];
    }
  };

  private String name;
  private String thumbnailUrl;
  private String imageUrl;

  /**
   * Parses photos XML (a list of photo; the contents of an album).
   * 
   * @param xmlStr
   *          the photo XML
   * @return a list of {@link Photo}s
   */
  public static List<Photo> parseFromPicasaXml(String xmlStr) {
    PicasaPhotosSaxHandler handler = new PicasaPhotosSaxHandler();
    try {
      // The Parser somehow has some trouble with a plus sign in the
      // content. This is a hack to fix this.
      // TODO: Maybe we should replace all these special characters with
      // XML entities?
      xmlStr = xmlStr.replace("+", "&#43;");
      Xml.parse(xmlStr, handler);
      return handler.getPhotos();
    } catch (SAXException e) {
      Log.e("Photo", e.getMessage(), e);
    }
    return new ArrayList<Photo>();
  }

  /**
   * Returns the photo name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the photo.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the thumbnail URL of the photo.
   */
  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  /**
   * Sets the thumbnail URL of the photo.
   */
  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }

  /**
   * Returns the URL of a medium resolution version the photo that can be used
   * to be shown on the screen.
   * <p>
   * TODO(haeberling): This is Picasa specific, this should be made more
   * general.
   */
  public String getMediumImageUrl(int photoSizeLongSide) {
    int pos = imageUrl.lastIndexOf('/');
    return imageUrl.substring(0, pos + 1) + 's' + photoSizeLongSide
        + imageUrl.substring(pos);
  }

  /**
   * Returns the URL to the highest resolution version of the photo.
   */
  public String getFullImageUrl() {
    return imageUrl;
  }

  /**
   * Sets the URL to the highest resolution version of the photo.
   */
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  /**
   * Returns the serialized Photo object.
   */
  public byte[] convertToBytes() {
    try {
      ByteArrayOutputStream result = new ByteArrayOutputStream();
      ObjectOutputStream output = new ObjectOutputStream(result);
      output.writeObject(this);
      return result.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeByteArray(convertToBytes());
  }
}

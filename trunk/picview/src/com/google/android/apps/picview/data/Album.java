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

import com.google.android.apps.picview.data.parser.PicasaAlbumsSaxHandler;

/**
 * The Album data object containing all information about an album.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class Album implements Serializable, Parcelable {
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

  private static final String TAG = "Album";
  private String name;
  private String thumbnailUrl;
  private String gdataUrl;

  /**
   * Parses Picasa albums XML and returns a list of albums.
   */
  public static List<Album> parseFromPicasaXml(String xmlStr) {
    PicasaAlbumsSaxHandler handler = new PicasaAlbumsSaxHandler();
    try {
      Xml.parse(xmlStr, handler);
      return handler.getAlbums();
    } catch (SAXException e) {
      Log.e(TAG, e.getMessage(), e);
    }
    return new ArrayList<Album>();
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setThumbnailUrl(String url) {
    this.thumbnailUrl = url;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public void setGdataUrl(String url) {
    this.gdataUrl = url;
  }

  public String getGdataUrl() {
    return gdataUrl;
  }

  /**
   * Returns the serialized object.
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

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

package com.google.android.apps.picview.data.parser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.android.apps.picview.data.Album;

/**
 * A SAX handler for parsing Picasa Albums XML.
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class PicasaAlbumsSaxHandler extends DefaultHandler {
  private List<Album> albums = new ArrayList<Album>();
  private Album currentAlbum;
  private StringBuilder builder = new StringBuilder();

  public List<Album> getAlbums() {
    return albums;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    builder.append(ch, start, length);
  }

  @Override
  public void endElement(String uri, String localName, String qName)
      throws SAXException {
    if (localName.equals("entry")) {
      albums.add(currentAlbum);
    } else if (localName.equals("title")) {
      if (currentAlbum != null) {
        currentAlbum.setName(builder.toString());
      }
    }
    builder.setLength(0);
  }

  @Override
  public void startElement(String uri, String localName, String qName,
      Attributes attributes) throws SAXException {
    if (localName.equals("entry")) {
      currentAlbum = new Album();
    } else {
      if (currentAlbum != null) {
        if (localName.equals("thumbnail")) {
          String thumbnail = attributes.getValue("", "url");
          currentAlbum.setThumbnailUrl(thumbnail);
        } else if (localName.equals("link")) {
          if (attributes.getValue("", "rel").equals(
              "http://schemas.google.com/g/2005#feed")) {
            String gdataUrl = attributes.getValue("", "href");
            currentAlbum.setGdataUrl(gdataUrl);
          }
        }
      }
    }
  }
}
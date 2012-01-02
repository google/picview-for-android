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

/**
 * An account the user follows
 * 
 * @author haeberling@google.com (Sascha Haeberling)
 */
public class Account {
  /** The position in the view. */
  public final int position;
  /** The account type (like Picasa). */
  public final int type;
  /** The ID within the account type, e.g. a username. */
  public final String id;
  /** A name the user gave the account. */
  public final String name;

  public Account(int position, int type, String id, String name) {
    this.position = position;
    this.type = type;
    this.id = id;
    this.name = name;
  }

  @Override
  public String toString() {
    if (name.length() > 0) {
      return name + " (" + id + ")";
    } else {
      return id;
    }
  }
}

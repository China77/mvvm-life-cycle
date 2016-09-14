/*
 * Copyright 2016 "Henry Tao <hi@henrytao.me>"
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.henrytao.mvvmlifecycle.viewmodel;

import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by henrytao on 9/14/16.
 */
public class Data {

  private static <T> T getData(Class<T> tClass, Map<Object, Object> data, Object key, T defaultValue) {
    try {
      Object object = key != null && data.containsKey(key) ? data.get(key) : defaultValue;
      return tClass.cast(object);
    } catch (ClassCastException ignore) {
      return defaultValue;
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> List<T> getListData(Class<T> tClass, Map<Object, Object> data, Object key, List<T> defaultValue) {
    try {
      Object object = key != null && data.containsKey(key) ? data.get(key) : defaultValue;
      return (List<T>) object;
    } catch (ClassCastException ignore) {
      return defaultValue;
    }
  }

  private final Map<Object, Object> mData = new HashMap<>();

  public Data() {
  }

  public boolean containsKey(Object key) {
    return mData.containsKey(key);
  }

  @Nullable
  public Object get(Object key) {
    return get(key, null);
  }

  public Object get(Object key, Object defaultValue) {
    return key != null && mData.containsKey(key) ? mData.get(key) : defaultValue;
  }

  public <T> T get(Object key, Class<T> tClass) {
    return get(key, tClass, null);
  }

  public <T> T get(Object key, Class<T> tClass, T defaultValue) {
    return getData(tClass, mData, key, defaultValue);
  }

  public boolean getBoolean(Object key) {
    return getBoolean(key, false);
  }

  public boolean getBoolean(Object key, boolean defaultValue) {
    return getData(Boolean.class, mData, key, defaultValue);
  }

  public byte getByte(Object key) {
    return getByte(key, (byte) 0);
  }

  public byte getByte(Object key, byte defaultValue) {
    return getData(Byte.class, mData, key, defaultValue);
  }

  public char getChar(Object key) {
    return getChar(key, (char) 0);
  }

  public char getChar(Object key, char defaultValue) {
    return getData(Character.class, mData, key, defaultValue);
  }

  public double getDouble(Object key) {
    return getDouble(key, 0.0);
  }

  public double getDouble(Object key, double defaultValue) {
    return getData(Double.class, mData, key, defaultValue);
  }

  public float getFloat(Object key) {
    return getFloat(key, 0f);
  }

  public float getFloat(Object key, float defaultValue) {
    return getData(Float.class, mData, key, defaultValue);
  }

  public int getInt(Object key) {
    return getInt(key, (int) 0);
  }

  public int getInt(Object key, int defaultValue) {
    return getData(Integer.class, mData, key, defaultValue);
  }

  public <T> List<T> getList(Object key, Class<T> tClass) {
    return getList(key, tClass, null);
  }

  public <T> List<T> getList(Object key, Class<T> tClass, List<T> defaultValue) {
    return getListData(tClass, mData, key, defaultValue);
  }

  public long getLong(Object key) {
    return getLong(key, 0l);
  }

  public long getLong(Object key, long defaultValue) {
    return getData(Long.class, mData, key, defaultValue);
  }

  public short getShort(Object key) {
    return getShort(key, (short) 0);
  }

  public short getShort(Object key, short defaultValue) {
    return getData(Short.class, mData, key, defaultValue);
  }

  @Nullable
  public String getString(Object key) {
    return getString(key, null);
  }

  public String getString(Object key, String defaultValue) {
    return getData(String.class, mData, key, defaultValue);
  }

  public void put(Object key, Object value) {
    mData.put(key, value);
  }
}

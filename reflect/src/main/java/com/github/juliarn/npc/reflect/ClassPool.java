/*
 * This file is part of npc-lib, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020-2021 Julian (juliarn), Pasqual (derklaro) and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.juliarn.npc.reflect;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ClassPool {

  private static final ConcurrentMap<String, Class<?>> CLASS_LOOKUP_CACHE = new ConcurrentHashMap<>();

  private ClassPool() {
    throw new UnsupportedOperationException();
  }

  public static @NotNull Class<?> lookupClass(@NotNull String... names) {
    // check if we have a cached class by this name
    Class<?> result;
    for (String name : names) {
      result = CLASS_LOOKUP_CACHE.get(name);
      if (result != null) {
        return result;
      }
    }
    // no class in cache, try a manual lookup
    for (String name : names) {
      result = javaClassLookupOrNull(name);
      if (result != null) {
        // cache the result
        CLASS_LOOKUP_CACHE.put(name, result);
        return result;
      }
    }
    // no class found for any of the names
    throw new IllegalArgumentException("Unable to lookup class by any of these names: "
        + String.join(", ", names));
  }

  public static @Nullable Class<?> javaClassLookupOrNull(@NotNull String name) {
    try {
      return Class.forName(name);
    } catch (ClassNotFoundException exception) {
      return null;
    }
  }
}

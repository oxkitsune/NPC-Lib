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

package com.github.juliarn.npc.reflect.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.jetbrains.annotations.NotNull;

public final class ReflectionUtils {

  private static final Field MODIFIER_FIELD = getField(Field.class, "modifiers", false);

  private ReflectionUtils() {
    throw new UnsupportedOperationException();
  }

  public static void makeAccessible(@NotNull Field field) {
    if (MODIFIER_FIELD != null && Modifier.isFinal(field.getModifiers())) {
      try {
        MODIFIER_FIELD.setInt(field, field.getModifiers() & ~Modifier.FINAL);
      } catch (IllegalAccessException exception) {
        exception.printStackTrace();
      }
    }
  }

  public static void setFieldValue(@NotNull Field field, @NotNull Object instance, Object value) {
    try {
      field.set(instance, value);
    } catch (IllegalAccessException exception) {
      exception.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T getFieldValue(@NotNull Field field, @NotNull Object instance) {
    try {
      return (T) field.get(instance);
    } catch (IllegalAccessException exception) {
      exception.printStackTrace();
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T getMethodResult(@NotNull Method method, @NotNull Object instance,
      @NotNull Object[] arguments) {
    try {
      return (T) method.invoke(instance, arguments);
    } catch (IllegalAccessException | InvocationTargetException exception) {
      exception.printStackTrace();
      return null;
    }
  }

  public static Field getField(@NotNull Class<?> clazz, String name, boolean mayBeStatic) {
    do {
      for (Field field : clazz.getDeclaredFields()) {
        if (field.getName().equals(name)
            && (mayBeStatic || !Modifier.isStatic(field.getModifiers()))) {
          try {
            field.setAccessible(true);
          } catch (Exception ignored) {
            // not accessible for us (silently ignore)
          }
          return field;
        }
      }
      // go one class up
      clazz = clazz.getSuperclass();
    } while (clazz != Object.class);
    // no result
    return null;
  }
}

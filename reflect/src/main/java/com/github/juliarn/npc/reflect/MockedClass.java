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

import com.github.juliarn.npc.reflect.filter.FieldFilter;
import com.github.juliarn.npc.reflect.filter.MethodFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public final class MockedClass {

  private static final Map<Class<?>, MockedClassInformation> CACHE = new ConcurrentHashMap<>();
  // instance access
  protected final Object instance;
  // type information
  private final Class<?> type;
  // method/fields
  private final Map<Class<?>, List<Field>> fields;
  private final Map<Class<?>, List<Method>> methods;

  public MockedClass(Object instance) {
    this.type = instance.getClass();
    this.instance = instance;
    // prepare the method/field information
    MockedClassInformation information = CACHE.computeIfAbsent(
        this.type, $ -> new MockedClassInformation(type));
    this.fields = information.fields;
    this.methods = information.methods;
  }

  public @NotNull Map<Class<?>, List<Field>> fields() {
    return this.fields;
  }

  public @NotNull Map<Class<?>, List<Method>> methods() {
    return this.methods;
  }

  public @NotNull FieldFilter.FieldFilterBuilder fieldFilter() {
    return new FieldFilter.FieldFilterBuilder(this);
  }

  public @NotNull MethodFilter.MethodFilterBuilder methodFilter() {
    return new MethodFilter.MethodFilterBuilder(this);
  }

  private static final class MockedClassInformation {

    private final Map<Class<?>, List<Field>> fields = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<Method>> methods = new ConcurrentHashMap<>();

    public MockedClassInformation(Class<?> type) {
      // cache fields
      for (Field field : type.getDeclaredFields()) {
        this.fields.computeIfAbsent(field.getType(), $ -> new LinkedList<>()).add(field);
      }
      // cache methods
      for (Method method : type.getDeclaredMethods()) {
        this.methods.computeIfAbsent(method.getReturnType(), $ -> new LinkedList<>()).add(method);
      }
    }
  }
}

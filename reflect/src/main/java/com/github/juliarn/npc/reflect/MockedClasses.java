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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MockedClasses {

  private static final Object[] EMPTY_PARAMETERS = new Object[0];
  private static final Map<Class<?>, Map<Integer, MethodHandle>> CACHED_CONSTRUCTORS = new ConcurrentHashMap<>();

  private MockedClasses() {
    throw new UnsupportedOperationException();
  }

  public static @NotNull MockedClass mock(@NotNull String... classNames) {
    return mock(EMPTY_PARAMETERS, classNames);
  }

  public static @NotNull MockedClass mock(@NotNull Object[] parameters, @NotNull String... names) {
    Class<?> clazz = ClassPool.lookupClass(names);
    return mock(clazz, parameters);
  }

  public static @NotNull MockedClass mock(@NotNull Class<?> clazz) {
    return mock(clazz, EMPTY_PARAMETERS);
  }

  public static @NotNull MockedClass mock(@NotNull Class<?> clazz, @NotNull Object... parameters) {
    // read the necessary information for the class cache
    Class<?>[] parameterTypes = instancesToTypeArray(parameters);
    int parameterTypeIdentifier = Arrays.hashCode(parameterTypes);
    // get the cached class handle or create a mapping
    Map<Integer, MethodHandle> cachedClassConstructors = CACHED_CONSTRUCTORS.computeIfAbsent(clazz,
        $ -> new ConcurrentHashMap<>());
    // check if we know a constructor for these argument types
    MethodHandle constructor = cachedClassConstructors.get(parameterTypeIdentifier);
    // try to create a new instance of the class
    MockedClass result = tryMock(constructor, parameters);
    if (result != null) {
      return result;
    }
    // try to find a constructor for the provided arguments
    try {
      constructor = MethodHandles.publicLookup()
          .findConstructor(clazz, constructorMT(parameterTypes));
      cachedClassConstructors.putIfAbsent(parameterTypeIdentifier, constructor);
    } catch (NoSuchMethodException | IllegalAccessException exception) {
      throw new IllegalStateException(String.format(
          "Unable to access or find constructor of class %s with argument types %s",
          clazz.getName(), Arrays.toString(parameterTypes)
      ), exception);
    }
    // instantiate the class
    result = tryMock(constructor, parameters);
    if (result == null) {
      throw new IllegalStateException("Unable to create instance of class " + clazz.getName());
    }
    return result;
  }

  private static @Nullable MockedClass tryMock(
      @Nullable MethodHandle constructor,
      @NotNull Object... parameters
  ) {
    if (constructor != null) {
      try {
        return new MockedClass(constructor.invoke(parameters));
      } catch (Throwable ignored) {
        return null;
      }
    }
    return null;
  }

  private static @NotNull Class<?>[] instancesToTypeArray(@NotNull Object... instances) {
    Class<?>[] result = (Class<?>[]) Array.newInstance(Class.class, instances.length);
    for (int i = 0; i < instances.length; i++) {
      result[i] = instances[i].getClass();
    }
    return result;
  }

  private static @NotNull MethodType constructorMT(@NotNull Class<?>[] parameterTypes) {
    return MethodType.methodType(void.class, parameterTypes);
  }
}

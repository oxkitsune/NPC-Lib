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

package com.github.juliarn.npc.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Iterables {

  public static @NotNull <E> Collection<E> combine(@NotNull Iterable<? super E>... iterables) {
    return Iterables.combine(ArrayList::new, iterables);
  }

  public static @NotNull <E, R extends Collection<E>> R combine(
      @NotNull Supplier<R> collectionFactory,
      @NotNull Iterable<? super E>... iterables
  ) {
    R result = collectionFactory.get();
    for (Iterable<? super E> iterable : iterables) {
      for (Object o : iterable) {
        result.add((E) o);
      }
    }
    return result;
  }

  public static @Nullable <E> E firstOrNull(
      @NotNull Iterable<E> iterable,
      @NotNull Predicate<E> filter
  ) {
    for (E e : iterable) {
      if (filter.test(e)) {
        return e;
      }
    }
    return null;
  }

  public static @NotNull <E> Collection<E> filterAll(
      @NotNull Iterable<E> iterable,
      @NotNull Predicate<E> filter
  ) {
    Collection<E> result = new ArrayList<>();
    Iterables.filterAll(iterable, filter, result::add);
    return result;
  }

  public static <E> void filterAll(
      @NotNull Iterable<E> iterable,
      @NotNull Predicate<E> filter,
      @NotNull Consumer<E> handler
  ) {
    for (E e : iterable) {
      if (filter.test(e)) {
        handler.accept(e);
      }
    }
  }
}

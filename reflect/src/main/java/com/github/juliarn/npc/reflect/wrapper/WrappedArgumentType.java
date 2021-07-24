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

package com.github.juliarn.npc.reflect.wrapper;

import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public final class WrappedArgumentType<I, O> {

  private final I inputType;
  private final Function<I, O> mapper;

  private WrappedArgumentType(@NotNull I inputType, @NotNull Function<I, O> mapper) {
    this.inputType = Objects.requireNonNull(inputType, "inputType");
    this.mapper = Objects.requireNonNull(mapper, "mapper");
  }

  public static @NotNull <I> WrappedArgumentType<I, I> passThrough(@NotNull I inputType) {
    return new WrappedArgumentType<>(inputType, Function.identity());
  }

  public static @NotNull <I, O> WrappedArgumentType<I, O> of(
      @NotNull I inputType,
      @NotNull Function<I, O> mapper
  ) {
    return new WrappedArgumentType<>(inputType, mapper);
  }

  public @NotNull I inputType() {
    return this.inputType;
  }

  public @NotNull Function<I, O> mapper() {
    return this.mapper;
  }

  public @NotNull O map() {
    return this.mapper.apply(this.inputType);
  }
}

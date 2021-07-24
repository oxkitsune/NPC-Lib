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

import com.github.juliarn.npc.reflect.utils.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FieldMemberAccessor extends AbstractMemberAccessor implements MemberAccessor {

  private final Field field;
  private final MockedClass holder;

  public FieldMemberAccessor(Field field, MockedClass holder) {
    super(field);

    this.field = field;
    this.holder = holder;
  }

  public <T> void set(@NotNull T newValue) {
    this.set(newValue, Function.identity());
  }

  public <T, R> void set(@NotNull T newValue, @NotNull Function<T, R> mapper) {
    ReflectionUtils.makeAccessible(this.field);
    ReflectionUtils.setFieldValue(this.field, this.holder.instance, mapper.apply(newValue));
  }

  public @Nullable <T> T get() {
    return this.get(Function.identity());
  }

  public @Nullable <T, R> R get(@NotNull Function<T, R> mapper) {
    T value = ReflectionUtils.getFieldValue(this.field, this.holder.instance);
    return mapper.apply(value);
  }
}

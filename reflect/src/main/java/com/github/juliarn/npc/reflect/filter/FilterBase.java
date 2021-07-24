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

package com.github.juliarn.npc.reflect.filter;

import com.github.juliarn.npc.reflect.FieldMemberAccessor;
import com.github.juliarn.npc.reflect.MemberAccessor;
import com.github.juliarn.npc.reflect.MockedClass;
import com.github.juliarn.npc.reflect.filter.FilterBase.Builder;
import com.github.juliarn.npc.utility.Buildable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public interface FilterBase<V extends Member, A extends MemberAccessor> extends
    Predicate<V>, Buildable<FilterBase<V, A>, Builder<V, A>> {

  static @NotNull FilterBase.Builder<Field, FieldMemberAccessor> forField(
      @NotNull MockedClass holder) {
    return new FieldFilter.FieldFilterBuilder(holder);
  }

  boolean required();

  @NotNull Collection<FilterBase<V, ?>> filters();

  @NotNull Optional<A> findFirst();

  @NotNull Collection<A> findAll();

  interface Builder<V extends Member, A extends MemberAccessor>
      extends Buildable.Builder<FilterBase<V, A>> {

    @NotNull Builder<V, A> required();

    @NotNull Builder<V, A> with(@NotNull FilterBase<V, ?> filter);

    @NotNull Builder<V, A> require(@NotNull Predicate<V> predicate);

    @NotNull Builder<V, A> requireModifier(int modifier);

    @NotNull Builder<V, A> requireName(@NotNull String name);

    @NotNull Builder<V, A> requireType(@NotNull Class<?> type);
  }
}

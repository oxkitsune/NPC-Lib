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

import com.github.juliarn.npc.common.Iterables;
import com.github.juliarn.npc.reflect.FieldMemberAccessor;
import com.github.juliarn.npc.reflect.MockedClass;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public class FieldFilter implements FilterBase<Field, FieldMemberAccessor> {

  private final MockedClass mockedClass;

  private final boolean required;
  private final Collection<Predicate<Field>> filters;
  private final Collection<FilterBase<Field, ?>> children;

  protected FieldFilter(MockedClass mockedClass, boolean required,
      Collection<Predicate<Field>> filters, Collection<FilterBase<Field, ?>> children) {
    this.mockedClass = mockedClass;
    this.required = required;
    this.filters = filters;
    this.children = children;
  }

  @Override
  public boolean required() {
    return this.required;
  }

  @Override
  public @NotNull Collection<FilterBase<Field, ?>> filters() {
    return Iterables.combine(this.filters, this.children);
  }

  @Override
  public @NotNull Optional<FieldMemberAccessor> findFirst() {
    for (List<Field> value : this.mockedClass.fields().values()) {
      Field field = Iterables.firstOrNull(value, this);
      if (field != null) {
        return Optional.of(new FieldMemberAccessor(field, this.mockedClass));
      }
    }
    return Optional.empty();
  }

  @Override
  public @NotNull Collection<FieldMemberAccessor> findAll() {
    Collection<FieldMemberAccessor> result = new ArrayList<>();
    for (List<Field> value : this.mockedClass.fields().values()) {
      Iterables.filterAll(
          value,
          this,
          field -> result.add(new FieldMemberAccessor(field, this.mockedClass))
      );
    }
    return Collections.unmodifiableCollection(result);
  }

  @Override
  public boolean test(Field field) {
    // check for the required defined filters
    for (Predicate<Field> filter : this.filters) {
      if (!filter.test(field)) {
        return false;
      }
    }
    // check softly for the parent filters
    for (FilterBase<Field, ?> child : this.children) {
      if (child.required() && !child.test(field)) {
        return false;
      }
    }
    // all checks passed
    return true;
  }

  @Override
  public @NotNull Builder<Field, FieldMemberAccessor> asBuilder() {
    FieldFilterBuilder builder = new FieldFilterBuilder(this.mockedClass);
    builder.required = this.required;
    builder.filters.addAll(this.filters);
    builder.children.addAll(this.children);
    return builder;
  }

  public static class FieldFilterBuilder implements
      FilterBase.Builder<Field, FieldMemberAccessor> {

    private final MockedClass holder;
    private final Collection<Predicate<Field>> filters = new CopyOnWriteArrayList<>();
    private final Collection<FilterBase<Field, ?>> children = new CopyOnWriteArrayList<>();

    private boolean required;

    public FieldFilterBuilder(MockedClass holder) {
      this.holder = holder;
    }

    @Override
    public @NotNull Builder<Field, FieldMemberAccessor> required() {
      this.required = true;
      return this;
    }

    @Override
    public @NotNull Builder<Field, FieldMemberAccessor> with(@NotNull FilterBase<Field, ?> filter) {
      this.children.add(filter);
      return this;
    }

    @Override
    public @NotNull Builder<Field, FieldMemberAccessor> require(@NotNull Predicate<Field> filter) {
      this.filters.add(filter);
      return this;
    }

    @Override
    public @NotNull Builder<Field, FieldMemberAccessor> requireModifier(int modifier) {
      return this.require(field -> (field.getModifiers() & modifier) != 0);
    }

    @Override
    public @NotNull Builder<Field, FieldMemberAccessor> requireName(@NotNull String name) {
      return this.require(field -> field.getName().equals(name));
    }

    @Override
    public @NotNull Builder<Field, FieldMemberAccessor> requireType(@NotNull Class<?> type) {
      return this.require(field -> field.getType().equals(type));
    }

    @Override
    public @NotNull FilterBase<Field, FieldMemberAccessor> build() {
      return new FieldFilter(this.holder, this.required, this.filters, this.children);
    }
  }
}

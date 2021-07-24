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
import com.github.juliarn.npc.reflect.MethodMemberAccessor;
import com.github.juliarn.npc.reflect.MockedClass;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public class MethodFilter implements FilterBase<Method, MethodMemberAccessor> {

  private final MockedClass mockedClass;

  private final boolean required;
  private final Collection<Predicate<Method>> filters;
  private final Collection<FilterBase<Method, ?>> children;

  protected MethodFilter(MockedClass mockedClass, boolean required,
      Collection<Predicate<Method>> filters, Collection<FilterBase<Method, ?>> children) {
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
  public @NotNull Collection<FilterBase<Method, ?>> filters() {
    return Iterables.combine(this.filters, this.children);
  }

  @Override
  public @NotNull Optional<MethodMemberAccessor> findFirst() {
    for (List<Method> value : this.mockedClass.methods().values()) {
      Method method = Iterables.firstOrNull(value, this);
      if (method != null) {
        return Optional.of(new MethodMemberAccessor(method, this.mockedClass));
      }
    }
    return Optional.empty();
  }

  @Override
  public @NotNull Collection<MethodMemberAccessor> findAll() {
    Collection<MethodMemberAccessor> result = new ArrayList<>();
    for (List<Method> value : this.mockedClass.methods().values()) {
      Iterables.filterAll(
          value,
          this,
          method -> result.add(new MethodMemberAccessor(method, this.mockedClass))
      );
    }
    return Collections.unmodifiableCollection(result);
  }

  @Override
  public boolean test(Method method) {
    // check for the required defined filters
    for (Predicate<Method> filter : this.filters) {
      if (!filter.test(method)) {
        return false;
      }
    }
    // check softly for the parent filters
    for (FilterBase<Method, ?> child : this.children) {
      if (child.required() && !child.test(method)) {
        return false;
      }
    }
    // all checks passed
    return true;
  }

  @Override
  public @NotNull Builder<Method, MethodMemberAccessor> asBuilder() {
    MethodFilterBuilder builder = new MethodFilterBuilder(this.mockedClass);
    builder.required = this.required;
    builder.filters.addAll(this.filters);
    builder.children.addAll(this.children);
    return builder;
  }

  public static class MethodFilterBuilder implements
      FilterBase.Builder<Method, MethodMemberAccessor> {

    private final MockedClass holder;
    private final Collection<Predicate<Method>> filters = new CopyOnWriteArrayList<>();
    private final Collection<FilterBase<Method, ?>> children = new CopyOnWriteArrayList<>();

    private boolean required;

    public MethodFilterBuilder(MockedClass holder) {
      this.holder = holder;
    }

    @Override
    public @NotNull Builder<Method, MethodMemberAccessor> required() {
      this.required = true;
      return this;
    }

    @Override
    public @NotNull Builder<Method, MethodMemberAccessor> with(@NotNull FilterBase<Method, ?> fil) {
      this.children.add(fil);
      return this;
    }

    @Override
    public @NotNull Builder<Method, MethodMemberAccessor> require(@NotNull Predicate<Method> fil) {
      this.filters.add(fil);
      return this;
    }

    @Override
    public @NotNull Builder<Method, MethodMemberAccessor> requireModifier(int modifier) {
      return this.require(method -> (method.getModifiers() & modifier) != 0);
    }

    @Override
    public @NotNull Builder<Method, MethodMemberAccessor> requireName(@NotNull String name) {
      return this.require(method -> method.getName().equals(name));
    }

    @Override
    public @NotNull Builder<Method, MethodMemberAccessor> requireType(@NotNull Class<?> type) {
      return this.require(method -> method.getReturnType().equals(type));
    }

    @Override
    public @NotNull FilterBase<Method, MethodMemberAccessor> build() {
      return new MethodFilter(this.holder, this.required, this.filters, this.children);
    }
  }
}

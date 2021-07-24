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
import com.github.juliarn.npc.reflect.wrapper.WrappedArgumentType;
import java.lang.reflect.Method;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public final class MethodMemberAccessor extends AbstractMemberAccessor implements MemberAccessor {

  private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

  private final Method method;
  private final MockedClass holder;

  public MethodMemberAccessor(Method method, MockedClass holder) {
    super(method);

    this.method = method;
    this.holder = holder;
  }

  public @NotNull <T> T invoke(@NotNull WrappedArgumentType<?, ?>... arguments) {
    return invoke(Function.identity(), arguments);
  }

  public @NotNull <I, O> O invoke(
      @NotNull Function<I, O> mapper,
      @NotNull WrappedArgumentType<?, ?>... arguments
  ) {
    Object[] unboxedArguments =
        arguments.length == 0 ? EMPTY_OBJECT_ARRAY : new Object[arguments.length];
    if (arguments.length > 0) {
      for (int i = 0; i < arguments.length; i++) {
        unboxedArguments[i] = arguments[i].map();
      }
    }

    I value = ReflectionUtils.getMethodResult(this.method, this.holder.instance, unboxedArguments);
    return mapper.apply(value);
  }
}

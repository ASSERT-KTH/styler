package com.developmentontheedge.be5.operation.util;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class Either<T, U>
{

    public static <T, U> Either<T, U> first(T first)
    {
        checkNotNull(first);
        return new Either<T, U>(first, null);
    }

    public static <T, U> Either<T, U> second(U second)
    {
        checkNotNull(second);
        return new Either<T, U>(null, second);
    }

    private final T first;
    private final U second;

    private Either(T first, U second)
    {
        this.first = first;
        this.second = second;
    }

    public boolean isFirst()
    {
        return first != null;
    }

    public boolean isSecond()
    {
        return second != null;
    }

    public T getFirst()
    {
        checkState(first != null);
        return first;
    }

    public U getSecond()
    {
        checkState(second != null);
        return second;
    }

    public Object get()
    {
        return isFirst() ? getFirst() : getSecond();
    }

    public <V> V map(Function<T, V> f, Function<U, V> g)
    {
        return isFirst() ? f.apply(getFirst()) : g.apply(getSecond());
    }

    public void apply(Consumer<T> f, Consumer<U> g)
    {
        if (isFirst())
        {
            f.accept(first);
        }
        else
        {
            g.accept(second);
        }
    }

}

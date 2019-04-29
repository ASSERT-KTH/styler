package com.developmentontheedge.be5.modules.core.util;

import com.google.common.collect.ImmutableList;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class Generators
{
//    /**
//     * Generates a list in reverse order.
//     * @return an immutable list of the generated elements
//     */
//    public static <T, U> List<U> reverseList(T lastItem, UnaryOperator<T> previous, Function<T, U> get)
//    {
//        List<U> list = StreamEx.iterate(lastItem, previous).takeWhile(Objects::nonNull).map(get).toList();
//        Collections.reverse(list);
//        return Collections.unmodifiableList(list);
//    }

    public static <K, T> List<T> forest(
            List<T> items,
            Function<T, K> getId,
            Predicate<T> isRoot,
            Function<T, K> getParentId,
            BiConsumer<T, T> addChild)
    {
        requireNonNull(items);
        requireNonNull(getId);
        requireNonNull(isRoot);
        requireNonNull(getParentId);
        requireNonNull(addChild);

        Map<K, T> index = StreamEx.of(items).toMap(getId, Function.identity());
        List<T> forest = new ArrayList<>();

        for (T item : items)
        {
            if (isRoot.test(item))
            {
                forest.add(item);
            }
            else
            {
                T parent = index.get(getParentId.apply(item));
                requireNonNull(parent);
                addChild.accept(parent, item);
            }
        }

        return ImmutableList.copyOf(forest);
    }

}

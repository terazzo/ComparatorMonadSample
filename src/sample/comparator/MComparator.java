package sample.comparator;

import java.util.Comparator;

import com.google.common.base.Function;

/**
 * ProcessStatusにbindして比較条件をチェーンできるようにする為のコンパレータクラス。
 * @param <T>　比較する物同士の型
 */
public abstract class MComparator<T> implements Function<Comparatee<T>, ProcessStatus<ComparisonResult, Comparatee<T>>> {
    public ProcessStatus<ComparisonResult, Comparatee<T>> apply(
            Comparatee<T> target) {
        return compare(target.left, target.right);
    }
    protected abstract ProcessStatus<ComparisonResult, Comparatee<T>> compare(T left, T right);

    public static <T> MComparator<T> toMComparator(final Comparator<T> comparator) {
        return new MComparator<T>() {
            @Override
            protected ProcessStatus<ComparisonResult, Comparatee<T>> compare(
                    T left, T right) {
                int sign = comparator.compare(left, right);
                if (sign == 0) {
                    return Comparisons.unsettled(Comparatee.of(left, right));
                } else {
                    return Comparisons.settled(ComparisonResult.valueOf(sign));
                }
            }
        };
    }
    public static <T> Comparator<T> toComparator(final MComparator<T> comparator) {
        return new Comparator<T>() {
            public int compare(T left, T right) {
                return comparator.apply(Comparatee.of(left, right))
                        .getResult(ComparisonResult.IDENTICAL).sign;
            }
        };
    }

}

package sample.comparator;

import com.google.common.base.Function;

public class Comparisons {

    // Œˆ’…ó‘Ô‚ğ¶¬‚µ‚Ä–ß‚·(Œ^ŒÅ’è”Å)
    public static <T> ProcessStatus<ComparisonResult, T> settled(ComparisonResult result) {
        return ProcessStatus.settled(result);
    }
    // –¢Œˆ’…ó‘Ô‚ğ¶¬‚µ‚Ä–ß‚·(Œ^ŒÅ’è”Å)
    public static <T> ProcessStatus<ComparisonResult, T> unsettled(T target) {
        return ProcessStatus.unsettled(target);
    }
    public static <T> ProcessStatus<ComparisonResult, Comparatee<T>> unsettled(T left, T right) {
        return ProcessStatus.unsettled(Comparatee.of(left, right));
    }

    // ’PˆÊ(Œ^ŒÅ’è”Å)
    public static <T> ProcessStatus<ComparisonResult, T> unit(T target) {
        return ProcessStatus.unit(target);
    }
    public static <T> Function<T, ProcessStatus<ComparisonResult, T>> unit() {
        return new Function<T, ProcessStatus<ComparisonResult, T>>() {
            public ProcessStatus<ComparisonResult, T> apply(T target) {
                return unit(target);
            }
        };
    }

    public static <T> ProcessStatus<ComparisonResult, T>
    flatten(ProcessStatus<ComparisonResult, ProcessStatus<ComparisonResult, T>> target) {
        return ProcessStatus.flatten(target);
    }

    public static <T, S> ProcessStatus<ComparisonResult, S>
    map(final Function<T, S> f, ProcessStatus<ComparisonResult, T> status) {
        return ProcessStatus.map(f, status);
    }

    public static <T> Function<ProcessStatus<ComparisonResult, ProcessStatus<ComparisonResult, T>>,
                               ProcessStatus<ComparisonResult, T>> flatten() {
        return new Function<ProcessStatus<ComparisonResult,ProcessStatus<ComparisonResult,T>>,
                            ProcessStatus<ComparisonResult,T>>() {
                public ProcessStatus<ComparisonResult, T>
                apply(ProcessStatus<ComparisonResult, ProcessStatus<ComparisonResult, T>> status) {
                    return ProcessStatus.flatten(status);
                }
            };
    }

}

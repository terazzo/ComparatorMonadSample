package sample.comparator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.google.common.base.Function;

/**
 * 現在の処理状態を表し、処理の続行を制御するためのクラス。
 * 未確定状態の場合、保持している処理対象targetをbind() or map()で渡された関数に渡し、結果を取得する。
 * 確定状態の場合、bind() or map()で渡された関数はバイパスして、resultを最終結果とする。
 * EitherをもとにしたErrorモナドと同じような動きになる。(決着済み=Left、未決着=Right)
 * 
 * @param <R> 処理結果の型
 * @param <T> 処理対象(引数)の型
 */
public final class ProcessStatus<R, T> {
    // 処理対象
    private final T target;
    // 処理結果
    private final R result;
    // 決着がついたかどうかのフラグ
    private final boolean isSettled;
    
    // コンストラクタはプライベート
    private ProcessStatus(T target, R result, boolean isSettled) {
        this.target = target;
        this.result = result;
        this.isSettled = isSettled;
    }
    /* ファクトリメソッド */
    // 決着状態を生成して戻す
    public static <R, T> ProcessStatus<R, T> settled(R result) {
        return new ProcessStatus<R, T>(null, result, true);
    }
    // 未決着状態を生成して戻す
    public static <R, T> ProcessStatus<R, T> unsettled(T target) {
        return new ProcessStatus<R, T>(target, null, false);
    }
    /* 結果を取り出すためのメソッド */
    // 決着がついたかどうかのフラグを戻す
    public boolean isSettled() {
        return isSettled;
    }
    // 結果が確定していればその値を、未確定ならdefaultValueを戻す。
    public R getResult(R defaultValue) {
        return isSettled ? result : defaultValue;
    }

    /* equals, hashCode, toStringを適当に実装 */
    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
    @Override
    public String toString() {
        return isSettled
            ? String.format("settled(%s)", result.toString())
            : String.format("unsettled(%s)", target.toString());
    }



    /* 以下モナド的なメソッド */

    // 単位
    public static <R, T> ProcessStatus<R, T> unit(T target) {
        return unsettled(target);
    }
    public static <R, T> Function<T, ProcessStatus<R, T>> unit() {
        return new Function<T, ProcessStatus<R, T>>() {
            public ProcessStatus<R, T> apply(T target) {
                return unit(target);
            }
        };
    }

    /* 拡張スタイル */
    /**
     * ProcessStatusとFunctionからProcessStatusを自然な感じで戻す。
     * 具体的には、決着済みの場合は関数呼び出しを行わずthisを、
     * 未決着の場合にはhandlerにtargetを渡して得た結果を戻す。
     * @param handler bindする関数
     * @param <S> 戻り値のtargetの型
     */
    public <S> ProcessStatus<R, S> bind(Function<T, ProcessStatus<R, S>> handler) {
        if (isSettled) {
            // もう決着済みなのでcomparatorに関係なくthisを戻す。
            // JavaにはNothingとか無いので残念だがキャストでごまかす。
            // isSettled==trueの時は必ずresult==nullなので何も問題ないはず。
            return (ProcessStatus<R, S>) this;
        } else {
            // 未決着なので関数を呼び出してその結果を使用する。
            return handler.apply(target);
        }
    }
    // その派生
    public static <R, T, S> ProcessStatus<R, S> bind(
            ProcessStatus<R, T> status, Function<T, ProcessStatus<R, S>> handler) {
        return status.bind(handler);
    }
    public static <R, T, S> Function<ProcessStatus<R, T>, ProcessStatus<R, S>> ext(
            final Function<T, ProcessStatus<R, S>> handler) {
        return new Function<ProcessStatus<R,T>, ProcessStatus<R,S>>() {
            public ProcessStatus<R, S> apply(ProcessStatus<R, T> status) {
                return bind(status, handler);
            }
        };
    }

    /* 代数スタイルを拡張スタイルで定義 */
    // flatten = bind(id) = bind(ext(unit))
    public static <R, T> ProcessStatus<R, T> flatten(ProcessStatus<R, ProcessStatus<R, T>> ss) {
        return bind(ss, ext(ProcessStatus.<R, T>unit()));
    }
    // map(f, m) = bind(m, ( \x -> unit (f x) ))
    public static <R, T, S> ProcessStatus<R, S> map(final Function<T, S> f, ProcessStatus<R, T> status) {
        return status.bind(new Function<T, ProcessStatus<R, S>>() {
            public ProcessStatus<R, S> apply(T x) {
                return unit(f.apply(x));
            }
        });
    }
    public static <R, T> Function<ProcessStatus<R, ProcessStatus<R, T>>, ProcessStatus<R, T>> flatten() {
        return new Function<ProcessStatus<R, ProcessStatus<R, T>>, ProcessStatus<R, T>>() {
            public ProcessStatus<R, T> apply(
                    ProcessStatus<R, ProcessStatus<R, T>> status) {
                return flatten(status);
            }
        };
    }

}

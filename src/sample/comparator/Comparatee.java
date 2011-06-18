package sample.comparator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * 比較対象となるデータの対を保持する
 * @param <T> 比較対象となるデータの型
 */
public class Comparatee<T> {
    public final T left;
    public final T right;
    public Comparatee(T left, T right) {
        super();
        this.left = left;
        this.right = right;
    }
    public static <T> Comparatee<T> of(T left, T right) {
        return new Comparatee<T>(left, right);
    }
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
        return String.format("%s, %s", left.toString(), right.toString());
    }
}

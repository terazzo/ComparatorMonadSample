package sample.comparator;

/**
 * 比較結果をあらわすEnum
 */
public enum ComparisonResult {
    // 「左側が小さい」という結果
    SMALLER(-1),
    // 「左側と右側が等しい」という結果
    IDENTICAL(0),
    // 「左側が大きい」という結果
    LARGER(1);

    // 比較結果を負の数、ゼロ、正の数で表したもの
    public final int sign;

    ComparisonResult(int sign) {
        this.sign = sign;
    }
    // ComparableやComparatorと協同しやすいように変換メソッドを定義
    public static ComparisonResult valueOf(int sign) {
        return sign < 0 ? SMALLER
             : sign > 0 ? LARGER
             :            IDENTICAL;
    }
}

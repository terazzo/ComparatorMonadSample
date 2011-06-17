package sample.comparator;

/**
 * ��r���ʂ�����킷Enum
 */
public enum ComparisonResult {
    // �u�������������v�Ƃ������ʂ��擾
    SMALLER(-1),
    // �u�����ƉE�����������v�Ƃ������ʂ��擾 
    IDENTICAL(0),
    // �u�������傫���v�Ƃ������ʂ��擾
    LARGER(1);

    // ��r���ʂ𕉂̐��A�[���A���̐��ŕ\��������
    public final int sign;

    ComparisonResult(int sign) {
        this.sign = sign;
    }
    // Comparable��Comparator�Ƌ������₷���悤�ɕϊ����\�b�h���`
    public static ComparisonResult valueOf(int sign) {
        return sign < 0 ? SMALLER
             : sign > 0 ? LARGER
             :            IDENTICAL;
    }
}

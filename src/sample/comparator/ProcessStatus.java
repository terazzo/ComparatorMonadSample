package sample.comparator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.google.common.base.Function;

/**
 * ���݂̏�����Ԃ�\���A�����̑��s�𐧌䂷�邽�߂̃N���X�B
 * ���m���Ԃ̏ꍇ�A�ێ����Ă��鏈���Ώ�target��bind() or map()�œn���ꂽ�֐��ɓn���A���ʂ��擾����B
 * �m���Ԃ̏ꍇ�Abind() or map()�œn���ꂽ�֐��̓o�C�p�X���āAresult���ŏI���ʂƂ���B
 * Either�����Ƃɂ���Error���i�h�Ɠ����悤�ȓ����ɂȂ�B(�����ς�=Left�A������=Right)
 * 
 * @param <R> �߂�l�̌^
 * @param <T> �����Ώ�(����)�̌^
 */
public final class ProcessStatus<R, T> {
    // �����Ώ�
    private final T target;
    // ��������
    private final R result;
    // �������������ǂ����̃t���O
    private final boolean isSettled;
    
    // �R���X�g���N�^�̓v���C�x�[�g
    private ProcessStatus(T target, R result, boolean isSettled) {
        this.target = target;
        this.result = result;
        this.isSettled = isSettled;
    }
    /* �t�@�N�g�����\�b�h */
    // ������Ԃ𐶐����Ė߂�
    public static <R, T> ProcessStatus<R, T> settled(R result) {
        return new ProcessStatus<R, T>(null, result, true);
    }
    // ��������Ԃ𐶐����Ė߂�
    public static <R, T> ProcessStatus<R, T> unsettled(T target) {
        return new ProcessStatus<R, T>(target, null, false);
    }
    /* ���ʂ����o�����߂̃��\�b�h */
    // �������������ǂ����̃t���O��߂�
    public boolean isSettled() {
        return isSettled;
    }
    // ���ʂ��m�肵�Ă���΂��̒l���A���m��Ȃ�defaultValue��߂��B
    public R getResult(R defaultValue) {
        return isSettled ? result : defaultValue;
    }

    /* equals, hashCode, toString��K���Ɏ��� */
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



    /* �ȉ����i�h�I�ȃ��\�b�h */

    // �P��
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

    /* �g���X�^�C�� */
    /**
     * ProcessStatus��Function����ProcessStatus�����R�Ȋ����Ŗ߂��B
     * ��̓I�ɂ́A�����ς݂̏ꍇ�͊֐��Ăяo�����s�킸this���A
     * �������̏ꍇ�ɂ�handler��target��n���ē������ʂ�߂��B
     * @param handler bind����֐�
     * @param <S> �߂�l��target�̌^
     */
    public <S> ProcessStatus<R, S> bind(Function<T, ProcessStatus<R, S>> handler) {
        if (isSettled) {
            // ���������ς݂Ȃ̂�comparator�Ɋ֌W�Ȃ�this��߂��B
            // Java�ɂ�Nothing�Ƃ������̂Ŏc�O�����L���X�g�ł��܂����B
            // isSettled==true�̎��͕K��result==null�Ȃ̂ŉ������Ȃ��͂��B
            return (ProcessStatus<R, S>) this;
        } else {
            // �������Ȃ̂Ŋ֐����Ăяo���Ă��̌��ʂ��g�p����B
            return handler.apply(target);
        }
    }
    // ���̔h��
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

    /* �㐔�X�^�C�����g���X�^�C���Œ�` */
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

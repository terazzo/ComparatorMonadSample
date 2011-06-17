package sample.comparator;

import static junit.framework.Assert.*;
import static sample.comparator.Comparisons.*;
import static sample.comparator.MComparator.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;

public class ComparisonsTest {
    private List<Entry> entries;
    @Before
    public void initSamples() throws Exception {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        entries = Arrays.asList(
            new Entry(6, 2, fmt.parse("2011-03-03"), "iPad2欲しいですか？","..."),
            new Entry(5, 2, fmt.parse("2011-01-06"), "今年の抱負","..."),
            new Entry(4, 2, fmt.parse("2011-03-10"), "ブログと私","..."),
            new Entry(3, 2, fmt.parse("2011-02-18"), "これはモナドですか？","..."),
            new Entry(2, 2, fmt.parse("2011-02-18"), "ついつい集めてしまうもの","..."),
            new Entry(1, 1, fmt.parse("2011-02-18"), "ついつい集めてしまうもの","..."));
    }
    // pubDateの降順でソートする時のコンパレータ
    private MComparator<Entry> pubDateComparator = toMComparator(
        new Comparator<Entry>(){
            public int compare(Entry first, Entry second) {
                return -1 * first.getPubDate().compareTo(second.getPubDate());
            }
        });
    // idの昇順でソートする時のコンパレータ
    private MComparator<Entry> idComparator = toMComparator(
        new Comparator<Entry>(){
           public int compare(Entry first, Entry second) {
                return first.getId().compareTo(second.getId());
            }
        });

    @Test
    public void testComparison() {
        //(1)pubDateの降順、(2)idの昇順、という順序でソートする時のComparator
        Comparator<Entry> composedComparator = toComparator(new MComparator<Entry>() {
            protected ProcessStatus<ComparisonResult, Comparatee<Entry>> compare(Entry left, Entry right) {
                return
                    unsettled(left, right)
                    .bind(pubDateComparator)
                    .bind(idComparator);
            }
        });

        ArrayList<Entry> sorted = new ArrayList<Entry>(entries);
        Collections.sort(sorted, composedComparator);

        // 順番になっているか確認。一つ前と比べると、日付が古い(before)か、日付が等しいならIDが大きいはず。
        Entry last = null;
        for (Entry entry : sorted) {
            assertTrue(
                last == null ||
                entry.getPubDate().before(last.getPubDate()) ||     // 一つ前より日付が古いか、
                (last.getPubDate().equals(entry.getPubDate()) &&
                        entry.getId() > last.getId())           // 日付が等しくIDが大きい
            );
            last = entry;
            System.out.printf("id=%d, pubDate = %s, title=%s\n",
                    entry.getId(), entry.getPubDate(), entry.getTitle());
        }
    }

    // その1.「(return x) >>= f ≡ f x」
    @Test
    public void testRule1() {
        for (Entry left : entries) {
            for (Entry right : entries) {
                Comparatee<Entry> x = Comparatee.of(left, right);
                assertEquals(
                        unit(x).bind(pubDateComparator), 
                        pubDateComparator.apply(x));
            }
        }
    }
    // その2. 「m >>= return ≡ m」
    @Test
    public void testRule2() {
        for (Entry left : entries) {
            for (Entry right : entries) {
                 ProcessStatus<ComparisonResult, Comparatee<Entry>>
                 m = unsettled(left, right);
                 
                assertEquals(
                        m.bind(Comparisons.<Comparatee<Entry>>unit()),
                        m);
            }
        }
    }
    // その3. 「(m >>= f) >>= g ≡ m >>= ( \x -> (f x >>= g) )」
    @Test
    public void testRule3() {
        for (Entry left : entries) {
            for (Entry right : entries) {
                 ProcessStatus<ComparisonResult, Comparatee<Entry>>
                 m = unsettled(left, right);
                 
                assertEquals(
                        m.bind(pubDateComparator).bind(idComparator),
                        m.bind(new Function<Comparatee<Entry>, ProcessStatus<ComparisonResult, Comparatee<Entry>>>() {
                            public ProcessStatus<ComparisonResult, Comparatee<Entry>> apply(Comparatee<Entry> target) {
                                    return pubDateComparator.apply(target).bind(idComparator);
                             }
                        }));
            }
        }
    }


    // 代数スタイルのモナド則
    @Test
    public void testMonad1() {
        ProcessStatus<ComparisonResult, Comparatee<String>> status =
                unsettled("left", "right");

        // モナド則(1) flatten(unit(m)) = m
        assertEquals(
                flatten(unit(status)),
                status);
    }
    @Test
    public void testMonad2() {
        ProcessStatus<ComparisonResult, Comparatee<String>> status = 
                unsettled("left", "right");

        // モナド則(2) flatten(map(unit, m)) = m
        assertEquals(
                flatten(map(Comparisons.<Comparatee<String>>unit(), status)),
                status);
    }

    @Test
    public void testMonad3() {
        ProcessStatus<ComparisonResult,
            ProcessStatus<ComparisonResult,
                ProcessStatus<ComparisonResult, Comparatee<String>>>>
            sss = unsettled(unsettled(Comparisons.unsettled("left", "right")));

        // モナド則(3) flatten(flatten(mmm)) = flatten(map(flatten, mmm))
        assertEquals(
                flatten(flatten(sss)),
                flatten(map(Comparisons.<Comparatee<String>>flatten(), sss)));

        System.out.println("sss = " + sss);
        System.out.println("flatten(flatten(sss)) = " + flatten(ProcessStatus.flatten(sss)));
    }

    @Test
    public void testFlattenWhenOutsideIsUnsettle() {
        ProcessStatus<ComparisonResult, ProcessStatus<ComparisonResult, Comparatee<String>>>
            inner = unsettled(Comparisons.unsettled("left", "right"));

        ProcessStatus<ComparisonResult,
            ProcessStatus<ComparisonResult,
                ProcessStatus<ComparisonResult, Comparatee<String>>>>
            outer = unsettled(inner);

        assertEquals(inner, flatten(outer));
    }   
    @Test
    public void testFlattenWhenOutsideIsSettle() {
        ProcessStatus<ComparisonResult,
            ProcessStatus<ComparisonResult,
                ProcessStatus<ComparisonResult, Comparatee<String>>>>
            outer = settled(ComparisonResult.SMALLER);

        assertEquals(outer, flatten(outer));
    }   

}

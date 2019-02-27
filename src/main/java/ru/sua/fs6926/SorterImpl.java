package ru.sua.fs6926;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;

import static java.util.stream.Collectors.toList;

@Slf4j
public class SorterImpl<T> implements Sorter<T> {

    private PrintWriter out;

    public SorterImpl(String outputFileName, String encoding) {
        try {
            this.out = new PrintWriter(outputFileName, encoding);
        } catch (FileNotFoundException e) {
            log.error("Проблема с созданием выходного файла \'{}\' по причине \'{}\'", Launcher.outputFileName, e.getMessage());
            System.exit(10);
        } catch (UnsupportedEncodingException e) {
            log.error("Не поддерживается кодировка выходного файла \'{}\' по причине \'{}\'", Launcher.outputFileName, e.getMessage());
            System.exit(11);
        }
    }

    @Override
    public void doSort(List<BlockingDeque<T>> deques) {

        while (deques.stream().anyMatch(d -> d.peekFirst() != null)) {
            deques.removeIf(d -> (d.peekFirst() == null && d.peekLast() == null)); // or d.size == 0
            if (deques.size() == 0) break;
            BlockingDeque<T> actualDeque = null;
            if (Launcher.isStrings) {
                // TODO Strings sort
            } else {
                if (Launcher.isAscending) {
                    actualDeque = getActualDequeForIntegers(deques, Want.MIN);
                } else {
                    actualDeque = getActualDequeForIntegers(deques, Want.MAX);
                }
            }
            if (failedSortOrder(actualDeque)) {
                deques.remove(actualDeque);
                log.error("Нарушение сортировки в одном из входных файлов. Файл исключен из обработки."); // file name will show when shutdown reafer
                continue;
            }
            try {
                out.println(actualDeque.takeFirst());
            } catch (InterruptedException e) {
                log.error("Прерывание при получении значения из очереди. \'{}\'", e.getMessage());
            }
        }
    }

    private boolean failedSortOrder(BlockingDeque<T> deque) {
        if (deque.peekFirst() == null || deque.peekLast() == null) return false;
        if (Launcher.isStrings) {
            // TODO check sort for strings
            return false;
        } else {
            if (Launcher.isAscending) {
                return ((Integer) deque.peekFirst()) > ((Integer) deque.peekLast());
            } else {
                return ((Integer) deque.peekFirst()) < ((Integer) deque.peekLast());
            }
        }
    }

    private BlockingDeque<T> getActualDequeForIntegers(List<BlockingDeque<T>> deques, Want want) {
        Integer iArray[] = new Integer[deques.size()];
        BlockingDeque<T> dArray[] = new BlockingDeque[deques.size()];
        int i = 0;
        for (BlockingDeque<T> deque : deques) {
            if (deque.peekFirst() == null) continue;
            iArray[i] = (Integer) deque.peekFirst();
            dArray[i] = deque;
            i++;
        }
        if (want.equals(Want.MAX)) {
            return dArray[findMaxIndex(iArray)];
        } else {
            return dArray[findMinIndex(iArray)];
        }
    }

    private int findMinIndex(Integer[] numbers) {
        Optional<Integer> minimun = Arrays.stream(numbers).min(Comparator.comparingInt(Integer::intValue));
        return Arrays.stream(numbers).collect(toList()).indexOf(minimun.get());
    }

    private int findMaxIndex(Integer[] numbers) {
        Optional<Integer> maximum = Arrays.stream(numbers).max(Comparator.comparingInt(Integer::intValue));
        return Arrays.stream(numbers).collect(toList()).indexOf(maximum.get());
    }

    @Override
    public void close() {
        if (out != null) out.close();
    }

    private enum Want {
        MIN, MAX
    }
}

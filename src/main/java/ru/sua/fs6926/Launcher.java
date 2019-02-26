package ru.sua.fs6926;

import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.BlockingDeque;

import static java.util.stream.Collectors.toList;

@Slf4j
public class Launcher {
    static boolean isAscending = true;
    static boolean isStrings = true;
    static String outputFileName = "";
    static String encoding = "utf-8";
    static List<String> inputFileNames = new ArrayList<>();
    private static List<BlockingDeque<String>> dequesForStrings = new ArrayList<>();
    private static List<BlockingDeque<Integer>> dequesForIntegers = new ArrayList<>();
    private static List<ReadFileLineByLine<String>> workersForStrings = new ArrayList<>();
    private static List<ReadFileLineByLine<Integer>> workersForIntegers = new ArrayList<>();

    public static void main(String[] args) {
        new ParseCommandLine(args).invoke();
        if (isStrings) {
            doWork(workersForStrings, dequesForStrings);
        } else {
            doWork(workersForIntegers, dequesForIntegers);
        }
        shutdownMe();
    }

    private static <T> void doWork(List<ReadFileLineByLine<T>> workers, List<BlockingDeque<T>> deques) {
        try (PrintWriter out = new PrintWriter(outputFileName, encoding)) {
            for (String file : inputFileNames) {
                ReadFileLineByLine<T> worker = new ReadFileLineByLine<>(file, encoding);
                if (!worker.isFailed())
                    workers.add(worker);
            }
            if (workers.size() == 0) {
                log.warn("Нет доступных для обработки входных файлов.");
                return;
            } else {
                for (ReadFileLineByLine<T> reader : workers) {
                    BlockingDeque<T> deque = reader.beginAsyncReading();
                    if (deque != null) deques.add(deque);
                }
            }
            doSort(deques, out);
        } catch (FileNotFoundException e) {
            log.error("Проблема с созданием выходного файла \'{}\' по причине \'{}\'", outputFileName, e.getMessage());
        } catch (UnsupportedEncodingException e) {
            log.error("Не поддерживается кодировка выходного файла \'{}\' по причине \'{}\'", outputFileName, e.getMessage());
        }
    }

    private static <T> void doSort(List<BlockingDeque<T>> deques, PrintWriter out) {
        while (deques.stream().anyMatch(d -> d.peekFirst() != null)) {
            deques.removeIf(d -> (d.peekFirst() == null && d.peekLast() == null)); // or d.size == 0
            if (deques.size() == 0) break;
            BlockingDeque<T> actualDeque = null;
            if (isStrings) {
                // TODO Strings sort
            } else {
                if (isAscending) {
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

    private static <T> boolean failedSortOrder(BlockingDeque<T> deque) {
        if (deque.peekFirst() == null || deque.peekLast() == null) return false;
        if (isStrings) {
            // TODO check sort for strings
            return false;
        } else {
            if (isAscending) {
                return ((Integer) deque.peekFirst()) > ((Integer) deque.peekLast());
            } else {
                return ((Integer) deque.peekFirst()) < ((Integer) deque.peekLast());
            }
        }
    }

    private static <T> BlockingDeque<T> getActualDequeForIntegers(List<BlockingDeque<T>> deques, Want want) {
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

    private static int findMinIndex(Integer[] numbers) {
        Optional<Integer> minimun = Arrays.stream(numbers).min(Comparator.comparingInt(Integer::intValue));
        return Arrays.stream(numbers).collect(toList()).indexOf(minimun.get());
    }

    private static int findMaxIndex(Integer[] numbers) {
        Optional<Integer> maximum = Arrays.stream(numbers).max(Comparator.comparingInt(Integer::intValue));
        return Arrays.stream(numbers).collect(toList()).indexOf(maximum.get());
    }


    private static void shutdownMe() {
        workersForStrings.forEach(ReadFileLineByLine::stopMe);
        workersForIntegers.forEach(ReadFileLineByLine::stopMe);
    }

    private enum Want {
        MIN, MAX
    }
}

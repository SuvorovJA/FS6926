package ru.sua.fs6926;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;

@Slf4j
public class WorkersHolder<T> implements Closeable {
    private List<BlockingDeque<T>> deques = new ArrayList<>();
    private List<ReadFileLineByLine<T>> workers = new ArrayList<>();
    private Sorter<T> sorter;


    public WorkersHolder(Sorter<T> sorter) {
        this.sorter = sorter;
    }

    public void doWork() {
        for (String file : Launcher.inputFileNames) {
            ReadFileLineByLine<T> worker = new ReadFileLineByLine<>(file, Launcher.encoding);
            if (!worker.isFailed()) workers.add(worker);
        }
        if (workers.size() == 0) {
            log.warn("Нет доступных для обработки входных файлов.");
            System.exit(20);
        } else {
            for (ReadFileLineByLine<T> reader : workers) {
                BlockingDeque<T> deque = reader.beginAsyncReading();
                if (deque != null) deques.add(deque);
            }
        }
        sorter.doSort(deques);
    }

    @Override
    public void close() {
        workers.forEach(ReadFileLineByLine::close);
    }
}

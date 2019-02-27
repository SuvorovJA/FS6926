package ru.sua.fs6926;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class WorkersHolder implements Closeable {
    private List<BlockingDeque<String>> deques = new CopyOnWriteArrayList<>();
    private List<ReadFileLineByLine> workers = new ArrayList<>();
    private Map<BlockingDeque<String>, Boolean> hasFinishDataForDeque = new ConcurrentHashMap<>();
    private Sorter sorter;


    public WorkersHolder(Sorter sorter) {
        this.sorter = sorter;
    }

    public void doWork() {
        for (String file : Launcher.inputFileNames) {
            ReadFileLineByLine worker = new ReadFileLineByLine(file, Launcher.encoding, hasFinishDataForDeque);
            if (!worker.isFailed()) workers.add(worker);
        }
        if (workers.size() == 0) {
            log.error("Нет доступных для обработки входных файлов.");
            System.exit(20);
        } else {
            for (ReadFileLineByLine reader : workers) {
                BlockingDeque<String> deque = reader.beginAsyncReading();
                if (deque != null) deques.add(deque);
            }
        }
        sorter.doSort(deques, hasFinishDataForDeque);
    }

    @Override
    public void close() {
        workers.forEach(ReadFileLineByLine::close);
    }
}

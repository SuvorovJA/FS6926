package ru.sua.fs6926;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;


@Slf4j
public class ReadFileLineByLine<T> implements Runnable, Closeable {

    private String filename;
    private FileInputStream inputStream;
    private Scanner sc;
    private BlockingDeque<T> queue;
    private ExecutorService service;
    private boolean failed;

    public ReadFileLineByLine(String filename, String encoding) {
        this.filename = filename;
        try {
            inputStream = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            log.error("Входной файл \'{}\' не открыт по причине \'{}\'. В сортировке не участвует.", filename, e.getMessage());
            failed = true;
            return;
        }

        sc = new Scanner(inputStream, encoding);
        queue = new LinkedBlockingDeque<>(2);
        service = Executors.newSingleThreadExecutor();
    }

    public boolean isFailed() {
        return failed;
    }

    public BlockingDeque<T> getQueue() {
        return queue;
    }

    public BlockingDeque<T> beginAsyncReading() {
        if (!failed) {
            service.submit(this);
            return queue;
        } else {
            return null;
        }
    }

    @Override
    public void run() {
        try {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                T value = null;
                if (Launcher.isStrings) {
                    value = (T) line;
                } else {
                    try {
                        value = (T) Integer.valueOf(line);
                    } catch (NumberFormatException e) {
                        log.warn("Остановлено чтение файла \'{}\' на значении \'{}\', по причине \'{}\'", filename, line, e.getMessage());
                        close();
                    }
                }
                if (value == null) continue;
                try {
                    queue.putLast(value);
                } catch (InterruptedException e) {
                    log.warn("Прервано чтение файла \'{}\'", filename);
                    close();
                }
            }
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null)
                log.warn("Сбой при чтении файла \'{}\' по причине \'{}\'", filename, sc.ioException().getMessage());
        } finally {
            close();
        }
    }

    @Override
    public void close() {
        if (sc != null) sc.close();
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                // hide on closing
            }
        }
        service.shutdownNow();
    }
}

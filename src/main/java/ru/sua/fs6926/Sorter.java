package ru.sua.fs6926;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.BlockingDeque;

public interface Sorter<T> extends Closeable {
    void doSort(List<BlockingDeque<T>> blockingDeques);
}

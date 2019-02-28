package ru.sua.fs6926;

import java.util.concurrent.BlockingDeque;

public interface MediatorsSubscriber {
    void notifyEof(BlockingDeque<String> deque);
}

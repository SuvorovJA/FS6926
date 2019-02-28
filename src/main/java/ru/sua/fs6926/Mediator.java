package ru.sua.fs6926;

import java.util.concurrent.BlockingDeque;

public interface Mediator {
    void notifyEof(BlockingDeque<String> deque);
    void subscribe(MediatorsSubscriber subscriber);
}

package me.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TestConcurrentExecutor {

    public <T> List<Future<T>> executeWithResults(
        int threadCount,
        Callable<T> callable
    ) {
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        List<Future<T>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executorService.submit(callable));
        }

        return futures;
    }

    public void executeInMultiThread(
        int threadCount,
        Runnable action
    ) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    action.run();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
    }
}

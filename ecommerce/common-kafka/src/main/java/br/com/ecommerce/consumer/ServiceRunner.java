package br.com.ecommerce.consumer;

import java.util.concurrent.Executors;

public class ServiceRunner<T> {

    private final ServiceProvider<T> provider;

    public ServiceRunner(final ServiceFactory<T> factory) {
        this.provider = new ServiceProvider<T>(factory);
    }

    public void start(int threadCount) {
        var poll = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            poll.submit(provider);
        }
    }
}

package org.jmmo.datetime_callable;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DateTimeCallableConsumer extends Thread implements BiConsumer<Instant, Callable<?>> {
    private static Logger log = Logger.getLogger(DateTimeCallableConsumer.class.getName());

    protected final AtomicLong counter = new AtomicLong();
    protected final BlockingQueue<Task> queue = createDelayedQueue();

    @Override
    public void accept(Instant instant, Callable<?> callable) {
        final Task task = new Task(instant.toEpochMilli(), callable, counter.getAndIncrement());
        queue.add(task);

        log.fine(() -> "The task " + task + " was accepted");
    }

    @Override
    public void run() {
        while (true) {
            try {
                final Task task = queue.take();
                try {
                    log.fine(() -> "Starting the task " + task);

                    task.getCallable().call();

                    log.fine(() -> "Completed the task " + task);
                } catch (Exception e) {
                    log.log(Level.WARNING, "The task " + task + " cause exception", e);
                }
            } catch (InterruptedException e) {
                log.log(Level.WARNING, "DateTimeCallableConsumer was interrupted", e);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    protected DelayQueue<Task> createDelayedQueue() {
        return new DelayQueue<>();
    }

    public static class Task implements Delayed {
        final long executionTime;
        final Callable<?> callable;
        final long number;

        public Task(long executionTime, Callable<?> callable, long number) {
            this.executionTime = executionTime;
            this.callable = callable;
            this.number = number;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(getExecutionTime() - Instant.now().toEpochMilli(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            final Task other = (Task) o;
            final int timeCompare = Long.compare(getExecutionTime(), other.getExecutionTime());
            if (timeCompare != 0) {
                return timeCompare;
            }

            return Long.compare(getNumber(), other.getNumber());
        }

        public long getExecutionTime() {
            return executionTime;
        }

        public Callable<?> getCallable() {
            return callable;
        }

        public long getNumber() {
            return number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Task task = (Task) o;
            return getExecutionTime() == task.getExecutionTime() &&
                    getNumber() == task.getNumber();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getExecutionTime(), getNumber());
        }

        @Override
        public String toString() {
            return "Task{" +
                    "executionTime=" + executionTime +
                    ", number=" + number +
                    ", callable=" + callable +
                    '}';
        }
    }
}

package org.jmmo.datetime_callable;

import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class DateTimeCallableConsumer implements BiConsumer<Instant, Callable<?>> {

    @Override
    public void accept(Instant instant, Callable<?> callable) {

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
    }
}

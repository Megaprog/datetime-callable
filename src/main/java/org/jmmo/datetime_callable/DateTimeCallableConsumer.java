package org.jmmo.datetime_callable;

import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

public class DateTimeCallableConsumer implements BiConsumer<Instant, Callable<?>> {

    @Override
    public void accept(Instant instant, Callable<?> callable) {

    }
}

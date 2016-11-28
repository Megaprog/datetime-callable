package org.jmmo.datetime_callable;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class DateTimeCallableConsumerTest {

    private volatile Instant mockInstant = Instant.now();
    private AtomicInteger counter = new AtomicInteger();

    private DateTimeCallableConsumer consumer = new DateTimeCallableConsumer() {
        @Override
        protected Instant getInstant() {
            return mockInstant;
        }
    };

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Logger.getLogger("").setLevel(Level.FINE);
        Logger.getLogger("").getHandlers()[0].setLevel(Level.FINE);
    }

    @Before
    public void setUp() throws Exception {
        consumer.start();
    }

    @After
    public void tearDown() throws Exception {
        consumer.interrupt();
    }

    @Test
    public void test_Run_2nd() throws Exception {
        MockCallable mockCallable1 = new MockCallable();
        MockCallable mockCallable2 = new MockCallable();
        consumer.accept(mockInstant.plusMillis(2000), mockCallable1);
        consumer.accept(mockInstant, mockCallable2);

        Thread.sleep(100);

        assertEquals(0, mockCallable1.calls);
        assertEquals(1, mockCallable2.calls);
    }

    @Test
    public void test_Run_both() throws Exception {
        MockCallable mockCallable1 = new MockCallable();
        MockCallable mockCallable2 = new MockCallable();
        consumer.accept(mockInstant, mockCallable1);
        consumer.accept(mockInstant, mockCallable2);

        Thread.sleep(100);

        assertEquals(1, mockCallable1.calls);
        assertEquals(0, mockCallable1.order);
        assertEquals(1, mockCallable2.calls);
        assertEquals(1, mockCallable2.order);
    }

    @Test
    public void test_Run_with_exception() throws Exception {
        MockCallable mockCallable1 = new MockCallableWithException();
        MockCallable mockCallable2 = new MockCallable();
        consumer.accept(mockInstant, mockCallable1);
        consumer.accept(mockInstant, mockCallable2);

        Thread.sleep(100);

        assertEquals(1, mockCallable1.calls);
        assertEquals(0, mockCallable1.order);
        assertEquals(1, mockCallable2.calls);
        assertEquals(1, mockCallable2.order);
    }

    @Test
    public void test_Run_at_different_time() throws Exception {
        MockCallable mockCallable1 = new MockCallable();
        MockCallable mockCallable2 = new MockCallable();
        consumer.accept(mockInstant.plusMillis(200), mockCallable1);
        consumer.accept(mockInstant.plusMillis(100), mockCallable2);

        mockInstant = mockInstant.plusMillis(200);
        Thread.sleep(300);

        assertEquals(1, mockCallable2.calls);
        assertEquals(0, mockCallable2.order);
        assertEquals(1, mockCallable1.calls);
        assertEquals(1, mockCallable1.order);
    }

    private  class MockCallable implements Callable<Integer> {

        public int order = -1;
        public int calls = 0;

        @Override
        public Integer call() throws Exception {
            order = counter.getAndIncrement();
            return ++calls;
        }
    }

    private class MockCallableWithException extends MockCallable {
        @Override
        public Integer call() throws Exception {
            super.call();
            throw new RuntimeException("Mock exception");
        }
    }
}
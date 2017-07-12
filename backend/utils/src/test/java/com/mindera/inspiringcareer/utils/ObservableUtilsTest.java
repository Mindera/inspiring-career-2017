package com.mindera.inspiringcareer.utils;

import com.google.common.collect.Lists;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.functions.Action1;
import rx.functions.Func1;

import java.util.List;
import java.util.function.Supplier;

import static com.mindera.inspiringcareer.utils.ObservableUtils.asyncBlocking;
import static com.mindera.inspiringcareer.utils.ObservableUtils.fromAsync;
import static com.mindera.inspiringcareer.utils.ObservableUtils.fromAsyncBlocking;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(VertxUnitRunner.class)
public class ObservableUtilsTest {
    
    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    @Mock
    private Supplier<Integer> supplier;
    @Mock
    private Supplier<List<Integer>> listSupplier;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createFromNull() throws Exception {
        ObservableUtils.createFrom(() -> null)
                .subscribe(a -> fail(), e -> assertTrue(e instanceof NullPointerException));
    }

    @Test
    public void createFromException() throws Exception {
        ObservableUtils.createFrom(() -> {
            throw new IllegalArgumentException("error");
        })
                .subscribe(a -> fail(), e -> {
                    assertTrue(e instanceof IllegalArgumentException);
                    assertEquals("error", e.getMessage());
                });
    }

    @Test
    public void createFrom() throws Exception {
        ObservableUtils.createFrom(() -> "stuff")
                .subscribe(a -> assertEquals("stuff", a), e -> fail());
    }

    private void mockSupplier(final Integer t) {
        when(supplier.get()).thenReturn(t);
    }

    @Test
    public void asyncBlockingAction0(final TestContext ctx) {
        final Async async = ctx.async(2);

        mockSupplier(1);

        asyncBlocking(() -> {
                    System.out.println(supplier.get());
                    async.countDown();
                })
                .subscribe(a -> {
                    verify(supplier).get();
                    async.complete();
                }, ctx::fail);
    }

    @Test
    public void asyncAction0(final TestContext ctx) {
        final Async async = ctx.async(2);

        mockSupplier(2);

        ObservableUtils.async(() -> {
                    System.out.println(supplier.get());
                    async.countDown();
                })
                .subscribe(a -> {
                    verify(supplier).get();
                    async.complete();
                }, ctx::fail);
    }

    @Test
    public void asyncBlockingAction1(final TestContext ctx) {
        final Async async = ctx.async(2);

        mockSupplier(3);

        final Action1<Supplier<Integer>> action = s -> {
            System.out.println(s.get());
            async.countDown();
        };

        asyncBlocking(action, supplier)
                .subscribe(a -> {
                    verify(supplier).get();
                    async.complete();
                }, ctx::fail);
    }

    @Test
    public void asyncAction1(final TestContext ctx) {
        final Async async = ctx.async(2);

        mockSupplier(4);

        final Action1<Supplier<Integer>> action = s -> {
            System.out.println(s.get());
            async.countDown();
        };

        ObservableUtils.async(action, supplier)
                .subscribe(a -> {
                    verify(supplier).get();
                    async.complete();
                }, ctx::fail);
    }

    @Test
    public void asyncBlockingFunc0(final TestContext ctx) {
        final Async async = ctx.async(2);

        mockSupplier(5);

        asyncBlocking(() -> {
                    async.countDown();
                    return supplier.get();
                })
                .subscribe(result -> {
                    ctx.assertEquals(5, result);
                    verify(supplier).get();
                    async.complete();
                }, ctx::fail);
    }

    @Test
    public void asyncFunc0(final TestContext ctx) {
        final Async async = ctx.async(2);

        mockSupplier(6);

        ObservableUtils.async(() -> {
                    async.countDown();
                    return supplier.get();
                })
                .subscribe(result -> {
                    ctx.assertEquals(6, result);
                    verify(supplier).get();
                    async.complete();
                }, ctx::fail);
    }

    @Test
    public void asyncBlockingFunc1(final TestContext ctx) {
        final Async async = ctx.async(2);

        mockSupplier(7);

        final Func1<Supplier<Integer>, Integer> function = s -> {
            async.countDown();
            return s.get();
        };

        asyncBlocking(function, supplier)
                .subscribe(result -> {
                    ctx.assertEquals(7, result);
                    verify(supplier).get();
                    async.complete();
                }, ctx::fail);
    }

    @Test
    public void asyncFunc1(final TestContext ctx) {
        final Async async = ctx.async(2);

        mockSupplier(8);

        final Func1<Supplier<Integer>, Integer> function = s -> {
            async.countDown();
            return s.get();
        };

        ObservableUtils.async(function, supplier)
                .subscribe(result -> {
                    ctx.assertEquals(8, result);
                    verify(supplier).get();
                    async.complete();
                }, ctx::fail);
    }

    @Test
    public void asyncBlockingFunc2(final TestContext ctx) {
        final Async async = ctx.async(2);

        mockSupplier(9);

        asyncBlocking((a, b) -> {
                    async.countDown();
                    return a.get() + b.get();
                }, supplier, supplier)
                .subscribe(result -> {
                    ctx.assertEquals(18, result);
                    verify(supplier, times(2))
                            .get();
                    async.complete();
                }, ctx::fail);
    }

    @Test
    public void asyncFunc2(final TestContext ctx) {
        final Async async = ctx.async(2);

        mockSupplier(10);

        ObservableUtils.async((a, b) -> {
                    async.countDown();
                    return a.get() + b.get();
                }, supplier, supplier)
                .subscribe(result -> {
                    ctx.assertEquals(20, result);
                    verify(supplier, times(2))
                            .get();
                    async.complete();
                }, ctx::fail);
    }

    @Test
    public void asyncBlockingFunc3(final TestContext ctx) {
        final Async async = ctx.async(2);

        mockSupplier(11);

        asyncBlocking((a, b, c) -> {
                    async.countDown();
                    return a.get() + b.get() + c.get();
                }, supplier, supplier, supplier)
                .subscribe(result -> {
                    ctx.assertEquals(33, result);
                    verify(supplier, times(3))
                            .get();
                    async.complete();
                }, ctx::fail);
    }

    @Test
    public void asyncFunc3(final TestContext ctx) {
        final Async async = ctx.async(2);

        mockSupplier(12);

        ObservableUtils.async((a, b, c) -> {
                    async.countDown();
                    return a.get() + b.get() + c.get();
                }, supplier, supplier, supplier)
                .subscribe(result -> {
                    ctx.assertEquals(36, result);
                    verify(supplier, times(3))
                            .get();
                    async.complete();
                }, ctx::fail);
    }

    @Test
    public void asyncFromBlockingFunc1(final TestContext ctx) {
        final Async async = ctx.async(2);

        when(listSupplier.get())
                .thenReturn(Lists.newArrayList(1, 2, 3));

        fromAsyncBlocking(a -> {
                    async.countDown();
                    return a.get();
                }, listSupplier)
                .reduce(0, (a, b) -> a + b)
                .subscribe(result -> {
                    ctx.assertEquals(6, result);
                    verify(listSupplier).get();
                    async.complete();
                }, ctx::fail);
    }

    @Test
    public void asyncFromFunc1(final TestContext ctx) {
        final Async async = ctx.async(2);

        when(listSupplier.get())
                .thenReturn(Lists.newArrayList(1, 2, 3));

        fromAsync(a -> {
                    async.countDown();
                    return a.get();
                }, listSupplier)
                .reduce(0, (a, b) -> a + b)
                .subscribe(result -> {
                    ctx.assertEquals(6, result);
                    verify(listSupplier).get();
                    async.complete();
                }, ctx::fail);
    }
}

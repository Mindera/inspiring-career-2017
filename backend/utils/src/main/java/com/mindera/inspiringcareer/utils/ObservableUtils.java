package com.mindera.inspiringcareer.utils;

import rx.Observable;
import rx.Single;
import rx.functions.*;
import rx.schedulers.Schedulers;

import static rx.util.async.Async.toAsync;

public final class ObservableUtils {

    private ObservableUtils() {
    }

    public static <T> Observable<T> createFrom(final Func0<T> function) {
        return Observable.unsafeCreate(subscriber -> {
            try {
                final T call = function.call();
                if (call == null) {
                    throw new NullPointerException();
                }
                subscriber.onNext(call);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public static <T> Action1<T> emptyAction() {
        return action -> {
            //Empty action, useful for test cases
        };
    }

    public static <T> Func1<T, T> identity() {
        return t -> t;
    }

    public static Observable<Void> asyncBlocking(final Action0 action) {
        return toAsync(action, Schedulers.io()).call();
    }

    public static Observable<Void> async(final Action0 action) {
        return toAsync(action).call();
    }

    public static <T> Observable<Void> asyncBlocking(final Action1<T> action, final T parameter) {
        return toAsync(action, Schedulers.io()).call(parameter);
    }

    public static <T> Observable<Void> async(final Action1<T> action, final T parameter) {
        return toAsync(action).call(parameter);
    }

    public static <T> Observable<T> asyncBlocking(final Func0<T> function) {
        return toAsync(function, Schedulers.io()).call();
    }

    public static <T> Observable<T> asyncBlockingObs(final Func0<Observable<T>> function) {
        return toAsync(function, Schedulers.io()).call()
                .flatMap(identity());
    }

    public static <T> Observable<T> async(final Func0<T> function) {
        return toAsync(function).call();
    }

    public static <T, R> Observable<R> asyncBlocking(final Func1<T, R> function, final T parameter) {
        return toAsync(function, Schedulers.io())
                .call(parameter);
    }

    public static <T, R> Single<R> asyncBlockingSingle(final Func1<T, R> function, final T parameter) {
        return toAsync(function, Schedulers.io())
                .call(parameter)
                .toSingle();
    }

    public static <T, R> Observable<R> async(final Func1<T, R> function, final T parameter) {
        return toAsync(function)
                .call(parameter);
    }

    public static <T, E, R extends Iterable<E>> Observable<E> fromAsyncBlocking(final Func1<T, R> function, final T parameter) {
        return asyncBlocking(function, parameter)
                .flatMap(Observable::from);
    }

    public static <T, E, R extends Iterable<E>> Observable<E> fromAsync(final Func1<T, R> function, final T parameter) {
        return async(function, parameter)
                .flatMap(Observable::from);
    }

    public static <A, B, R> Observable<R> asyncBlocking(final Func2<A, B, R> function, final A param1, final B param2) {
        return toAsync(function, Schedulers.io())
                .call(param1, param2);
    }

    public static <A, B, R> Single<R> asyncBlockingSingle(final Func2<A, B, R> function, final A param1, final B param2) {
        return toAsync(function, Schedulers.io())
                .call(param1, param2)
                .toSingle();
    }

    public static <A, B, R> Observable<R> async(final Func2<A, B, R> function, final A param1, final B param2) {
        return toAsync(function)
                .call(param1, param2);
    }

    public static <A, B, C, R> Observable<R> asyncBlocking(final Func3<A, B, C, R> function, final A param1, final B param2, final C param3) {
        return toAsync(function, Schedulers.io())
                .call(param1, param2, param3);
    }

    public static <A, B, C, R> Single<R> asyncBlockingSingle(final Func3<A, B, C, R> function, final A param1, final B param2, final C param3) {
        return toAsync(function, Schedulers.io())
                .call(param1, param2, param3)
                .toSingle();
    }

    public static <A, B, C, R> Observable<R> asyncBlockingObs(final Func3<A, B, C, Observable<R>> function, final A param1, final B param2, final C param3) {
        return toAsync(function, Schedulers.io())
                .call(param1, param2, param3)
                .flatMap(identity());
    }

    public static <A, B, C, R> Observable<R> async(final Func3<A, B, C, R> function, final A param1, final B param2, final C param3) {
        return toAsync(function)
                .call(param1, param2, param3);
    }

    public static <A, B, C, D, R> Observable<R> asyncBlocking(final Func4<A, B, C, D, R> function, final A param1, final B param2, final C param3, final D param4) {
        return toAsync(function, Schedulers.io())
                .call(param1, param2, param3, param4);
    }
}

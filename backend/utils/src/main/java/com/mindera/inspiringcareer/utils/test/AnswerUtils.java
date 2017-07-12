package com.mindera.inspiringcareer.utils.test;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import org.mockito.ArgumentMatcher;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class AnswerUtils {

    private AnswerUtils() {
    }

    @SafeVarargs
    public static <T> ArgumentMatcher<List<T>> listThatContains(final T... values) {
        return object -> {
            if (object == null || !(object instanceof List)) {
                return false;
            }

            List list = (List) object;
            return Arrays.stream(values).allMatch(list::contains);
        };
    }

    @SafeVarargs
    public static <T, L extends Collection<T>> ArgumentMatcher<L> collectionThatContains(final T... values) {
        return object -> {
            if (object == null || !(object instanceof Collection)) {
                return false;
            }

            Collection collection = (Collection) object;
            return Arrays.stream(values).allMatch(collection::contains);
        };
    }

    @SafeVarargs
    public static <T, L extends Collection<T>> ArgumentMatcher<L> jsonObjectThatContains(final Map.Entry<String, String>... values) {
        return object -> {
            if (object == null || !(object instanceof JsonObject)) {
                return false;
            }

            JsonObject jsonObject = (JsonObject) object;
            return Stream.of(values).allMatch(value -> {
                final String key = value.getKey();
                final String objectValue = value.getValue();

                return jsonObject.containsKey(key) && jsonObject.getString(key).equals(objectValue);
            });
        };
    }

    public static <T> Answer succeededAsyncAnswer(final T value) {
        return invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Arrays.stream(arguments)
                    .filter(argument -> argument instanceof Handler)
                    .map(handler -> (Handler<AsyncResult<T>>) handler)
                    .findFirst()
                    .ifPresent(handler -> handler.handle(Future.succeededFuture(value)));
            return null;
        };
    }

    public static <T> Answer failedAsyncAnswer(final String value) {
        return invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Arrays.stream(arguments)
                    .filter(argument -> argument instanceof Handler)
                    .map(handler -> (Handler<AsyncResult<T>>) handler)
                    .findFirst()
                    .ifPresent(handler -> handler.handle(Future.failedFuture(value)));
            return null;
        };
    }
}

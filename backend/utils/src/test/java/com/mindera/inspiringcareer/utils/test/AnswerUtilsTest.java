package com.mindera.inspiringcareer.utils.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class AnswerUtilsTest {

    @Mock
    private Consumer<List<Integer>> listConsumer;

    @Mock
    private Consumer<Collection<Integer>> collectionConsumer;

    @Mock
    private Consumer<JsonObject> jsonConsumer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void listThatContains() {
        final List<Integer> list = Lists.newArrayList(1, 2, 3, 4);

        listConsumer.accept(list);

        verify(listConsumer).accept(argThat(AnswerUtils.listThatContains(1)));
        verify(listConsumer).accept(argThat(AnswerUtils.listThatContains(1, 2, 3, 4)));
        verify(listConsumer, never()).accept(argThat(AnswerUtils.listThatContains(5)));

        verify(listConsumer).accept(argThat(AnswerUtils.collectionThatContains(1)));
        verify(listConsumer).accept(argThat(AnswerUtils.collectionThatContains(1, 2, 3, 4)));
        verify(listConsumer, never()).accept(argThat(AnswerUtils.collectionThatContains(5)));
    }

    @Test
    public void emptyListThatContains() {
        final List<Integer> list = Lists.newArrayList();

        listConsumer.accept(list);

        verify(listConsumer, never()).accept(argThat(AnswerUtils.listThatContains(1, 2, 3, 4)));
        verify(listConsumer, never()).accept(argThat(AnswerUtils.listThatContains(5)));

        verify(listConsumer, never()).accept(argThat(AnswerUtils.collectionThatContains(1, 2, 3, 4)));
        verify(listConsumer, never()).accept(argThat(AnswerUtils.collectionThatContains(5)));
    }

    @Test
    public void collectionThatContains() {
        final Collection<Integer> list = Lists.newArrayList(1, 2, 3, 4);

        collectionConsumer.accept(list);

        verify(collectionConsumer).accept(argThat(AnswerUtils.collectionThatContains(1)));
        verify(collectionConsumer).accept(argThat(AnswerUtils.collectionThatContains(1, 2, 3, 4)));
        verify(collectionConsumer, never()).accept(argThat(AnswerUtils.collectionThatContains(5)));
    }

    @Test
    public void emptyCollectionThatContains() {
        final Collection<Integer> list = Lists.newArrayList();

        collectionConsumer.accept(list);

        verify(collectionConsumer, never()).accept(argThat(AnswerUtils.collectionThatContains(1, 2, 3, 4)));
        verify(collectionConsumer, never()).accept(argThat(AnswerUtils.collectionThatContains(5)));
    }

    @Test
    public void emptyJsonThatContains() {
        final JsonObject json = new JsonObject();

        jsonConsumer.accept(json);

        verify(jsonConsumer, never()).accept(argThat(AnswerUtils.jsonObjectThatContains(Maps.immutableEntry("anything", "anything"))));
    }

    @Test
    public void jsonThatContains() {
        final JsonObject json = new JsonObject()
                .put("stuff", "text")
                .put("other stuff", "something");

        jsonConsumer.accept(json);

        verify(jsonConsumer).accept(argThat(AnswerUtils.jsonObjectThatContains(Maps.immutableEntry("stuff", "text"))));
        verify(jsonConsumer).accept(argThat(AnswerUtils.jsonObjectThatContains(Maps.immutableEntry("other stuff", "something"))));
        verify(jsonConsumer)
                .accept(argThat(AnswerUtils.jsonObjectThatContains(Maps.immutableEntry("stuff", "text"), Maps.immutableEntry("other stuff", "something"))));
        verify(jsonConsumer, never()).accept(argThat(AnswerUtils.jsonObjectThatContains(Maps.immutableEntry("anything", "anything"))));
    }
}

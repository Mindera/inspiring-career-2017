package com.mindera.inspiringcareer.bootstrap;

import com.englishtown.vertx.guice.GuiceVerticleFactory;
import com.google.common.base.Stopwatch;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.mindera.inspiringcareer.guice.AppBootstrapBinder;
import com.mindera.inspiringcareer.guice.binders.RxVertxBinder;
import com.mindera.inspiringcareer.guice.binders.VertxBinder;
import com.mindera.inspiringcareer.guice.binders.VertxConfigBinder;
import com.mindera.inspiringcareer.utils.Constants;
import com.mindera.inspiringcareer.utils.Loggable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.buffer.Buffer;
import rx.Observable;
import rx.Single;
import rx.plugins.RxJavaHooks;
import rx.plugins.RxJavaSchedulersHook;

import java.util.concurrent.TimeUnit;

import static com.mindera.inspiringcareer.utils.JsonUtils.emptyJsonObject;

public class MainVerticle extends AbstractVerticle implements Loggable {

    private static final String ROUTES_CONFIG = "routes.json";
    private static final String VERTICLES_CONFIG = "verticles.json";

    @Override
    public void start(final Future<Void> start) {
        final Stopwatch stopwatch = Stopwatch.createStarted();

        overrideRxJavaSchedulers();

        final Single<JsonObject> routesConfig = readRoutesConfig();
        final Single<JsonArray> verticlesConfig = readVerticlesConfig();

        this.vertx.exceptionHandler(uncaughtException -> logger().error("Uncaught exception {}.", uncaughtException, uncaughtException.getClass()));

        routesConfig
                .flatMap(this::registerGuiceVerticleFactory)
                .zipWith(verticlesConfig, (factory, config) -> config)
                .flatMapObservable(Observable::from)
                .cast(JsonObject.class)
                .map(this::deployController)
                .flatMap(Single::toObservable)
                .toList()
                .doOnCompleted(() -> logger().info("Applications started. Time elapsed: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms."))
                .doOnError(e -> logger().error("Failed to start application.", e))
                .subscribe(result -> start.complete(), error -> start.fail(error.getMessage()));
    }

    private void overrideRxJavaSchedulers() {
        final RxJavaSchedulersHook rxJavaSchedulersHook = RxHelper.schedulerHook(vertx);
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> rxJavaSchedulersHook.getNewThreadScheduler());
        RxJavaHooks.setOnComputationScheduler(scheduler -> rxJavaSchedulersHook.getComputationScheduler());
        RxJavaHooks.setOnIOScheduler(scheduler -> rxJavaSchedulersHook.getIOScheduler());
    }

    private Single<JsonArray> readVerticlesConfig() {
        return vertx.fileSystem().rxReadFile(config().getString("verticles", VERTICLES_CONFIG))
                .map(Buffer::toJsonArray);
    }

    private Single<JsonObject> readRoutesConfig() {
        return vertx.fileSystem().rxReadFile(config().getString("routes", ROUTES_CONFIG))
                .map(Buffer::toJsonObject);
    }

    @SuppressWarnings("squid:UnusedPrivateMethod")
    private Single<String> deployController(final JsonObject controllerOptions) {
        final String name = controllerOptions.getString("name");
        final JsonObject poolInfo = controllerOptions.getJsonObject("pool", emptyJsonObject());

        final JsonObject verticleConfig = new JsonObject()
                .mergeIn(config())
                .mergeIn(poolInfo);

        final JsonObject verticleOptions = new JsonObject()
                .mergeIn(controllerOptions)
                .put(Constants.CONFIG, verticleConfig);

        final DeploymentOptions controllerDeploymentOptions = new DeploymentOptions(verticleOptions);

        return vertx.rxDeployVerticle(GuiceVerticleFactory.PREFIX + ':' + name, controllerDeploymentOptions)
                .doOnEach(a -> logger().trace("Deployed " + name))
                .doOnError(e -> logger().error("Failed to deploy {}", e, name));
    }

    @SuppressWarnings("squid:UnusedPrivateMethod")
    private Single<GuiceVerticleFactory> registerGuiceVerticleFactory(final JsonObject routes) {
        return this.vertx.rxExecuteBlocking(blocking -> {
            try {
                blocking.complete(registerGuiceVerticleFactoryBlocking(routes));
            } catch (Exception e) {
                logger().error("Failed to register GuiceVerticleFactory and set injector.", e);
                blocking.fail(e);
            }
        }, false);
    }

    private GuiceVerticleFactory registerGuiceVerticleFactoryBlocking(final JsonObject routes) {
        final GuiceVerticleFactory factory = this.getVertx().verticleFactories().stream()
                .filter(verticleFactory -> verticleFactory instanceof GuiceVerticleFactory)
                .map(GuiceVerticleFactory.class::cast)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("GuiceVerticleFactory is not registered."));

        final Module[] binders = new Module[]{
                new VertxConfigBinder(this.config(), routes),
                new VertxBinder(this.getVertx()),
                new RxVertxBinder(this.vertx),
                new AppBootstrapBinder()
        };

        final Injector currentInjector = factory.getInjector();

        final Injector newInjector = currentInjector == null
                ? Guice.createInjector(Stage.PRODUCTION, binders)
                : currentInjector.createChildInjector(binders);

        return factory.setInjector(newInjector);
    }
}

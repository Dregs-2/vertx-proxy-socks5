
package org.dregs.vertx.proxy;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import org.dregs.vertx.proxy.function.Block;

import java.util.function.UnaryOperator;

public class ExtendWriteStream<T> implements WriteStream<T> {

    private Vertx vertx;
    private WriteStream<T> target;
    private UnaryOperator<T> dataStreamOperator = UnaryOperator.identity();
    private boolean block = false;

    public static <T> ExtendWriteStream<T> create(Vertx vertx, WriteStream<T> target, UnaryOperator<T> dataStreamOperator) {
        ExtendWriteStream<T> ExtendWriteStream = new ExtendWriteStream<>();
        ExtendWriteStream.vertx = vertx;
        ExtendWriteStream.target = target;
        ExtendWriteStream.dataStreamOperator = dataStreamOperator;
        ExtendWriteStream.block = dataStreamOperator instanceof Block;
        return ExtendWriteStream;
    }


    public static <T> ExtendWriteStream<T> create(Vertx vertx, WriteStream<T> target) {
        return create(vertx, target, UnaryOperator.identity());
    }


    @Override
    public WriteStream<T> exceptionHandler(Handler<Throwable> handler) {
        return this.target.exceptionHandler(handler);
    }

    @Override
    public Future<Void> write(T data) {
        if (!block) {
            T writeData = dataStreamOperator.apply(data);
            if (null == writeData) {
                return Future.succeededFuture();
            }
            return this.target.write(writeData);
        }
        return vertx.executeBlocking(promise -> {
            T writeData = dataStreamOperator.apply(data);
            if (null == writeData) {
                promise.tryComplete(null);
                return;
            }
            target
                    .write(writeData)
                    .onSuccess(promise::tryComplete)
                    .onFailure(promise::tryFail);
        });
    }


    @Override
    public void write(T data, Handler<AsyncResult<Void>> handler) {
        this.write(data).onComplete(handler);
    }

    @Override
    public void end(Handler<AsyncResult<Void>> handler) {
        this.target.end(handler);
    }

    @Override
    public WriteStream<T> setWriteQueueMaxSize(int maxSize) {
        return this.target.setWriteQueueMaxSize(maxSize);
    }

    @Override
    public boolean writeQueueFull() {
        return this.target.writeQueueFull();
    }

    @Override
    public WriteStream<T> drainHandler(Handler<Void> handler) {
        return this.target.drainHandler(handler);
    }

    public Future<Void> pipeFrom(ReadStream<T> readStream) {
        return readStream.pipeTo(this);
    }

}

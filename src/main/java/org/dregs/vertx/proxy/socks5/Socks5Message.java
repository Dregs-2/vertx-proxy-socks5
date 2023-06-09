
package org.dregs.vertx.proxy.socks5;

import io.vertx.core.buffer.Buffer;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Socks5Message<M> {

    default byte version() {
        return 0x05;
    }


    Buffer toBuffer();

    M ofBuffer(Buffer buffer);

    default void consume(Consumer<Buffer> consumer) {
        consumer.accept(toBuffer());
    }

    default <T> T apply(Function<Buffer, T> function) {
        return function.apply(toBuffer());
    }
}


package org.dregs.vertx.proxy.operator;

import io.vertx.core.buffer.Buffer;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;

@Getter
@Accessors(fluent = true)
public class CountOperator implements UnaryOperator<Buffer> {

    private final String id;

    private final String type;

    private final AtomicLong quantity = new AtomicLong();


    public CountOperator(String id, String type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public Buffer apply(Buffer buffer) {
        if (null != buffer) {
            quantity.addAndGet(buffer.length());
            System.out.printf("[%s] - [%s] - [%s]%n", id, type, quantity.get());
        }
        return buffer;
    }
}


package org.dregs.vertx.proxy;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

import java.util.function.UnaryOperator;

public interface ProxyServerHandleExtend {

    boolean initialized();

    void preHandle(Buffer buffer);

    Future<Void> connectedHandle();

    void connectFailureHandle(Throwable throwable);

    String connectAddr();

    int connectPort();

    default UnaryOperator<Buffer> inboundDataStreamOperator() {
        return UnaryOperator.identity();
    }

    default UnaryOperator<Buffer> outboundDataStreamOperator() {
        return UnaryOperator.identity();
    }


}

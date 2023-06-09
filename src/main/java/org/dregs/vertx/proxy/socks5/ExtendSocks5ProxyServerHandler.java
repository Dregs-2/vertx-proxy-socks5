
package org.dregs.vertx.proxy.socks5;

import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dregs.vertx.proxy.socks5.authentication.Socks5Authentication;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Setter(AccessLevel.PROTECTED)
@Accessors(fluent = true, chain = true)
public class ExtendSocks5ProxyServerHandler extends Socks5ProxyServerHandler {

    private Supplier<UnaryOperator<Buffer>> inboundDataStreamOperatorSupplier;
    private Supplier<UnaryOperator<Buffer>> outboundDataStreamOperatorSupplier;


    public static ExtendSocks5ProxyServerHandler createExtendSocks5ProxyServerHandler(
            Vertx vertx, NetSocket netSocket,
            Supplier<UnaryOperator<Buffer>> inboundDataStreamOperatorSupplier,
            Supplier<UnaryOperator<Buffer>> outboundDataStreamOperatorSupplier) {
        ExtendSocks5ProxyServerHandler extendSocks5ProxyServerHandler = new ExtendSocks5ProxyServerHandler();
        extendSocks5ProxyServerHandler
                .inboundDataStreamOperatorSupplier(inboundDataStreamOperatorSupplier)
                .outboundDataStreamOperatorSupplier(outboundDataStreamOperatorSupplier)
                .vertx(vertx)
                .netSocket(netSocket)
                .authRequired(false);
        return extendSocks5ProxyServerHandler;
    }

    public static ExtendSocks5ProxyServerHandler createExtendSocks5ProxyServerHandler(
            Vertx vertx, NetSocket netSocket, Map<Socks5AuthMethod, Socks5Authentication> socks5AuthenticationMap,
            Supplier<UnaryOperator<Buffer>> inboundDataStreamOperatorSupplier,
            Supplier<UnaryOperator<Buffer>> outboundDataStreamOperatorSupplier) {
        ExtendSocks5ProxyServerHandler extendSocks5ProxyServerHandler = new ExtendSocks5ProxyServerHandler();
        extendSocks5ProxyServerHandler
                .inboundDataStreamOperatorSupplier(inboundDataStreamOperatorSupplier)
                .outboundDataStreamOperatorSupplier(outboundDataStreamOperatorSupplier)
                .vertx(vertx)
                .netSocket(netSocket)
                .socks5AuthenticationMap(Objects.requireNonNull(Collections.unmodifiableMap(socks5AuthenticationMap)))
                .authRequired(true);
        return extendSocks5ProxyServerHandler;
    }

    @Override
    public UnaryOperator<Buffer> inboundDataStreamOperator() {
        return this.inboundDataStreamOperatorSupplier.get();
    }

    @Override
    public UnaryOperator<Buffer> outboundDataStreamOperator() {
        return this.outboundDataStreamOperatorSupplier.get();
    }
}

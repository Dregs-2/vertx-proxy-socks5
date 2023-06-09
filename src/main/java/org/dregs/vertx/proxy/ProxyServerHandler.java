
package org.dregs.vertx.proxy;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.util.function.Function;


public class ProxyServerHandler implements Handler<Buffer> {

    private Vertx vertx;

    private NetClient netClient;

    private NetSocket netSocket;

    private ProxyServerHandleExtend proxyServerHandleExtend;

    private boolean connected;

    public static Handler<NetSocket> createProxyServerConnectHandler(
            Vertx vertx,
            NetClient netClient,
            Function<NetSocket, ProxyServerHandleExtend> proxyServerHandleFunction) {
        return netSocket -> {
            ProxyServerHandler proxyServerHandler = new ProxyServerHandler();
            proxyServerHandler.vertx = vertx;
            proxyServerHandler.netSocket = netSocket;
            proxyServerHandler.netClient = netClient;
            proxyServerHandler.proxyServerHandleExtend = proxyServerHandleFunction.apply(netSocket);
            netSocket.handler(proxyServerHandler);
        };
    }


    @Override
    public void handle(Buffer buffer) {
        if (!proxyServerHandleExtend.initialized()) {
            proxyServerHandleExtend.preHandle(buffer);
        }
        if (!proxyServerHandleExtend.initialized()) {
            return;
        }
        if (connected) {
            return;
        }

        String addr = proxyServerHandleExtend.connectAddr();
        int port = proxyServerHandleExtend.connectPort();
        netClient
                .connect(port, addr)
                .onFailure(proxyServerHandleExtend::connectFailureHandle)
                .onSuccess(socket -> {
                    connected = true;
                    netSocket.closeHandler($ -> socket.close());
                    socket.closeHandler($ -> netSocket.close());
                    proxyServerHandleExtend
                            .connectedHandle()
                            .onSuccess($ -> {
                                ExtendWriteStream
                                        .create(vertx, socket, proxyServerHandleExtend.outboundDataStreamOperator())
                                        .pipeFrom(netSocket);
                                ExtendWriteStream
                                        .create(vertx, netSocket, proxyServerHandleExtend.inboundDataStreamOperator())
                                        .pipeFrom(socket);
                            })
                            .onFailure($ -> {
                                netSocket.close();
                                socket.close();
                            });
                });
    }

}


package org.dregs.vertx.proxy;

import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import org.dregs.vertx.proxy.operator.CountOperator;
import org.dregs.vertx.proxy.socks5.ExtendSocks5ProxyServerHandler;
import org.dregs.vertx.proxy.socks5.authentication.Socks5Authentication;
import org.dregs.vertx.proxy.socks5.authentication.Socks5MemoryPasswordAuthentication;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class Boot {

    public static void main(String[] args) {

        Properties properties = new Properties();
        try {
            properties.load(Boot.class.getResourceAsStream("/password.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Vertx vertx = Vertx.vertx();

        NetClient netClient = vertx.createNetClient(new NetClientOptions().setLogActivity(true));
        NetServer netServer = vertx.createNetServer(new NetServerOptions().setLogActivity(true));

        Map<Socks5AuthMethod, Socks5Authentication> socks5AuthenticationMap = Map.of(Socks5AuthMethod.PASSWORD, new Socks5MemoryPasswordAuthentication(properties));

        Handler<NetSocket> proxyServerHandler = ProxyServerHandler.createProxyServerConnectHandler(
                vertx,
                netClient,
                netSocket -> ExtendSocks5ProxyServerHandler.createExtendSocks5ProxyServerHandler(
                        vertx,
                        netSocket,
                        //socks5AuthenticationMap,
                        //^^^ delete the comment to turn on authentication
                        () -> new CountOperator(netSocket.remoteAddress().toString(), "IN"),
                        () -> new CountOperator(netSocket.remoteAddress().toString(), "OUT")));

        netServer
                .connectHandler(proxyServerHandler)
                .exceptionHandler(Throwable::printStackTrace)
                .listen(1080)
                .onFailure(Throwable::printStackTrace)
                .result();
    }

}

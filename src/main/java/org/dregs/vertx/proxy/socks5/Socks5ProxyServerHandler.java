
package org.dregs.vertx.proxy.socks5;

import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dregs.vertx.proxy.ProxyServerHandleExtend;
import org.dregs.vertx.proxy.socks5.authentication.Socks5Authentication;
import org.dregs.vertx.proxy.socks5.request.Socks5CommandRequest;
import org.dregs.vertx.proxy.socks5.request.Socks5InitialRequest;
import org.dregs.vertx.proxy.socks5.response.Socks5CommandResponse;
import org.dregs.vertx.proxy.socks5.response.Socks5InitialResponse;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Setter(AccessLevel.PROTECTED)
@Accessors(fluent = true, chain = true)
public class Socks5ProxyServerHandler implements ProxyServerHandleExtend {


    private Map<Socks5AuthMethod, Socks5Authentication> socks5AuthenticationMap;

    //region authentication
    private Socks5AuthMethod socks5AuthMethod = Socks5AuthMethod.UNACCEPTED;
    //endregion

    //region initialize state
    private static final int INITIAL = 0;
    private static final int AUTH = 1;
    private static final int COMMAND = 2;

    private int state;

    private void state(int state) {
        this.state = state;
    }
    //endregion


    //region internal variables
    private boolean initialized = false;

    private String remoteAddr;

    private int remotePort;
    //endregion

    private Vertx vertx;

    private NetSocket netSocket;

    private boolean authRequired;

    public static Socks5ProxyServerHandler createSocks5ProxyServerHandler(Vertx vertx, NetSocket netSocket) {
        return new Socks5ProxyServerHandler()
                .vertx(vertx)
                .netSocket(netSocket)
                .authRequired(false);
    }

    public static Socks5ProxyServerHandler createSocks5ProxyServerHandler(Vertx vertx, NetSocket netSocket, Map<Socks5AuthMethod, Socks5Authentication> socks5AuthenticationMap) {
        return new Socks5ProxyServerHandler()
                .vertx(vertx)
                .netSocket(netSocket)
                .socks5AuthenticationMap(Objects.requireNonNull(Collections.unmodifiableMap(socks5AuthenticationMap)))
                .authRequired(true);
    }

    @Override
    public boolean initialized() {
        return this.initialized;
    }

    @Override
    public void preHandle(Buffer buffer) {
        switch (state) {
            case INITIAL:
                initialHandle(buffer);
                break;
            case AUTH:
                authHandle(buffer);
                break;
            case COMMAND:
                commandHandle(buffer);
                break;
            default:
                break;
        }
    }

    @Override
    public Future<Void> connectedHandle() {
        return Socks5CommandResponse
                .create()
                .bndAddrType(Socks5AddressType.IPv4)
                .bndAddr(null)
                .bndPort(0)
                .status(Socks5CommandStatus.SUCCESS)
                .apply(netSocket::write);
    }

    @Override
    public void connectFailureHandle(Throwable throwable) {
        throwable.printStackTrace();
        this.netSocket.close();
    }

    @Override
    public String connectAddr() {
        return this.remoteAddr;
    }

    @Override
    public int connectPort() {
        return this.remotePort;
    }


    private void initialHandle(Buffer buffer) {
        Socks5InitialRequest socks5InitialRequest = Socks5InitialRequest
                .create()
                .ofBuffer(buffer);
        if (!this.authRequired) {
            Socks5InitialResponse
                    .create()
                    .authMethod(Socks5AuthMethod.NO_AUTH)
                    .apply(netSocket::write);
            state(COMMAND);
            return;
        }
        List<Socks5AuthMethod> socks5AuthMethods = socks5InitialRequest.authMethods();

        if (null != socks5AuthMethods && !socks5AuthMethods.isEmpty()) {
            for (Socks5AuthMethod authMethod : socks5AuthMethods) {
                if (socks5AuthenticationMap.containsKey(authMethod)) {
                    socks5AuthMethod = authMethod;
                    break;
                }
            }
        }

        Socks5InitialResponse
                .create()
                .authMethod(socks5AuthMethod)
                .apply(netSocket::write);

        state(AUTH);
        //if(Socks5AuthMethod.UNACCEPTED.equals(this.socks5AuthMethod)){
        //    netSocket.close();
        //}
    }


    private void authHandle(Buffer buffer) {
        socks5AuthenticationMap
                .get(socks5AuthMethod)
                .authentication(netSocket, buffer)
                .onSuccess($ -> state(COMMAND));
    }

    private void commandHandle(Buffer buffer) {
        Socks5CommandRequest socks5CommandRequest = Socks5CommandRequest
                .create()
                .ofBuffer(buffer);
        this.remoteAddr = socks5CommandRequest.dstAddr();
        this.remotePort = socks5CommandRequest.dstPort();
        this.initialized = true;
    }
}

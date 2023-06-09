
package org.dregs.vertx.proxy.socks5.authentication;

import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthStatus;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import org.dregs.vertx.proxy.socks5.request.Socks5PasswordAuthRequest;
import org.dregs.vertx.proxy.socks5.response.Socks5PasswordAuthResponse;

@FunctionalInterface
public interface Socks5PasswordAuthentication extends Socks5Authentication {

    default Future<Void> authentication(NetSocket netSocket, Buffer buffer) {
        Socks5PasswordAuthRequest socks5PasswordAuthRequest = Socks5PasswordAuthRequest
                .create()
                .ofBuffer(buffer);

        String username = socks5PasswordAuthRequest.username();
        String password = socks5PasswordAuthRequest.password();
        Promise<Void> promise = Promise.promise();
        verify(username, password)
                .onFailure(promise::fail)
                .onSuccess(b ->
                        Socks5PasswordAuthResponse
                                .create()
                                .status(Boolean.TRUE.equals(b) ? Socks5PasswordAuthStatus.SUCCESS : Socks5PasswordAuthStatus.FAILURE)
                                .apply(netSocket::write)
                                .onSuccess(promise::tryComplete)
                                .onFailure(promise::fail)
                );
        return promise.future();
    }

    Future<Boolean> verify(String username, String password);
}

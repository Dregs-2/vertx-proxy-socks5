
package org.dregs.vertx.proxy.socks5.authentication;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

@FunctionalInterface
public interface Socks5Authentication {

    Future<Void> authentication(NetSocket netSocket, Buffer buffer);

}

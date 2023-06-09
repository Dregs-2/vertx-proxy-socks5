
package org.dregs.vertx.proxy.socks5.request;

import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.vertx.core.buffer.Buffer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dregs.vertx.proxy.socks5.Socks5Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
@NoArgsConstructor(staticName = "create")
public class Socks5InitialRequest implements Socks5Message<Socks5InitialRequest> {

    private List<Socks5AuthMethod> authMethods;

    @Override
    public Buffer toBuffer() {
        if (null == this.authMethods || this.authMethods.isEmpty()) {
            this.authMethods = List.of(Socks5AuthMethod.NO_AUTH);
        }

        if (Byte.MAX_VALUE * 2 < authMethods.size()) {
            this.authMethods = this.authMethods.subList(0, Byte.MAX_VALUE * 2);
        }

        Buffer buffer = Buffer
                .buffer()
                .appendByte(version())
                .appendByte((byte) this.authMethods.size());
        this.authMethods
                .stream()
                .map(Socks5AuthMethod::byteValue)
                .forEach(buffer::appendByte);
        return buffer;
    }

    @Override
    public Socks5InitialRequest ofBuffer(Buffer buffer) {
        short methods = buffer.getUnsignedByte(1);
        List<Socks5AuthMethod> authMethods = new ArrayList<>();
        for (byte method : buffer.getBytes(2, methods + 2)) {
            authMethods.add(Socks5AuthMethod.valueOf(method));
        }
        this.authMethods = Collections.unmodifiableList(authMethods);
        return this;
    }

}

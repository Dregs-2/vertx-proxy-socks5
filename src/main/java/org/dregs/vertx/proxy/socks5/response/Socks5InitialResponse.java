
package org.dregs.vertx.proxy.socks5.response;

import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.vertx.core.buffer.Buffer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dregs.vertx.proxy.socks5.Socks5Message;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
@NoArgsConstructor(staticName = "create")
public class Socks5InitialResponse implements Socks5Message<Socks5InitialResponse> {

    private Socks5AuthMethod authMethod;

    @Override
    public Buffer toBuffer() {
        return Buffer
                .buffer()
                .appendByte(version())
                .appendByte(authMethod.byteValue());
    }

    @Override
    public Socks5InitialResponse ofBuffer(Buffer buffer) {
        this.authMethod = Socks5AuthMethod.valueOf(buffer.getByte(1));
        return null;
    }
}

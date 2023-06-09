
package org.dregs.vertx.proxy.socks5.response;

import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthStatus;
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
public class Socks5PasswordAuthResponse implements Socks5Message<Socks5PasswordAuthResponse> {

    private Socks5PasswordAuthStatus status;

    @Override
    public Buffer toBuffer() {
        return Buffer
                .buffer()
                .appendByte(version())
                .appendByte(this.status.byteValue());
    }

    @Override
    public Socks5PasswordAuthResponse ofBuffer(Buffer buffer) {
        this.status = Socks5PasswordAuthStatus.valueOf(buffer.getByte(1));
        return this;
    }
}

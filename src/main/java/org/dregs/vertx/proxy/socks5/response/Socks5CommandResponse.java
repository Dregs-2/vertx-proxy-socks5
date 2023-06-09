
package org.dregs.vertx.proxy.socks5.response;

import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
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
public class Socks5CommandResponse implements Socks5Message<Socks5CommandResponse> {

    private Socks5CommandStatus status;

    private Socks5AddressType bndAddrType;

    private String bndAddr;

    private int bndPort;

    @Override
    public Buffer toBuffer() {
        Buffer buffer = Buffer
                .buffer()
                .appendByte(version())
                .appendByte(this.status.byteValue())
                .appendByte((byte) 0)
                .appendByte(this.bndAddrType.byteValue());
        if (null != this.bndAddr) {
            buffer.appendString(this.bndAddr);
        } else {
            buffer.appendInt(0);
        }
        buffer.appendShort((short) this.bndPort);
        return buffer;
    }

    @Override
    public Socks5CommandResponse ofBuffer(Buffer buffer) {
        this.status = Socks5CommandStatus.valueOf(buffer.getByte(1));
        this.bndAddrType = Socks5AddressType.valueOf(buffer.getByte(3));
        this.bndAddr = buffer.getString(4, buffer.length() - 2);
        this.bndPort = buffer.getShort(buffer.length() - 2);
        return this;
    }
}

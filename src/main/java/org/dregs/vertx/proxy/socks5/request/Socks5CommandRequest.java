
package org.dregs.vertx.proxy.socks5.request;

import io.netty.handler.codec.socksx.v5.Socks5AddressDecoder;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5CommandType;
import io.vertx.core.buffer.Buffer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.dregs.vertx.proxy.socks5.Socks5Message;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
@NoArgsConstructor(staticName = "create")
public class Socks5CommandRequest implements Socks5Message<Socks5CommandRequest> {

    private Socks5CommandType type;

    private Socks5AddressType dstAddrType;

    private String dstAddr;

    private int dstPort;


    @Override
    public Buffer toBuffer() {
        return Buffer
                .buffer()
                .appendByte(version())
                .appendByte(this.type.byteValue())
                .appendByte((byte) 0)
                .appendByte(this.dstAddrType.byteValue())
                .appendString(this.dstAddr)
                .appendShort((short) this.dstPort);
    }

    @Override
    @SneakyThrows
    public Socks5CommandRequest ofBuffer(Buffer buffer) {
        Socks5CommandType cmdType = Socks5CommandType.valueOf(buffer.getByte(1));
        Socks5AddressType addrType = Socks5AddressType.valueOf(buffer.getByte(3));
        int port = buffer.getUnsignedShort(buffer.length() - 2);
        String addr = Socks5AddressDecoder.DEFAULT.decodeAddress(addrType, buffer.getByteBuf().skipBytes(4));
        this.type = cmdType;
        this.dstAddrType = addrType;
        this.dstAddr = addr;
        this.dstPort = port;
        return this;
    }
}


package org.dregs.vertx.proxy.socks5.request;

import io.vertx.core.buffer.Buffer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dregs.vertx.proxy.exception.StringTooLongException;
import org.dregs.vertx.proxy.socks5.Socks5Message;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
@NoArgsConstructor(staticName = "create")
public class Socks5PasswordAuthRequest implements Socks5Message<Socks5PasswordAuthRequest> {

    private String username;

    private String password;

    @Override
    public Buffer toBuffer() {
        if (null == this.username) {
            this.username = "";
        }
        if (null == this.password) {
            this.password = "";
        }
        long max = Byte.MAX_VALUE * 2;
        if (this.username.length() > max) {
            throw new StringTooLongException("username", max, this.username.length(), this.username);
        }
        if (this.password.length() > max) {
            throw new StringTooLongException("password", max, this.password.length(), "...");
        }

        return Buffer
                .buffer()
                .appendByte(version())
                .appendByte((byte) this.username.length())
                .appendString(this.username)
                .appendByte((byte) this.password.length())
                .appendString(this.password);
    }

    @Override
    public Socks5PasswordAuthRequest ofBuffer(Buffer buffer) {
        short usernameLength = buffer.getUnsignedByte(1);
        String username = buffer.getString(2, 2 + usernameLength);
        String password = buffer.getString(3 + usernameLength, buffer.length());
        this.username = username;
        this.password = password;
        return this;
    }

}

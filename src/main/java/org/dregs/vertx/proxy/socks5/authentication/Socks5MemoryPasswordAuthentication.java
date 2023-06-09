package org.dregs.vertx.proxy.socks5.authentication;

import io.vertx.core.Future;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Socks5MemoryPasswordAuthentication implements Socks5PasswordAuthentication {

    private Map<String, String> userInfoMap;

    public Socks5MemoryPasswordAuthentication(Map<String, String> map) {
        this.userInfoMap = Collections.unmodifiableMap(Objects.requireNonNull(map));
    }

    public Socks5MemoryPasswordAuthentication(Properties properties) {
        Map<String, String> map = properties
                .stringPropertyNames()
                .stream()
                .collect(Collectors.toMap(Function.identity(), properties::getProperty));
        this.userInfoMap = Collections.unmodifiableMap(Objects.requireNonNull(map));
    }

    @Override
    public Future<Boolean> verify(String username, String password) {
        Boolean result = Optional
                .of(username)
                .map(userInfoMap::get)
                .map(p -> Objects.equals(p, password))
                .orElse(Boolean.FALSE);
        return Future.succeededFuture(result);
    }
}

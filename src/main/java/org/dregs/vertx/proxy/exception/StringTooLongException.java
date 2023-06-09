package org.dregs.vertx.proxy.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StringTooLongException extends RuntimeException {

    private String fieldName;

    private long max;

    private long length;

    private String string;


}

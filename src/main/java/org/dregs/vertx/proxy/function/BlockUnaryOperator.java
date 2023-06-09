
package org.dregs.vertx.proxy.function;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface BlockUnaryOperator<T> extends UnaryOperator<T>, Block {

    static <T> BlockUnaryOperator<T> identity() {
        return t -> t;
    }


    static <T> BlockUnaryOperator<T> block(UnaryOperator<T> unaryOperator) {
        return unaryOperator::apply;
    }

}

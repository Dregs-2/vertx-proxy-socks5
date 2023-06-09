
package org.dregs.vertx.proxy.function;

import java.util.function.Predicate;

@FunctionalInterface
public interface BlockPredicate<T> extends Predicate<T>, Block {


    static <T> BlockPredicate<T> block(Predicate<T> predicate) {
        return predicate::test;
    }

}

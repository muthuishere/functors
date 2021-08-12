package com.deemwar.functors.lazy;

import com.deemwar.functors.interfaces.BiFunctionWithException;
import com.deemwar.functors.interfaces.FunctionWithException;
import com.deemwar.functors.interfaces.SupplierWithException;
import com.deemwar.functors.interfaces.TriFunctionWithException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TryLazy<I1, I2, I3, O, E extends Throwable> {

    public TryLazy(List<Object> methods) {
        Objects.requireNonNull(methods);

        this.methods = methods;
    }

     List<Object> methods;

    public static <L, U extends Throwable> TryLazyBlock<L> from(SupplierWithException<L, U> method) {
        Objects.requireNonNull(method);
        TryLazyBlock lazyTry = new TryLazyBlock();
        lazyTry.or(method);

        return lazyTry;
    }

    public TryLazyBlock<O> with(I1 arg1, I2 arg2, I3 arg3) {
        return methods.stream()
                .peek(Objects::requireNonNull)
                .map(obj -> (TriFunctionWithException<I1, I2, I3, O, E>) obj)
                .map(ioeFunctionWithException -> getSupplierWithException(ioeFunctionWithException, arg1, arg2, arg3))
                .reduce(new TryLazyBlock(), (tryLazyBlock, oeSupplierWithException) -> tryLazyBlock.or(oeSupplierWithException),null);
    }
    public TryLazyBlock<O> with(I1 arg1, I2 arg2) {
        return methods.stream()
                .peek(Objects::requireNonNull)
                .map(obj -> (BiFunctionWithException<I1, I2,  O, E>) obj)
                .map(ioeFunctionWithException -> getSupplierWithException(ioeFunctionWithException, arg1, arg2))
                .reduce(new TryLazyBlock(), (tryLazyBlock, oeSupplierWithException) -> tryLazyBlock.or(oeSupplierWithException),null);
    }

    public TryLazyBlock<O> with(I1 arg1) {
        return methods.stream()
                .peek(Objects::requireNonNull)
                .map(obj -> (FunctionWithException<I1, O, E>) obj)
                .map(ioeFunctionWithException -> getSupplierWithException(ioeFunctionWithException, arg1))
                .reduce(new TryLazyBlock(), (tryLazyBlock, oeSupplierWithException) -> tryLazyBlock.or(oeSupplierWithException),((tryLazyBlock, tryLazyBlock2) -> tryLazyBlock));

    }


    public SupplierWithException<O, E> getSupplierWithException(TriFunctionWithException<I1, I2, I3, O, E> ioeFunctionWithException, I1 arg1, I2 arg2, I3 arg3) {
        return () -> ioeFunctionWithException.apply(arg1, arg2, arg3);
    }

    public SupplierWithException<O, E> getSupplierWithException(BiFunctionWithException<I1, I2, O, E> ioeFunctionWithException, I1 arg1, I2 arg2) {
        return () -> ioeFunctionWithException.apply(arg1, arg2);
    }

    public SupplierWithException<O, E> getSupplierWithException(FunctionWithException<I1,  O, E> ioeFunctionWithException, I1 arg1) {
        return () -> ioeFunctionWithException.apply(arg1);
    }

    public static <I1, I2, I3, O, E extends Throwable> TryLazy<I1, I2, I3, O, E> any(FunctionWithException<I1, O, E>... methods) {

        return new TryLazy(Arrays.asList(methods));

    }

}
package com.warm.flow.core;

import org.junit.Test;

public class FlowTest1 {

    @Test
    public void configuration() throws InterruptedException {
        int n = 0;
        while (true) {
            n = b(n);
            n = c(n);
            Thread.sleep(20);
        }
    }

    public int b(int n) {
        System.out.println("b" + n);
        ++n;
        return n;
    }

    public int c(int n) {
        System.out.println("c" + n);
        ++n;
        return n;
    }
}

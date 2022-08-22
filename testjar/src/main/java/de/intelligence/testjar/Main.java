package de.intelligence.testjar;

import java.io.IOException;

/**
 * @author Heinrich Töpfer (heinrich.toepfer@uni-oldenburg.de)
 */
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        var obj = new TestClass();
        ((Runnable) obj::inc).run();
        try {
            obj.throwException();
        } catch (TestException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(obj.loadFile().length());
    }
}
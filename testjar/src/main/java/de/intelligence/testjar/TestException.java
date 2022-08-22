package de.intelligence.testjar;

/**
 * @author Heinrich Töpfer (heinrich.toepfer@uni-oldenburg.de)
 */
public class TestException extends Exception {

    public TestException(String message) {
        super(message);
        System.out.println("TEST");
    }
}

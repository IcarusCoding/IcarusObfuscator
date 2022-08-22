package de.intelligence.testjar;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Heinrich Töpfer (heinrich.toepfer@uni-oldenburg.de)
 */
public class TestClass {

    private Integer i = 0;

    public void inc() {
        i++;
    }

    public void throwException() throws TestException {
        throw new TestException("Test");
    }

    public String loadFile() throws IOException {
        // read file dummy.txt from resources
        ClassLoader classLoader = getClass().getClassLoader();
        return new String(Objects.requireNonNull(classLoader.getResourceAsStream("dummy.txt")).readAllBytes());
    }

}

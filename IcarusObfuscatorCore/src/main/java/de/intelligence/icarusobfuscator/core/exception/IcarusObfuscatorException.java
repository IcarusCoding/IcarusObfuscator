package de.intelligence.icarusobfuscator.core.exception;

public class IcarusObfuscatorException extends RuntimeException {

    public IcarusObfuscatorException(String message) {
        super(message);
    }

    public IcarusObfuscatorException(String message, Throwable cause) {
        super(message, cause);
    }

}

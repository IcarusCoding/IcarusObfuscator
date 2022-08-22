package de.intelligence.icarusobfuscator.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IIcarusObfuscator {

    Logger LOG = LogManager.getLogger(IIcarusObfuscator.class);

    void obfuscate();

}

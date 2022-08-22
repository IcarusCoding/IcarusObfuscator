package de.intelligence.icarusobfuscator.core.utils;

/**
 * Get the java version major version.
 *
 * @author Heinrich Töpfer (heinrich.toepfer@uni-oldenburg.de)
 */
public enum JavaVersion {

    JAVA_1_1(45),
    JAVA_1_2(46),
    JAVA_1_3(47),
    JAVA_1_4(48),
    JAVA_5(49),
    JAVA_6(50),
    JAVA_7(51),
    JAVA_8(52),
    JAVA_9(53),
    JAVA_10(54),
    JAVA_11(55),
    JAVA_12(56),
    JAVA_13(57),
    JAVA_14(58),
    JAVA_15(59),
    JAVA_16(60),
    JAVA_17(61),
    JAVA_18(62),
    JAVA_19(63);

    private final int majorVersion;

    JavaVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

}
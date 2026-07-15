package com.badrulamin.University_Management.exception;

public class FeatureDisabledException extends RuntimeException {
    private final String featureKey;

    public FeatureDisabledException(String featureKey) {
        super("This feature has been disabled by the Super Admin.");
        this.featureKey = featureKey;
    }

    public FeatureDisabledException(String featureKey, String message) {
        super(message);
        this.featureKey = featureKey;
    }

    public String getFeatureKey() {
        return featureKey;
    }
}

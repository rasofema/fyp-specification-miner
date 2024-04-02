package org.example.helper;

public enum SupportedLearningAlgorithm {
    DFA_ClassicLStar,
    DFA_ExtensibleLStar,
    DFA_KV,
    DFA_TTT,
    RaDT,
    RaLambda,
    RaStar;

    public static boolean isRA(SupportedLearningAlgorithm value) {
        return value == RaDT || value == RaLambda || value == RaStar;
    }

    public static boolean isDFA(SupportedLearningAlgorithm value) {
        return value == DFA_ClassicLStar || value == DFA_ExtensibleLStar || value == DFA_KV || value == DFA_TTT;
    }
}

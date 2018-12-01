package core;

public enum MessageCode {
    PROVER_SENDING_NEW_BLOCK,
    PROVER_REQUEST_FOR_CURRENT_BLOCKCHAIN,
    END_OF_FILE,
    VERIFIER_CHECK_IS_FILE_STORED,
    UNKNOWN
}

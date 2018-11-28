package core;

import blockchain.Block;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.Socket;

@Getter
@AllArgsConstructor
class ProverNodeInformation {

    private final Socket socket;
    private final String fileHash;
    private final long fileLineNumber;
    private final String fileLine;
    private final int fileSizeMbs;
    private final Block proposedBlock;
}

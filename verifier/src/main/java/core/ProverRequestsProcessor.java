package core;

import blockchain.Block;
import blockchain.Blockchain;
import file.FileGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

@Slf4j
class ProverRequestsProcessor extends Thread {
    private Socket prover;
    private BufferedReader in;
    private PrintWriter out;

    ProverRequestsProcessor(Socket prover) {
        this.prover = prover;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(prover.getInputStream()));
            out = new PrintWriter(prover.getOutputStream(), true);

            String messageCodeString;
            while ((messageCodeString = in.readLine()) != null) {
                MessageCode messageCode = MessageCode.valueOf(messageCodeString);
                processRequest(messageCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stopConnection();
        }
    }

    private void processRequest(MessageCode messageCode) {
        switch (messageCode) {
            case PROVER_REQUEST_FOR_CURRENT_BLOCKCHAIN:
                processSendingBlockchain();
                break;
            case PROVER_SENDING_NEW_BLOCK:
                processReceivingNewBlock();
                break;
        }
    }

    private void processSendingBlockchain() {
        out.println(Blockchain.toJSON(VerifierNode.getBlockchain()));
    }

    private void processReceivingNewBlock() {
        try {
            generateAndSendFileOfSpecificSize(Integer.valueOf(in.readLine()));
            addBlockToBlockchain(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateAndSendFileOfSpecificSize(int fileSize) {
        File file = generateFileForProofOfSpace(buildPathToStoreFile(), fileSize);
        sendFileToProver(file);
    }

    private void sendFileToProver(File file) {
        try {
            FileReader fr = new FileReader(file);
            IOUtils.copy(fr, out);
            fr.close();
            out.println(MessageCode.END_OF_FILE);
            FileUtils.deleteQuietly(file);
            System.out.println("Wyslano plik");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File generateFileForProofOfSpace(String path, int fileSize) {
        return FileGenerator.ofSizeMBs(path, fileSize);
    }

    private String buildPathToStoreFile() {
        return "/tmp/" + prover.getPort() + LocalDateTime.now() + ".txt";
    }

    private void addBlockToBlockchain(String blockJSON) {
        Block block = Block.fromJSON(blockJSON);
        log.info("Block received from node " + prover.getPort() + " : " + block);
        VerifierNode.addToBlockchain(block);
    }

    private void stopConnection() {
        try {
            in.close();
            out.close();
            prover.close();
        } catch (IOException e) {
            log.error("Error while stopping connection with prover");
            log.error(e.getMessage());
        }
    }
}

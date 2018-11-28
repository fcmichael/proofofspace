package core;

import blockchain.Block;
import blockchain.Blockchain;
import file.FileGenerator;
import file.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

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
            int fileSize = Integer.valueOf(in.readLine());
            File generatedFile = generateAndSendFileOfSpecificSize(fileSize);
            String fileHash = FileService.getFileMd5Hash(generatedFile);
            Block block = Block.fromJSON(in.readLine());
            long randomLineNumber = generateRandomFileLine(FileService.countFileLines(generatedFile.getPath()));
            String fileLine = FileService.getSpecificFileLine(generatedFile.getPath(), randomLineNumber);
            VerifierNode.addProverToBlockchainParticipants(prover.getPort(), new ProverNodeInformation(prover, fileHash, randomLineNumber, fileLine, fileSize, block));
            sendFileToProver(generatedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File generateAndSendFileOfSpecificSize(int fileSize) {
        return generateFileForProofOfSpace(buildPathToStoreFile(), fileSize);
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

    private long generateRandomFileLine(long maxSize) {
        return ThreadLocalRandom.current().nextLong(1, maxSize);
    }

    private String buildPathToStoreFile() {
        return "/tmp/" + prover.getPort() + LocalDateTime.now() + ".txt";
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

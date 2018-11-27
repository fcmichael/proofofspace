package core;

import blockchain.Block;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
        Gson gson = new Gson();
        String blockchainJSON = gson.toJson(VerifierNode.getBlockchain());
        out.println(blockchainJSON);
    }

    private void processReceivingNewBlock() {
        try {
            String input = in.readLine();
            Gson gson = new Gson();
            Block block = gson.fromJson(input, Block.class);
            log.info("Block received from node " + prover.getPort() + " : " + block);
            VerifierNode.addToBlockchain(block);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

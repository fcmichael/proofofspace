package core;

import blockchain.Blockchain;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class VerifierNode {

    private static final int SERVER_SOCKET_PORT = 2222;

    private static final Blockchain blockchain = new Blockchain<String>();

    private static ServerSocket serverSocket;
    private static Map<Integer, Socket> provers;

    public static void main(String[] args) {
        init();
        configureProverRequestsProcessor();
    }

    static Blockchain getBlockchain(){
        return blockchain;
    }

    private static void init() {
        try {
            serverSocket = new ServerSocket(SERVER_SOCKET_PORT);
            provers = new HashMap<>();
        } catch (IOException e) {
            log.error("Error while initializing server socket");
            log.error(e.getMessage());
        }
    }

    private static void configureProverRequestsProcessor() {
        try {
            Socket prover = serverSocket.accept();
            provers.put(prover.getPort(), prover);
            new ProverRequestsProcessor(prover).start();
        } catch (IOException e) {
            log.error("Error while accepting client request");
            log.error(e.getMessage());
        }
    }
}

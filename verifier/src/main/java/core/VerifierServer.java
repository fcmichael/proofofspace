package core;

import blockchain.Block;
import blockchain.Blockchain;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VerifierServer {

    private static final int SERVER_SOCKET_PORT = 2222;
    private static final Blockchain blockchain = new Blockchain();
    private static ServerSocket serverSocket;
    private static Map<Integer, ProverNodeInformation> proversTakingPartInBlockCreation;
    private static final int CHOOSING_WINNING_BLOCK_PERIOD_SECONDS = 45;

    public static void main(String[] args) {
        init();
        scheduleChoosingWinningBlock();
        configureProverRequestsProcessor();
    }

    static void addToBlockchain(Block block) {
        if (blockchain.addBlock(block)) {
            System.out.println("Poprawnie dodano nowy blok do łancucha");
        } else {
            System.out.println("Niepoprawny blok, nie dodano");
        }
    }

    static Blockchain getBlockchain() {
        return blockchain;
    }

    static HashMap<Integer, ProverNodeInformation> getCurrentBlockchainParticipants() {
        return new HashMap<>(proversTakingPartInBlockCreation);
    }

    static void addProverToBlockchainParticipants(int port, ProverNodeInformation proverNodeInformation) {
        proversTakingPartInBlockCreation.put(port, proverNodeInformation);
    }

    static void clearBlockchainParticipants() {
        proversTakingPartInBlockCreation.clear();
    }

    private static void init() {
        try {
            serverSocket = new ServerSocket(SERVER_SOCKET_PORT);
            proversTakingPartInBlockCreation = new ConcurrentHashMap<>();
            System.out.println("Zainicjalizowano serwer weryfikujący na porcie: " + SERVER_SOCKET_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void configureProverRequestsProcessor() {
        try {
            while (true) {
                Socket prover = serverSocket.accept();
                new ProverRequestsProcessor(prover).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void scheduleChoosingWinningBlock() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(
                new WinningBlockChooser(), CHOOSING_WINNING_BLOCK_PERIOD_SECONDS, CHOOSING_WINNING_BLOCK_PERIOD_SECONDS, TimeUnit.SECONDS);
    }
}

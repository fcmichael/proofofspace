package core;

import blockchain.Block;
import blockchain.Blockchain;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static core.ConsoleCode.FINISH;

@Slf4j
public class ProverNode {

    private static Blockchain blockchain;

    private static final String VERIFIER_NODE_HOST = "127.0.0.1";
    private static final int VERIFIER_NODE_PORT = 2222;

    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void main(String[] args) {
        startConnectionWithVerifierNode();
        downloadCurrentBlockchain();
        askUserForRequests();
        stopConnectionWithVerifierNode();
    }

    private static void startConnectionWithVerifierNode() {
        try {
            socket = new Socket(VERIFIER_NODE_HOST, VERIFIER_NODE_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            log.error("Error while initializing prover node and connection with verifier node");
            log.error(e.getMessage());
        }
    }

    private static void downloadCurrentBlockchain() {
        try {
            out.println(MessageCode.PROVER_REQUEST_FOR_CURRENT_BLOCKCHAIN.name());
            String input = in.readLine();
            Gson gson = new Gson();
            blockchain = gson.fromJson(input, Blockchain.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void askUserForRequests() {
        printMenu();

        try (Scanner scanner = new Scanner(System.in)) {
            ConsoleCode task;
            while ((task = ConsoleCode.fromId(scanner.nextInt())) != FINISH) {
                switch (task) {
                    case PRINT_BLOCKCHAIN:
                        downloadCurrentBlockchain();
                        blockchain.print();
                        break;
                    case ADD_NEW_BLOCK:
                        processAddingNewBlock(scanner);
                        break;
                }
                printMenu();
            }
        }
    }

    private static void processAddingNewBlock(Scanner scanner) {
        System.out.println("Podaj ciag znakow, ktory zostanie dodany do lancucha:");
        String blockData = scanner.next();
        Block block = new Block(blockchain.size(),
                blockchain.getBlockAtIndex(blockchain.size() - 1).getHash(),
                String.valueOf(socket.getLocalPort()), blockData);

        out.println(MessageCode.PROVER_SENDING_NEW_BLOCK.name());
        out.println(new Gson().toJson(block));
    }

    private static void printMenu() {
        System.out.println("(0) - zakoncz");
        System.out.println("(1) - wyswietl blockchain");
        System.out.println("(2) - dodaj nowy blok");
        System.out.print("Podaj komende:");
    }

    private static void stopConnectionWithVerifierNode() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            log.error("Error while stopping connection with verifier node");
            log.error(e.getMessage());
        }
    }
}

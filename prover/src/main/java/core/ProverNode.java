package core;

import blockchain.Block;
import blockchain.Blockchain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
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
            blockchain = Blockchain.fromJSON(in.readLine());
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
        System.out.println("Podaj rozmiar pliku na potrzeby realizacji konsensusu Proof of Space [MB]:");
        int fileSize = scanner.nextInt();

        Block block = createBlock(blockData);

        out.println(MessageCode.PROVER_SENDING_NEW_BLOCK.name());
        out.println(fileSize);
        out.println(Block.toJSON(block));

        receiveAndStoreFileForProofOfSpace();
    }

    private static Block createBlock(String data) {
        return new Block(blockchain.size(),
                blockchain.getBlockAtIndex(blockchain.size() - 1).getHash(),
                String.valueOf(socket.getLocalPort()), data);
    }

    private static String buildFilePath() {
        return "files/" + socket.getPort() + LocalDateTime.now() + ".txt";
    }

    private static void receiveAndStoreFileForProofOfSpace() {
        File file = new File(buildFilePath());

        try {
            FileUtils.touch(file);
            FileWriter fw = new FileWriter(file);

            String line;
            while ((line = in.readLine()) != null && !MessageCode.END_OF_FILE.name().equals(line)) {
                fw.write(line);
                fw.write("\n");
            }

            fw.close();
            System.out.println("zapisano plik");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

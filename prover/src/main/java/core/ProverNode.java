package core;

import blockchain.Block;
import blockchain.Blockchain;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

import static core.ConsoleCode.FINISH;
import static core.ConsoleCode.UNKNOWN;

public class ProverNode {

    private static String proofOfSpaceFilePath;

    private static Blockchain blockchain;

    private static final String VERIFIER_NODE_HOST = "127.0.0.1";
    private static final int VERIFIER_NODE_PORT = 2222;

    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    private static ProverServer proverServer;

    public static void main(String[] args) {
        startConnectionWithVerifierNode();
        downloadCurrentBlockchain();
        initVerifierRequestsProcessor();
        askUserForRequests();
        stopVerifierRequestsProcessor();
        stopConnectionWithVerifierNode();
    }

    private static void startConnectionWithVerifierNode() {
        try {
            socket = new Socket(VERIFIER_NODE_HOST, VERIFIER_NODE_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            proofOfSpaceFilePath = "files/" + socket.getLocalPort() + ".pospace.txt";
            System.out.println("Zainicjalizowano węzeł dowodzący na porcie: " + socket.getLocalPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initVerifierRequestsProcessor() {
        proverServer = new ProverServer(proofOfSpaceFilePath);
        new Thread(proverServer).start();
        sendServerPortNumber();
    }

    private static void stopVerifierRequestsProcessor() {
        proverServer.stop();
    }

    private static void downloadCurrentBlockchain() {
        try {
            out.println(MessageCode.PROVER_REQUEST_FOR_CURRENT_BLOCKCHAIN.name());
            blockchain = Blockchain.fromJSON(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendServerPortNumber() {
        out.println(MessageCode.PROVER_SENDING_SERVER_PORT_NUMBER.name());
        out.println(proverServer.getLocalPort());
    }

    private static void askUserForRequests() {
        try (Scanner scanner = new Scanner(System.in)) {
            ConsoleCode task = UNKNOWN;
            while (task != FINISH) {
                try {
                    printMenu();
                    task = ConsoleCode.fromId(scanner.nextInt());
                    switch (task) {
                        case PRINT_BLOCKCHAIN:
                            downloadCurrentBlockchain();
                            blockchain.print();
                            break;
                        case ADD_NEW_BLOCK:
                            downloadCurrentBlockchain();
                            processAddingNewBlock(scanner);
                            break;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Błędne dane. Podaj poprawną komendę z listy.");
                    scanner.next();
                }
            }
        }
    }

    private static void processAddingNewBlock(Scanner scanner) {
        try {
            System.out.println("Podaj ciag znakow, ktory zostanie dodany do lancucha:");
            String blockData = scanner.next();
            System.out.println("Podaj rozmiar pliku na potrzeby realizacji konsensusu Proof of Space [MB]:");
            int fileSize = scanner.nextInt();

            Block block = createBlock(blockData);

            out.println(MessageCode.PROVER_SENDING_NEW_BLOCK.name());
            out.println(fileSize);
            out.println(Block.toJSON(block));

            receiveAndStoreFileForProofOfSpace();
        } catch (InputMismatchException e) {
            System.out.println("Podano nieprawidłowy rozmiar pliku");
            scanner.next();
        }
    }

    private static Block createBlock(String data) {
        return new Block(blockchain.size(),
                blockchain.getBlockAtIndex(blockchain.size() - 1).getHash(),
                String.valueOf(socket.getLocalPort()), data);
    }

    private static void receiveAndStoreFileForProofOfSpace() {
        File file = new File(proofOfSpaceFilePath);

        try {
            FileUtils.touch(file);
            FileWriter fw = new FileWriter(file);

            String line;
            while ((line = in.readLine()) != null && !MessageCode.END_OF_FILE.name().equals(line)) {
                fw.write(line);
                fw.write("\n");
            }

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printMenu() {
        System.out.println("(0) - zakoncz");
        System.out.println("(1) - wyswietl blockchain");
        System.out.println("(2) - dodaj nowy blok");
        System.out.print("Wybierz polecenie:");
    }

    private static void stopConnectionWithVerifierNode() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

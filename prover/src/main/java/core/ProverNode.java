package core;

import blockchain.Blockchain;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class ProverNode {

    private static final String VERIFIER_NODE_HOST = "127.0.0.1";
    private static final int VERIFIER_NODE_PORT = 2222;

    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void main(String[] args) throws IOException {
        startConnectionWithVerifierNode();

        out.println(MessageCode.SEND_BLOCKCHAIN.name());

        String input = in.readLine();
        Gson gson = new Gson();

        Blockchain blockchain = gson.fromJson(input, Blockchain.class);

        blockchain.print();

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

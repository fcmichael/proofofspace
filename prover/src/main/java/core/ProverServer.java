package core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProverServer implements Runnable {

    private ServerSocket serverSocket;
    private int serverPort;
    private boolean isStopped = false;
    private String proofOfSpaceFilePath;

    ProverServer(int port, String proofOfSpaceFilePath) {
        this.serverPort = port;
        this.proofOfSpaceFilePath = proofOfSpaceFilePath;
    }

    @Override
    public void run() {
        openServerSocket();
        while (!isStopped()) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                new Thread(new VerifierRequestsProcessor(clientSocket, proofOfSpaceFilePath)).start();
            } catch (IOException e) {
                if (isStopped()) {
                    return;
                }
                e.printStackTrace();
            }
        }
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

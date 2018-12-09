package core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProverServer implements Runnable {

    private ServerSocket serverSocket;
    private boolean isStopped = false;
    private String proofOfSpaceFilePath;

    ProverServer(String proofOfSpaceFilePath) {
        this.proofOfSpaceFilePath = proofOfSpaceFilePath;
        openServerSocket();
    }

    @Override
    public void run() {
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

    int getLocalPort() {
        return serverSocket.getLocalPort();
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
            this.serverSocket = new ServerSocket(0);
            System.out.println("Zainicjalizowano server na porcie: " + this.serverSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

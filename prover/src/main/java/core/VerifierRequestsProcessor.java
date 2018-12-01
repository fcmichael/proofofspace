package core;

import file.FileService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class VerifierRequestsProcessor extends Thread {

    private Socket clientSocket;
    private int proverPort;
    private PrintWriter out;
    private BufferedReader in;

    VerifierRequestsProcessor(Socket clientSocket, int proverPort) {
        this.clientSocket = clientSocket;
        this.proverPort = proverPort;
    }

    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String messageCodeString;
            while ((messageCodeString = in.readLine()) != null) {
                MessageCode messageCode = MessageCode.valueOf(messageCodeString);
                processRequest(messageCode);
            }

            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processRequest(MessageCode messageCode) throws IOException {
        switch (messageCode) {
            case VERIFIER_CHECK_IS_FILE_STORED:
                System.out.println("Otrzymano zadanie weryfikacji pliku");
                long fileLineToSend = Long.valueOf(in.readLine());

                out.println(getLineFromStoredFile(fileLineToSend));

                out.println(getHashOfStoredFile());

                break;
        }
    }


    private String getLineFromStoredFile(long line) {
        String path = "files/" + proverPort + ".pospace.txt";
        return FileService.getSpecificFileLine(path, line);
    }

    private String getHashOfStoredFile() {
        String path = "files/" + proverPort + ".pospace.txt";
        return FileService.getFileMd5Hash(path);
    }
}

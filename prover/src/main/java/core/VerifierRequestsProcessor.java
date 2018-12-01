package core;

import file.FileService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class VerifierRequestsProcessor extends Thread {

    private String proofOfSpaceFilePath;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    VerifierRequestsProcessor(Socket clientSocket, String proofOfSpaceFilePath) {
        this.clientSocket = clientSocket;
        this.proofOfSpaceFilePath = proofOfSpaceFilePath;
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
        if (messageCode == MessageCode.VERIFIER_CHECK_IS_FILE_STORED) {
            long fileLineToSend = Long.valueOf(in.readLine());
            out.println(getLineFromStoredFile(fileLineToSend));
            out.println(getHashOfStoredFile());
        }
    }

    private String getLineFromStoredFile(long line) {
        return FileService.getSpecificFileLine(proofOfSpaceFilePath, line);
    }

    private String getHashOfStoredFile() {
        return FileService.getFileMd5Hash(proofOfSpaceFilePath);
    }
}

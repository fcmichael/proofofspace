package core;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@Slf4j
class ProverRequestsProcessor extends Thread {
    private Socket prover;
    private BufferedReader in;

    ProverRequestsProcessor(Socket prover) {
        this.prover = prover;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(prover.getInputStream()));

            String received = in.readLine();

            log.info("Received: " + received);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stopConnection();
        }
    }

    private void stopConnection() {
        try {
            in.close();
            prover.close();
        } catch (IOException e) {
            log.error("Error while stopping connection with prover");
            log.error(e.getMessage());
        }
    }
}

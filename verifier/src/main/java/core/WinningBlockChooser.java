package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class WinningBlockChooser extends Thread {

    private final String PROVER_HOST = "127.0.0.1";
    private final int NONE_PROVER_PASSED_PROOF_OF_SPACE_VERIFICATION = -1;

    @Override
    public void run() {
        try {
            Map<Integer, ProverNodeInformation> copy = VerifierServer.getCurrentBlockchainParticipants();

            System.out.println("Losowanie o " + LocalTime.now().withNano(0).toString());
            if (copy.size() > 0) {
                System.out.println("Wybieranie zwycięskiego węzła spośród kandydatów: ");
                printNodesChances(copy);

                int winningPort = chooseWinner(copy);

                if (winningPort != NONE_PROVER_PASSED_PROOF_OF_SPACE_VERIFICATION) {
                    System.out.println("Zwycieski port: " + winningPort);
                    VerifierServer.addToBlockchain(copy.get(winningPort).getProposedBlock());
                } else {
                    System.out.println("Żaden z kandydatów nie przeszedł weryfikacji Proof of Space");
                }

                VerifierServer.clearBlockchainParticipants();
            } else {
                System.out.println("Żaden węzeł nie zgłosił propozycji nowego bloku");
            }

            System.out.println("-------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int chooseWinner(Map<Integer, ProverNodeInformation> participants) {
        int portOfWinner = NONE_PROVER_PASSED_PROOF_OF_SPACE_VERIFICATION;
        boolean winnerChose = false;

        while (!participants.isEmpty() && !winnerChose) {
            int candidate = drawWinningTicket(createDrawingPool(participants));

            System.out.println("Wylosowano węzeł: " + candidate);

            if (checkProofOfSpace(participants.get(candidate))) {
                System.out.println("Kandydat pomyślnie przeszedł weryfikację Proof of Space");
                portOfWinner = candidate;
                winnerChose = true;
            } else {
                participants.remove(candidate);
            }
        }

        return portOfWinner;
    }

    Map<Integer, Double> calculateProversChancesToAddABlock(Map<Integer, ProverNodeInformation> participants) {
        double mbsSum = participants.values().stream().mapToDouble(ProverNodeInformation::getFileSizeMbs).sum();

        return participants.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFileSizeMbs() / mbsSum));
    }

    List<Integer> createDrawingPool(Map<Integer, ProverNodeInformation> participants) {
        List<Integer> drawingPool = new ArrayList<>();

        participants.forEach((port, info) -> {
            int ticketCount = info.getFileSizeMbs();
            for (int i = 0; i < ticketCount; i++) {
                drawingPool.add(port);
            }
        });

        return drawingPool;
    }

    private int drawWinningTicket(List<Integer> drawingPool) {
        int winningIndex = ThreadLocalRandom.current().nextInt(drawingPool.size());
        return drawingPool.get(winningIndex);
    }

    private void printNodesChances(Map<Integer, ProverNodeInformation> participants) {
        Map<Integer, Double> chances = calculateProversChancesToAddABlock(participants);
        chances.forEach((port, chance) -> System.out.println(port + " -> " + String.format("%.2f", chance)));
    }

    private boolean checkProofOfSpace(ProverNodeInformation winner) {
        boolean validProofOfSpace = false;

        try {
            Socket socket = new Socket(PROVER_HOST, winner.getServerPort());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(MessageCode.VERIFIER_CHECK_IS_FILE_STORED.name());
            out.println(winner.getFileLineNumber());

            String line = in.readLine();
            String hash = in.readLine();

            validProofOfSpace = isReceivedFileLineEqualStoredLine(line, winner.getFileLine()) &&
                    isReceivedHashOfFileEqualStoredHas(hash, winner.getFileHash());

            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return validProofOfSpace;
    }

    private boolean isReceivedFileLineEqualStoredLine(String received, String stored) {
        boolean valid = stored.equals(received);

        if (!valid) {
            System.out.println("Otrzymana linia pliku jest nieprawidłowa");
        }

        return valid;
    }

    private boolean isReceivedHashOfFileEqualStoredHas(String received, String stored) {
        boolean valid = stored.equals(received);

        if (!valid) {
            System.out.println("Otrzymany hash pliku jest nieprawidłowy");
        }

        return valid;
    }
}

package core;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class WinningBlockChooser extends Thread {

    @Override
    public void run() {
        try {
            Map<Integer, ProverNodeInformation> copy = VerifierNode.getCurrentBlockchainParticipants();

            System.out.println("Losowanie o " + LocalDateTime.now().toString());
            if (copy.size() > 0) {
                System.out.println("Wybieranie node'a");

                System.out.println("Propozycje: ");
                copy.forEach((integer, proverNodeInformation) -> {
                    System.out.println("Port " + integer + ", hash pliku: " + proverNodeInformation.getFileHash());
                });

                ArrayList<Integer> keys = new ArrayList<>(copy.keySet());
                Integer winningPort = keys.get(0);
                System.out.println("Zwycieski port: " + winningPort);

                VerifierNode.addToBlockchain(copy.get(winningPort).getProposedBlock());
                VerifierNode.clearBlockchainParticipants();
            } else {
                System.out.println("Żaden węzeł nie zgłosił propozycji nowego bloku");
            }

            System.out.println("-------------------------");
        } catch (Exception e) {
            System.err.println("Błąd podczas wybierania zwycięskiego węzła");
            System.err.println(e.getMessage());
        }
    }

    Map<Integer, Double> calculateProversChancesToAddABlock(Map<Integer, ProverNodeInformation> participants) {
        double mbsSum = participants.values().stream().mapToDouble(ProverNodeInformation::getFileSizeMbs).sum();

        return participants.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFileSizeMbs() / mbsSum));
    }

    void printNodesChances(Map<Integer, ProverNodeInformation> participants) {

    }
}

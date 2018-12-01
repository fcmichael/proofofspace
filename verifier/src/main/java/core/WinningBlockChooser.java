package core;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class WinningBlockChooser extends Thread {

    @Override
    public void run() {
        try {
            Map<Integer, ProverNodeInformation> copy = VerifierNode.getCurrentBlockchainParticipants();

            System.out.println("Losowanie o " + LocalTime.now().withNano(0).toString());
            if (copy.size() > 0) {
                System.out.println("Wybieranie zwycięskiego węzła spośród kandydatów: ");
                printNodesChances(copy);

                int winningPort = drawWinningTicket(createDrawingPool(copy));
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
}

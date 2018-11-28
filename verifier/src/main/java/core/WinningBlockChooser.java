package core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.Random;

public class WinningBlockChooser extends Thread {

    @Override
    public void run() {
        Map<Integer, ProverNodeInformation> copy = VerifierNode.getCurrentBlockchainParticipants();

        System.out.println(new Gson().toJson(copy));

        if (copy.size() > 0) {
            System.out.println("Choosing winning block");
//            Integer[] keys = (Integer[]) copy.keySet().toArray();
//            Integer randomKey = keys[0];
            System.out.println("Winning port: " + copy.get(0).getSocket().getPort());
            VerifierNode.addToBlockchain(copy.get(0).getProposedBlock());
        } else {
            System.out.println("No nodes sent block propositions");
        }
    }
}

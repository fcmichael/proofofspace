package blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {

    private List<Block> blockchain;

    public Blockchain() {
        this.blockchain = new ArrayList<>();
        Block genesis = new Block(0, "0", null, null);
        blockchain.add(genesis);
    }

    public boolean addBlock(Block block) {
        blockchain.add(block);
        if (!isValid()) {
            blockchain.remove(block);
            return false;
        }
        return true;
    }

    public int size() {
        return blockchain.size();
    }

    public Block getBlockAtIndex(int index) {
        return blockchain.get(index);
    }

    boolean isValid() {
        boolean valid = true;
        Block previous;
        Block current;

        int i = 1;
        int size = blockchain.size();
        while (valid && i < size) {
            current = blockchain.get(i);
            previous = blockchain.get(i - 1);

            valid = isBlockHashCalculatedProperly(current) &&
                    isPreviousBlockProperlyLinkedWithCurrentBlock(previous, current) &&
                    isCorrectBlockIndex(i, current);
            i++;
        }

        return valid;
    }

    public static String toJSON(Blockchain blockchain) {
        return new Gson().toJson(blockchain);
    }

    public static Blockchain fromJSON(String json) {
        return new Gson().fromJson(json, Blockchain.class);
    }

    public void print() {
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(blockchain));
    }

    private boolean isBlockHashCalculatedProperly(Block block) {
        String currentHash = block.getHash();
        String calculatedHash = block.calculateHash();
        boolean valid = currentHash.equals(calculatedHash);

        if (!valid) {
            System.out.println("Nieprawidłowy hash bloku, jest: " + block.getHash() + ", powinien być: " + calculatedHash);
        }

        return valid;
    }

    private boolean isPreviousBlockProperlyLinkedWithCurrentBlock(Block previous, Block current) {
        String previousBlockHash = previous.getHash();
        String currentBlockPreviousHash = current.getPreviousHash();
        boolean valid = previousBlockHash.equals(currentBlockPreviousHash);

        if (!valid) {
            System.out.println("Nieprawidłowy hash poprzedniego bloku, jest: "
                    + currentBlockPreviousHash + ", powinien być: " + previousBlockHash);
        }

        return valid;
    }

    private boolean isCorrectBlockIndex(int index, Block block) {
        boolean valid = index == block.getIndex();

        if (!valid) {
            System.out.println("Nieprawidłowy indeks bloku, jest: " + block.getIndex() + ", powinien być: " + index);
        }

        return valid;
    }
}

package blockchain;

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
            System.out.println("Invalid block");
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

    public void print() {
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(blockchain));
    }

    private boolean isBlockHashCalculatedProperly(Block block) {
        return block.getHash().equals(block.calculateHash());
    }

    private boolean isPreviousBlockProperlyLinkedWithCurrentBlock(Block previous, Block current) {
        return previous.getHash().equals(current.getPreviousHash());
    }

    private boolean isCorrectBlockIndex(int index, Block block) {
        return index == block.getIndex();
    }
}

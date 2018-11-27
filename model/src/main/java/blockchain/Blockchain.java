package blockchain;

import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Blockchain<T> {

    @Getter
    private List<Block<T>> blockchain;

    public Blockchain() {
        this.blockchain = new ArrayList<>();
        Block<T> genesis = new Block<>(0, "0", null);
        blockchain.add(genesis);
    }

    public void addBlock(T data) {
        String previousHash = blockchain.get(size() - 1).getHash();
        Block<T> block = new Block<>(size(), previousHash, data);
        blockchain.add(block);
    }

    public boolean isValid() {
        boolean valid = true;
        Block previous;
        Block current;

        int i = 1;
        int size = blockchain.size();
        while (valid && i < size) {
            current = blockchain.get(i);
            previous = blockchain.get(i - 1);

            valid = isBlockHashCalculatedProperly(current) && isPreviousBlockProperlyLinkedWithCurrentBlock(previous, current);
            i++;
        }

        return valid;
    }

    int size() {
        return blockchain.size();
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
}

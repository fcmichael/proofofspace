package blockchain;

import com.google.gson.Gson;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
public class Block {

    private int index;
    private String previousHash;
    private long timestamp;
    private String prover;
    private String data;
    private String hash;

    public Block(int index, String previousHash, String prover, String data) {
        this.index = index;
        this.previousHash = previousHash;
        this.timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        this.prover = prover;
        this.data = data;
        this.hash = calculateHash();
    }

    public static String toJSON(Block block) {
        return new Gson().toJson(block);
    }

    public static Block fromJSON(String json) {
        return new Gson().fromJson(json, Block.class);
    }

    String calculateHash() {
        return HashService.sha256(index + previousHash + timestamp + prover + data);
    }
}

package blockchain;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@ToString
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

    String calculateHash() {
        return HashService.sha256(index + previousHash + timestamp + prover + data);
    }
}

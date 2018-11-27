package blockchain;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@ToString
class Block<T> {

    private int index;
    private String previousHash;
    private long timestamp;
    private T data;
    private String hash;

    Block(int index, String previousHash, T data) {
        this.index = index;
        this.previousHash = previousHash;
        this.timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        this.data = data;
        this.hash = calculateHash();
    }

    String calculateHash() {
        return HashService.sha256(index + previousHash + timestamp + data);
    }
}

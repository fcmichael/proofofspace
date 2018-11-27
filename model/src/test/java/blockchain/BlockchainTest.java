package blockchain;

import org.junit.Assert;
import org.junit.Test;

public class BlockchainTest {

    @Test
    public void should_create_correct_string_blockchain_containing_3_blocks() {
        Blockchain blockchain = new Blockchain();
        blockchain.addBlock("Second block data");
        blockchain.addBlock("Third block data");

        blockchain.print();

        Assert.assertEquals(3, blockchain.size());
        Assert.assertTrue(blockchain.isValid());
    }

    @Test
    public void should_deny_blockchain_correctness_if_someone_removed_block() {
        Blockchain<String> blockchain = new Blockchain<>();
        blockchain.addBlock("Second block data");
        blockchain.addBlock("Third block data");
        blockchain.getBlockchain().remove(1);

        blockchain.print();

        Assert.assertEquals(2, blockchain.size());
        Assert.assertFalse(blockchain.isValid());
    }

}

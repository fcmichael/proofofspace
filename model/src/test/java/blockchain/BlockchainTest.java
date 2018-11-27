package blockchain;

import org.junit.Assert;
import org.junit.Test;

public class BlockchainTest {

    @Test
    public void should_create_correct_blockchain_containing_3_blocks() {
        Blockchain blockchain = new Blockchain();
        Block block1 = new Block(1, blockchain.getBlockAtIndex(0).getHash(), "prover1", "Second block data");
        boolean result1 = blockchain.addBlock(block1);
        Block block2 = new Block(2, blockchain.getBlockAtIndex(1).getHash(), "prover2", "Third block data");
        boolean result2 = blockchain.addBlock(block2);

        Assert.assertTrue(result1);
        Assert.assertTrue(result2);
        Assert.assertEquals(3, blockchain.size());
        Assert.assertEquals(block1, blockchain.getBlockAtIndex(1));
        Assert.assertEquals(block2, blockchain.getBlockAtIndex(2));
        Assert.assertTrue(blockchain.isValid());
    }

    @Test
    public void should_not_add_block_with_invalid_hash_of_previous_block() {
        Blockchain blockchain = new Blockchain();
        Block block1 = new Block(1, "INVALID_HASH", "prover1", "Second block data");
        boolean result1 = blockchain.addBlock(block1);

        Assert.assertFalse(result1);
        Assert.assertEquals(1, blockchain.size());
        Assert.assertTrue(blockchain.isValid());
    }

    @Test
    public void should_not_add_block_with_invalid_index() {
        Blockchain blockchain = new Blockchain();
        Block block1 = new Block(999, blockchain.getBlockAtIndex(0).getHash(), "prover1", "Second block data");
        boolean result1 = blockchain.addBlock(block1);

        Assert.assertFalse(result1);
        Assert.assertEquals(1, blockchain.size());
        Assert.assertTrue(blockchain.isValid());
    }
}

package core;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WinningBlockChooserTest {

    @Test
    public void should_count_blocks_chances_to_add_a_block() {
        ProverNodeInformation p1 = new ProverNodeInformation(null, null, 0, null, 2, null);
        ProverNodeInformation p2 = new ProverNodeInformation(null, null, 0, null, 3, null);
        ProverNodeInformation p3 = new ProverNodeInformation(null, null, 0, null, 5, null);
        Map<Integer, ProverNodeInformation> participants = new HashMap<>();

        participants.put(1, p1);
        participants.put(2, p2);
        participants.put(3, p3);

        WinningBlockChooser winningBlockChooser = new WinningBlockChooser();
        Map<Integer, Double> chances = winningBlockChooser.calculateProversChancesToAddABlock(participants);

        Assert.assertEquals(3, chances.size());
        Assert.assertEquals(0.2, chances.get(1), 0.001);
        Assert.assertEquals(0.3, chances.get(2), 0.001);
        Assert.assertEquals(0.5, chances.get(3), 0.001);
    }

    @Test
    public void should_create_drawing_pool() {
        ProverNodeInformation p1 = new ProverNodeInformation(null, null, 0, null, 2, null);
        ProverNodeInformation p2 = new ProverNodeInformation(null, null, 0, null, 3, null);
        ProverNodeInformation p3 = new ProverNodeInformation(null, null, 0, null, 5, null);
        Map<Integer, ProverNodeInformation> participants = new HashMap<>();

        participants.put(1, p1);
        participants.put(2, p2);
        participants.put(3, p3);

        WinningBlockChooser winningBlockChooser = new WinningBlockChooser();
        List<Integer> drawingPool = winningBlockChooser.createDrawingPool(participants);

        Assert.assertEquals(10, drawingPool.size());
        Assert.assertEquals(2, drawingPool.stream().filter(i -> i == 1).count());
        Assert.assertEquals(3, drawingPool.stream().filter(i -> i == 2).count());
        Assert.assertEquals(5, drawingPool.stream().filter(i -> i == 3).count());
    }
}

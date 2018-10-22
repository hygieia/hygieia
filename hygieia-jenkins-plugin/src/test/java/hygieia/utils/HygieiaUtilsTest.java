package hygieia.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class HygieiaUtilsTest {

    @Test
    public void getBuildCollectionId() {
        String input = "5ba16a0b0be2d34a64291205,56c39f487fab7c63c8f947aa";
        String output = HygieiaUtils.getBuildCollectionId(input);
        assertTrue(output.equals("5ba16a0b0be2d34a64291205"));
    }

    @Test
    public void getCollectorItemId() {
        String input = "5ba16a0b0be2d34a64291205,56c39f487fab7c63c8f947aa";
        String output = HygieiaUtils.getCollectorItemId(input);
        assertTrue(output.equals("56c39f487fab7c63c8f947aa"));
    }

    @Test
    public void getBuildCollectionIdJustOne() {
        String input = "5ba16a0b0be2d34a64291205a";
        String output = HygieiaUtils.getBuildCollectionId(input);
        assertTrue(output.equals("5ba16a0b0be2d34a64291205a"));
    }

    @Test
    public void getCollectorItemIdJustOne() {
        String input = "5ba16a0b0be2d34a64291205";
        String output = HygieiaUtils.getCollectorItemId(input);
        assertTrue(output.equals(""));
    }

    @Test
    public void getBuildCollectionIdJustOneComma() {
        String input = ",5ba16a0b0be2d34a64291205a";
        String output = HygieiaUtils.getBuildCollectionId(input);
        assertTrue(output.equals(""));
    }

    @Test
    public void getCollectorItemIdJustOneComma() {
        String input = ",5ba16a0b0be2d34a64291205";
        String output = HygieiaUtils.getCollectorItemId(input);
        assertTrue(output.equals("5ba16a0b0be2d34a64291205"));
    }

}
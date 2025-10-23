package io.JesseChum.centroidfinder;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

public class DfsBinaryTests {

    @Test
    public void dfsOneGroupSizeNine(){
        int[][] image = {
            {1, 1, 1, 0, 0},
            {1, 1, 1, 0, 0},
            {1, 1, 1, 0, 0},
            {0, 0, 0, 0, 0}
        };
        Coordinate center = new Coordinate(1, 1);
        Group expectedGroup = new Group(9, center);
        List<Group> expectedList = new ArrayList<Group>();
        expectedList.add(expectedGroup);

        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        List<Group> actualList = finder.findConnectedGroups(image);
        assertEquals(expectedList, actualList);
    }

    @Test
    public void dfsTwoGroupsSizeNine(){
        int[][] image = {
            {1, 1, 1, 0, 1, 1, 1},
            {1, 1, 1, 0, 1, 1, 1},
            {1, 1, 1, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0}
        };
        Coordinate centerOne = new Coordinate(1, 1);
        Group expectedGroupOne = new Group(9, centerOne);

        Coordinate centerTwo = new Coordinate(5, 1);
        Group expectedGroupTwo = new Group(9, centerTwo);
        
        List<Group> expectedList = new ArrayList<Group>();
        expectedList.add(expectedGroupOne);
        expectedList.add(expectedGroupTwo);
        
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        List<Group> actualList = finder.findConnectedGroups(image);
        assertEquals(expectedList, actualList);
    }
    
}

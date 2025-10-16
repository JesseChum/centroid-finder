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
        List<Group> actual = DfsBinaryGroupFinder.findConnectedGroups(image);
        expectedList.add(expectedGroup);
    }
    
}

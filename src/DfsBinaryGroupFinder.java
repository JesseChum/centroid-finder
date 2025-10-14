import java.util.HashSet;
import java.util.List;

public class DfsBinaryGroupFinder implements BinaryGroupFinder {
   /**
    * Finds connected pixel groups of 1s in an integer array representing a binary image.
    * 
    * The input is a non-empty rectangular 2D array containing only 1s and 0s.
    * If the array or any of its subarrays are null, a NullPointerException
    * is thrown. If the array is otherwise invalid, an IllegalArgumentException
    * is thrown.
    *
    * Pixels are considered connected vertically and horizontally, NOT diagonally.
    * The top-left cell of the array (row:0, column:0) is considered to be coordinate
    * (x:0, y:0). Y increases downward and X increases to the right. For example,
    * (row:4, column:7) corresponds to (x:7, y:4).
    *
    * The method returns a list of sorted groups. The group's size is the number 
    * of pixels in the group. The centroid of the group
    * is computed as the average of each of the pixel locations across each dimension.
    * For example, the x coordinate of the centroid is the sum of all the x
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * Similarly, the y coordinate of the centroid is the sum of all the y
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * The division should be done as INTEGER DIVISION.
    *
    * The groups are sorted in DESCENDING order according to Group's compareTo method
    * (size first, then x, then y). That is, the largest group will be first, the 
    * smallest group will be last, and ties will be broken first by descending 
    * y value, then descending x value.
    * 
    * @param image a rectangular 2D array containing only 1s and 0s
    * @return the found groups of connected pixels in descending order
    */

    // SO
    // This bad boy will check the initial array. Check if its okay or not to use.
    // If it is, the intention is for it to go through the whole array, looking for '1's.
    // a '1' represents a color match.
    // if we find a 1, we want to make sure we find all the '1's attached to it, which is done by
    // searchLocation. This is called a 'group'
    // findConnectedGroups's whole job is to simply put together all the groups into a bigger list.
    @Override
    public List<Group> findConnectedGroups(int[][] image) {
        if(image == null) throw new NullPointerException("Image array provided is NULL");
        if(image.length == 0) throw new IllegalArgumentException("Image array is is 0 in X direction");
        if(image[0].length == 0) throw new IllegalArgumentException("Image array is is 0 in Y direction");
        // declare a list of groups - empty when we start.
        // global set - empty when we start
        Set<Coordinate> global = new HashSet<>();
        List<Group> emptyList = new ArrayList<>();
        // for loop through the int[]
        for (int i = 0; i < image.length; i++){
        // for loop through the int[][] (double loop)
         for (int j = 0; j < image[i].length; j++){
            // if is '1' - we've entered a group
            if( image[i][j] == 1);
            // declare visited set - empty when we find a new group.
            Set<Coordinate> coordinate = new HashSet<>();
                    // enter recursive function - Niko
            searchLocation(image, i, j, coordinate);        
                    // we are returned a group
                int[] = coordinate.size;
                    // we put that group in a list
                int x;
                int y;

                for (coor temp : set)
               int x += temp.x()
               int y += temp.y()
               int x = x / visited;
               int y = y / visited;
                // if it is '0' we do nothing
            // for loop ends
             }
        // for loop ends
            }
        // we return a list of groups 
        return emptyList;
        }


    // We could probably do a few things here
    // First, we could calculate The 'center' pixel both through X and Y using some averaging somehow
    // We could also track the size of the group by increasing it each valid move
    // then, we return the group as a whole - Niko
    private static void searchLocation(int[][] image, int x, int y, HashSet<> visited) {
        // if out of bounds, return 
        // if already visited, return
        // if valid, we add to the visited set
        
        // probably swap with coordinates? - Niko
        int[][] moves = {
            {-1, 0}, //Up
            {1, 0}, //Down
            {0, 1}, //Right
            {0, -1} //Left
        };

        // track size somehow.
        // at the end, we need to just do visited.size - we will get the total amount of pixels
        // in the same group.
        // track average position somehow.
        // using our visited set, we can add up all the pixels visited in one group.
        // then take the average value.

        // For every movement
            // run searchLocation with the new coordinate
    }



    


    // Not sure below is necessary.

    for (int[] move : moves) {
        int newRow = r + move[0];
        int newCol = c + move[1];
        count += /* parameters and such that goes here. new int[]{newRow, newCol}, visited); */
    }
    // IDK why return count? The thing we do here is than run searchLocation, no?
    return count;


}

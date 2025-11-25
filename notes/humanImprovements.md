|Refactoring code|
-What improvements can you make to the design/architecture of your code?
making a folder to put notes inside their own folder
Breaking large multi purpose classes into smaller componenets

-How can you split up large methods or classes into smaller components?
any methods that have () can be split into different methods
anything way over 50 lines should be looked at

-Are there unused files/methods that can be removed?
removed files have already taken place, usure anout removed methods
old commented out logic can be removed
pom file needs to be checked

-Where would additional Java interfaces be appropriate?
only one in mind that can be used is the centroid finder

-How can you make things simpler, more-usable, and easier to maintain?
remove anything that has a duplicate
make error responses more available
add comments to explain something incase of forgetting

-Other refactoring improvements?
check on error handling

|Adding tests|
-What portions of your code are untested / only lightly tested?
the main java centroid logic
other error handling
video controller?

-Where would be the highest priority places to add new tests?
definetly the centroid detection

-Other testing improvements?
maybe add some type of mock for javafx?

|improving error handling|
-What parts of your code are brittle?
frameproccesor is going to need a rework
front end stuff

-Where could you better be using exceptions?
csv writing failures
video file not found
invalid paths

-Where can you better add input validation to check invalid input?
target color
threshold
outputs

-How can you better be resolving/logging/surfacing errors?
IllegalArgumentExceptions
IOException

-Other error handling improvements
N/A

|Writing documentation|
-What portions of your code are missing Javadoc/JSdoc for the methods/classes?
Frameprocessor, FXvideorunner, BinarizingImagegroupfinder, and the controller

-What documentation could be made clearer or improved?
a better explanation on how everything gets processed
for example. the meaning of threshold and target color

-Are there sections of dead code that are commented out?
in some files yes. they will be removed

-Where would be the most important places to add documentation to make your code easier to read?
 any type of video processing functionality should be documented and made easier to read
 mainly the centroid algorithm and frame processing

-Other documentation improvements?
anything else that needs an explanation should be in comments
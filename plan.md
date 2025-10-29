
brain storming plan to figure out how to implement this second video part.

Goal: Centroid find that works on video data. which produces an ouput that tracks an object position with the given time

goal in steps:
takes an mp4 video
each frame of that video is proccessed through the already existing centroid logic
largest centroid is detected over time in the video
results are in a CSV file
uses (-1, -1) when no centroid is found

How we create that. (Re-usable and must be object oriented)
make a new file for the new video logic commands
make another file for how we are going to process that video we recieve
use our already existing files to find the centroid
put those results in a CSV file

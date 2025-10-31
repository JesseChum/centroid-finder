
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

what we have done:

We used a sample video of a rotating rainbow with seperated with borders, 
this revealed a few things for us - particularly - that the our bounds were not completely set up correctly.
Along side this, while testing on towards the end of the video, the thread responsible for keeping the 
video up decided to just close out sometimes, so we needed to extend the duration of which it was up

potential thresehold:
looking at the Ensantina video, we noticed that everything is in a reddish hue, and the salamander is occasionally being
confused by the big blocks of black. Thus, we are thinking of keeping the thereshold of color pretty low, 10-20, as this
would still allow for some variation in the color, while still seperating the dark reds/browns from the blacks of the table.
we picked out the salamander's color pretty easily, we just used a eye dropper tool and grabbed the hex value of the middle
of the salamander's body.
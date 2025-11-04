Alright, so it looks like we are meant to make an API that doesn't take in any files nor spits em out.
All it does is it grabs from files it already has (Atleast for now, on our machine. Later, likely stored in the server).

One GET will simply list all the videos currently held. So if there are three videos to pick from? It will
return a list with three strings in it.

Another GET expects a filename, and will subsequently return a jpeg of the first frame of said file.
Assumedly, we could use our JAR to extract the first frame somehow? I am not quite sure yet, but 
storing the jpegs preemptively feels like a brute-force way to do so.

One of the POST expects a few things
- a file name of a video
- a targetColor param of a hexcode
- a threshold param of a number
This will start the process of goign through the video, and assumedly will be used to generate a CSV.
This is deifnitely the POST that will be using the JAR and kick it off - we will simply use the params
included in the POST to turn into one of the jar commands on the server.
This returns a Job ID for the person to look over.

The final POST expects a jobID, which is returned by the previous API call. All it will do is check if
the job has ever started, if it is happening, or if it has finished. Nice and simple.

Theoretically, the user does not ever need to ever interact with any actual things on the website. They simply use
a link to do the API calls, and things happen, occasionally returning things. 

Starting with 'List Available Videos'
All the user does is interact with something that makes '/api/videos' happen. Probably the best thing to do is 
look into the file where the videos are stored, count through each one, add the name to a list, and at the end of the count
return the list of names. Nice and Simple.

With 'Generate Thumbnail'
The user inputs a file name, and first we need to make sure that is actually a file that exists. IF so, then we should
make a function using JavaFX or ffmpeg and grab the first frame. This frame should be (Temporarily?) stored in the server,
long enough to be returned by the call.

With 'Video Processing Job'
The user will need to include the three things previously listed. First, we need to confirm that those parameters are indeed correct - which is done already by the videoProcessor file. However, currently, the exact error is not returned - just that something is wrong.
Regardless, assuming we are setup correctly, all the API will do is convert the POST call into a usable JAR command and send the thing running. We could probably use UUIDs to generate unique job IDs aswell.

Finally, with 'Processing Job Status', 
We would probably need to look at a few things. If there is no UUID, then obvious the job doesnt exist. If there is a match,
but no complete CSV file, then it should still be processing. If it does include a CSV, then it is obviously done. The other one - error - means we probably need to make a file that ISNT a CSV that states there is an error and we make sure to look for it each time.


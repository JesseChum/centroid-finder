AI reccomended improvements for certain files that might be useful

|frameProcessor|
Performance Improvements:
Avoid unnecessary sleep()
Thread.sleep(200) at the end is brittle and slows processing.
Replace with proper synchronization that ensures all FX tasks are done.
Reduce repeated seeking delays
Seeking → 200ms wait → snapshot
This is very slow.

Possible optimizations:
Pre-load or cache frames
Reduce wait duration
Use MediaPlayer’s state events instead of fixed delays
Better handling of BufferedImage conversion
SwingFXUtils.fromFXImage() is expensive.

Fixing the issues why processing time is so long:
No more seek() for every frame (this is the biggest lag source)
No more PauseTransition
No more timeout-based latch per timestamp
No more blocking await()
No more slow step-by-step loop
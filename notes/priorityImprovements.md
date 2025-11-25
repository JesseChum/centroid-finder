FrameProcessor
Performance Improvements:
Avoid unnecessary sleep()
Thread.sleep(200) at the end is brittle and slows processing.
Replace with proper synchronization that ensures all FX tasks are done.
Reduce repeated seeking delays

The way its currently working:
Seeking → 200ms wait → snapshot
This is very slow.

Fixing the issues why processing time is so long:
No more seek() for every frame (this is the biggest lag source)
No more PauseTransition
No more timeout-based latch per timestamp
No more blocking await()
No more slow step-by-step loop


JavaFXVideoRunner
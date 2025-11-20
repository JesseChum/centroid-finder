## API router is the biggest thing to refactor. 
# It is big, clunky, and doesnt handle thumbnails well.
# Processor thumbnail handler should also be reworked.
# to be handle the server's requests

## Add tests for java processor, specifcally, the video
# processor to gurantee it spits out a good CSV file

## Improvements to video processor 
# we can likely choose to not handle every single frame
# but instead of every 1/30th of a frame, or each second of the video.
# Changes between each frame isn't going to be significant, and each second shouldn't even be that big
# this will allow the processor to be much much faster

## Documentation. Alot of it. Change the inline comments to be proper
# documentation with explanations, params, and etc. Preferably near the processor.


Docker plan here goal: these commands should run the full backend server
docker run \
  -p 3000:3000 \
  -v "$VIDEO_DIRECTORY:/videos" \
  -v "$RESULTS_DIRECTORY:/results" \
  ghcr.io/<github-user>/salamander:latest

we need to:
Run node/express
Run java (executing the videoprocessor.jar)
show port 3000
read videos from the videos folder
write the results to the results folder
work with externally mounted host directories
publish to GHCR

There shouldnt be alot to do and there should be no huge change to our code




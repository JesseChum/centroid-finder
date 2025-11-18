## Multi-stage Dockerfile 
#  - maven-builder: builds the processor fat/shaded JAR
#  - node-builder: installs production Node dependencies and prepares server files
#  - runtime: runtime image (JRE + Node) that runs the Node server and bundles the JAR

### Stage 1: Build Java processor
FROM maven:3.9-eclipse-temurin-24-alpine AS maven-builder
RUN apk add --no-cache bash
WORKDIR /build/processor

# copy only pom first and prefetch dependencies for faster rebuilds
COPY processor/pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

# copy sources and build the shaded JAR
COPY processor/src ./src
RUN mvn -B -DskipTests clean package \
    && echo "---- target contents ----" && ls -la target || true \
    && selected=$(ls target/*.jar 2>/dev/null | grep -v 'original-' | head -n 1 || true) \
    && if [ -z "$selected" ]; then echo "ERROR: no shaded jar produced" >&2; exit 1; fi \
    && mv "$selected" target/app.jar

### Stage 2: Build Node server (production deps)
FROM node:20-alpine AS node-builder
RUN apk add --no-cache python3 make g++
WORKDIR /build/server

# copy package files and install production dependencies (inside builder)
COPY server/package*.json ./
RUN npm install --production --no-audit --no-fund

# copy remaining server sources (avoids copying host node_modules into the image)
COPY server/ .

### Stage 3: Runtime image (JRE + Node)
FROM eclipse-temurin:24-jre-alpine
# Ensure both node and npm are available in the runtime image
RUN apk add --no-cache nodejs npm openssl

# non-root user for runtime
RUN addgroup -S app && adduser -S -G app app
WORKDIR /app

# copy prepared server (including node_modules) and the built JAR
COPY --from=node-builder /build/server /app/server
COPY --from=maven-builder /build/processor/target/app.jar /app/processor/app.jar

ENV JAR_PATH=/app/processor/app.jar \
    VIDEOS_DIR=/videos \
    RESULTS_DIR=/results \
    NODE_ENV=production \
    PORT=3000

# data directories
RUN mkdir -p /app/data /videos /results \
    && chown -R app:app /app /videos /results

USER app
WORKDIR /app/server

EXPOSE 3000
VOLUME ["/videos", "/results", "/app/data"]

# run the server (expects a "start" script in package.json or main entrypoint)
CMD ["npm", "start"]

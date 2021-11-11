Run benchmark locally:

```bash
# Build warp app
mvn clean package -DskipTests

# Switch to repository root
cd ../../..

# Run plaintext comparison with quarkus
./tfb --test quarkus-reactive-routes-pgclient warp-java --type plaintext
```

When Benchmark is finished, upload results for example: `results/20211111110429/results.json` to https://tfb-status.techempower.com/share

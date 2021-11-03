# https://tfb-status.techempower.com/share
#  ./tfb --test helidon-se-dbclient --mode debug --type db
# docker run --rm --network=host techempower/postgres
# wrk -H 'Host: localhost' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 15 -c 64 --timeout 8 -t 16 "http://localhost:8080/db"
# wrk -H 'Host: localhost' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 15 -c 128 --timeout 8 -t 16 "http://localhost:8080/db"
# ./tfb --test quarkus-resteasy-reactive-pgclient helidon helidon-se-dbclient --type db
./tfb --test quarkus-resteasy-reactive-pgclient helidon helidon-se-dbclient helidon-se-vertx-pg-client --type db
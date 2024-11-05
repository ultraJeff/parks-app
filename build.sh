./mvnw package -DskipTests
docker buildx build --platform=linux/amd64 -f src/main/docker/Dockerfile.jvm -t jefrankl/parks-app .
docker tag jefrankl/parks-app quay.io/jefrankl/parks-app
docker push quay.io/jefrankl/parks-app
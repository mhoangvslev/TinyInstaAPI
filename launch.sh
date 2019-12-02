echo "Starting local datastore emulator..."
gnome-terminal -- sh -c 'gcloud beta emulators datastore start --host-port=localhost:8484 --no-store-on-disk'

echo "Rebuild project..."
mvn versions:use-latest-releases
mvn clean package -U;

echo "Invoking the Endpoints Frameworks tool ... "
mvn endpoints-framework:openApiDocs
sed -i 's/myapi.appspot.com/tinyinstagram.appspot.com/' target/openapi-docs/openapi.json

echo "Deploying the OpenAPI configuration file..."
gcloud components update
gcloud endpoints services deploy target/openapi-docs/openapi.json
#gcloud services list

echo "Runiing locally the server..."
mvn appengine:deploy


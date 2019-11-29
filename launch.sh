echo "Rebuild project..."
mvn clean package;

echo "Invoking the Endpoints Frameworks tool ... "
mvn endpoints-framework:openApiDocs
sed -i 's/myapi.appspot.com/tinyinstagram.appspot.com/' target/openapi-docs/openapi.json

echo "Deploying the OpenAPI configuration file..."
gcloud components update
#gnome-terminal -x sh -c 'gcloud beta emulators datastore start --host-port=localhost:8484'
gcloud endpoints services deploy target/openapi-docs/openapi.json
#gcloud services list

echo "Runiing locally the server..."
mvn appengine:run
echo "Rebuild project..."
mvn clean package;

echo "Invoking the Endpoints Frameworks tool ... "
mvn endpoints-framework:openApiDocs

echo "Deploying the OpenAPI configuration file..."
gcloud endpoints services deploy target/openapi-docs/openapi.json
gcloud services list

echo "Runiing locally the server..."
mvn appengine:run
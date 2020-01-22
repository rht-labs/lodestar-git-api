# open-management-portal-git-api project

This project uses Quarkus, the Supersonic Subatomic Java Framework.
If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application

Prior to running the app, you need to create a **Personal Access Token** linked to your GitLab account.

### Running in dev mode 

You can run your application in dev mode that enables **live coding** using:
```
export GITLAB_PERSONAL_ACCESS_TOKEN=<GitLab Personal Access Token>
./mvnw quarkus:dev
```

You could edit your bash profile and make your Quarkus quacky by adding this neat emoji alias, then all you need to fire up your  app is run 🦆
```
echo "alias 🦆='./mvnw quarkus:dev -Dquarkus.http.port=8080'" >> ~/.zshrc
source ~/.zshrc

🦆
```

### Running with a profile 

You can run your application using Quarkus profiles using:
```
export GITLAB_PERSONAL_ACCESS_TOKEN=<GitLab Personal Access Token>
export QUARKUS_PROFILE=<Quarkus profile>
./mvnw clean package
java -jar target/open-management-portal-git-api-*-runner.jar
```

## Packaging and running the application

The application is packageable using `./mvnw package`.
It produces the executable `open-management-portal-git-api-1.0.0-SNAPSHOT-runner.jar` file in `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/open-management-portal-git-api-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or you can use Podman to build the native executable using: `./mvnw package -Pnative -Dquarkus.native.container-runtime=podman`.

You can then execute your binary: `./target/open-management-portal-git-api-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image-guide .

## Add basic JDG to OCP
```
oc new-app cache-service \
    -p APPLICATION_USER=omp \
    -p APPLICATION_PASSWORD=<PASSWORD> \
    -p TOTAL_CONTAINER_MEM=4096 \
    -p EVICTION_POLICY=reject \
    -n <PROJECT_NAME>
```

## Configuration
The preferred place to store non-sensitive data is in the application.properties.

Sensitive fields like git repo location, repository id for resdiencies, repository id for the config and the gitlab api token are stored in the secrets.
This info is stored in ocp-s11/labs-test/omp-gitlab-configuration.

Deployment template will read from the above secret and inject following env variables. These are controlled from application.properries, so if a different env name is needed, change in the application propoerties file and the deployment template.

TEMPLATE_REPOSITORY_ID
RESIDENCIES_PARENT_REPOSITORIES_ID
GITLAB_API_URL



# open-management-portal-git-api project

This project uses Quarkus, the Supersonic Subatomic Java Framework.
If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application

Prior to running the app, you need to create a **Personal Access Token** linked to your GitLab account.

### Local Infinispan (if necessary)

Fire 🔥 up a docker instance of Infinispan to work with the app

```
docker run -it -p 11222:11222 -e USER="omp" -e PASS="omp" infinispan/server:10.1
```

### Running in dev mode 

You can run your application in dev mode that enables **live coding** using:
```
export CACHE_CLIENT_INTELLIGENCE=<For mac dev set to BASIC>
export CACHE_USE_AUTH=true
export CONFIG_REPOSITORY_ID =<Git Repo id where the config files are>
export GITLAB_API_URL=<The base url of your git api. ie https://gitlab.com>
export GITLAB_PERSONAL_ACCESS_TOKEN=<GitLab Personal Access Token>
export OMP_LOGGING=DEBUG
export RESIDENCIES_PARENT_REPOSITORIES_ID=<Parent project id where repos will be saved>
export TEMPLATE_REPOSITORY_ID=<Repo where template live>
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
export CACHE_CLIENT_INTELLIGENCE=<For mac dev set to BASIC>
export CACHE_USE_AUTH=true
export CONFIG_REPOSITORY_ID =<Git Repo id where the config files are>
export GITLAB_API_URL=<The base url of your git api. ie https://gitlab.com>
export GITLAB_PERSONAL_ACCESS_TOKEN=<GitLab Personal Access Token>
export OMP_LOGGING=DEBUG
export RESIDENCIES_PARENT_REPOSITORIES_ID=<Parent project id where repos will be saved>
export TEMPLATE_REPOSITORY_ID=<Repo where template live>
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

## Add Infinispan to OCP via the Infinispan Operator

* Cluster admin probably needed for CRD

### Create secret

Create a secret file named `omp-cache-secret.yaml`. This secret will create an `identities.yaml` file on the cache. Take note of the name that must match the app secret By default the user / pass will be omp / omp as defined in `src/main/resources/application.properties` and the secret below.

```
apiVersion: v1
data:
  identities.yaml: Y3JlZGVudGlhbHM6Ci0gdXNlcm5hbWU6IG9tcAogIHBhc3N3b3JkOiBvbXAKLSB1c2VybmFtZTogb3BlcmF0b3IKICBwYXNzd29yZDogTDdFYXZOQklBRXhORDVHdAo=
kind: Secret
metadata:
  labels:
    app: infinispan-secret-identities
    clusterName: omp-cache
    infinispan_cr: omp-cache
  name: omp-cache-secret
type: Opaque
```

### Create the Infinispan cluster file

Create a file named infinispan-cluster.yaml

```
apiVersion: infinispan.org/v1
kind: Infinispan
metadata:
  name: omp-cache
spec:
  replicas: 1
  image: infinispan/server:10.1
  logging:
    categories:
      org.infinispan: info
      org.jgroups: info
  security:
    endpointSecretName: omp-cache-secret
```

### Create the Infinispan Operator

Login to OpenShift with the `oc` client and run the following commands

```
# Install Infinispan Operator running these commands
oc apply -f omp-cache-secret.yaml
oc apply -f https://raw.githubusercontent.com/infinispan/infinispan-operator/master/deploy/rbac.yaml
oc apply -f https://raw.githubusercontent.com/infinispan/infinispan-operator/master/deploy/operator.yaml
oc apply -f https://raw.githubusercontent.com/infinispan/infinispan-operator/master/deploy/crd.yaml

# Create an Infinispan Cluster
oc apply -f infinispan-cluster.yaml
```

## Configuration
The preferred place to store non-sensitive data is in the application.properties.

Sensitive fields like the gitlab token and cluster credentials should be stored in a OpenShift secret at a minimum. Other environment specific information should be stored in environmental variables such as repository id for residencies and repository id for the config.
This info is stored in `ocp-s11/labs-test/omp-gitlab-configuration`.

Deployment template will read from the above secret and inject following env variables. These are controlled from application.properties, so if a different env name is needed, change in the application properties file and the deployment template.

* `TEMPLATE_REPOSITORY_ID`
* `RESIDENCIES_PARENT_REPOSITORIES_ID`
* `GITLAB_API_URL`
* `GITLAB_PERSONAL_ACCESS_TOKEN` (should be secret and a service account)
* `CACHE_SERVICE`
* `CACHE_USER`
* `CACHE_PASS` (should be secret and match the Infinispan operator secret)
* `CACHE_USE_AUTH` set to true
* *

### OpenShift Applier

This section is not guaranteed to be up to date. This project includes an `openshift-applier` inventory. To use it, make sure that you are logged in to the cluster and that you customize the variables in `.applier/inventory/group_vars/all.yml` - namely make sure that `deploy_vars` uses the correct endpoints. Once these are configured, you can deploy the project with:

```bash
cd .applier/
ansible-galaxy install -r requirements.yml --roles-path=roles --force
ansible-playbook apply.yml -i inventory/
```

## Pipeline (deprecated)

The deployment pipeline is running through a `Jenkinsfile` located in the root folder of the project. This `Jenksinfile` is written in groovy.
The pipeline expects the nexus is available nexus:8080. Make sure that nexus is available and accessible to Jenkins.

#### Prepare environment for [ENVIRONMENT] deploy

The first stage is going to set environment vars based on the branch selected to build:

```groovy
master - env.PROJECT_NAMESPACE = "${NAMESPACE_PREFIX}-test"
         env.NODE_ENV = "test"
         env.QUARKUS_PROFILE = "openshift-test"
         env.RELEASE = true

develop.* or feature.* - env.PROJECT_NAMESPACE = "${NAMESPACE_PREFIX}-dev"
                         env.NODE_ENV = "dev"
                         env.QUARKUS_PROFILE = "openshift-dev"
```

#### Ansible

Jenkins will spin up an Ansible agent that will run a playbook called OpenShift Applier (https://github.com/redhat-cop/openshift-applier). The openshift-applier is used to apply OpenShift objects to an OpenShift Cluster. 

This stage is going to download the playbook dependencies using Ansible Galaxy and apply the playbook using **build** as a *filter_tag*. This is going to create the necessary resources for our application build in an OpenShift cluster. 

#### Test/Maven Build/Nexus/OpenShift Build

Jenkins will spin up a Maven agent to test, Maven build, upload to Nexus and start the OpenShift build.

##### Test

```
mvn clean test
```

##### Maven Build

```
mvn clean install
```

##### Static Code Analysis

```
mvn checkstyle:checkstyle
mvn org.jacoco:jacoco-maven-plugin:prepare-agent install -Dmaven.test.failure.ignore=true
```

##### Nexus

```
mvn deploy
```

##### OpenShift Build

The OpenShift build is going to start after the Nexus deployment is complet and successful.

###### OpenShift Atomic Registry

If you're pushing from the master branch the build will create a container image and push it to the Openshift internal registry.

```
oc project ${PIPELINES_NAMESPACE}
oc patch bc ${APP_NAME} -p "{\\"spec\\":{\\"output\\":{\\"to\\":{\\"kind\\":\\"ImageStreamTag\\",\\"name\\":\\"${APP_NAME}:${JENKINS_TAG}\\"}}}}"
oc start-build ${APP_NAME} --from-file=target/${ARTIFACTID}-${VERSION}-runner.jar --follow
```

###### Quay

If you're pushing from a release tag the build will create a container image and push it to Quay.

```
oc project ${PIPELINES_NAMESPACE} # probs not needed
oc patch bc ${APP_NAME} -p "{\\"spec\\":{\\"output\\":{\\"to\\":{\\"kind\\":\\"DockerImage\\",\\"name\\":\\"quay.io/rht-labs/${APP_NAME}:${JENKINS_TAG}\\"}}}}"
oc start-build ${APP_NAME} --from-file=target/${ARTIFACTID}-${VERSION}-runner.jar --follow
```


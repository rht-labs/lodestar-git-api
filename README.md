![Container Build](https://github.com/rht-labs/lodestar-git-api/workflows/Container%20Build/badge.svg) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.redhat.labs%3Alodestar-git-api&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.redhat.labs%3Alodestar-git-api) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.redhat.labs%3Alodestar-git-api&metric=coverage)](https://sonarcloud.io/dashboard?id=com.redhat.labs%3Alodestar-git-api)

# LodeStar - Git API

The Git API for LodeStar.

This API uses GitLab as a repository to store LodeStar resources.

## JSON REST APIs

The JSON REST APIs consist of the following resources:

* Config
* Engagements
* Version

### Config Resource

The config resource exposes an API that allows clients to retrieve a configuration files from GitLab.

#### LodeStar Runtime Configuration File

```
GET  /api/v1/config
GET  /api/v2/config
```

Version 1 of the API returns the file in YAML and version 2 of the API returns the file in JSON.

#### Webhooks Configuration File

```
GET  /api/v2/config/webhooks
```

Returns a JSON representation of the configured webhooks.

### Engagement Resource

The engagement resource exposes an API that allows clients to create or update and engagement resource in GitLab.

```
POST ​  /api​/v1​/engagements
```
Used to create or update an engagement.  This endpoint will create the expected group/project structure in GitLab.  Then, will update the `engagement.json` file if it already exists or create it if it does not.
```
POST   ​/api​/v1​/engagements​/customer​/{customer}​/{engagement}​/hooks
```
Used to create a webhook in GitLab for the project associated to the customer and engagement name combination.
```
GET ​   /api​/v1​/engagements
```
Optional query parameters:

- `includeCommits` - adds commit data to engagement if set to `true`
- `includeStatus`  - adds status data to engagement if set to `true`
- `pagination` - returns a single page of engagements if true.  all if false
- `page` - page number to retrieve from GitLab
- `per_page` - number of engagements to return per page

Pagination Headers Returned:
- `Link` - contains links for rel `first`, `last`, and `next`.  `next` omitted if last page is requested
- `x-first-page` number of first page
- `x-next-page`  number of next page (omitted if last page)
- `x-last-page` number of last page

```
```
GET  ​  /api​/v1​/engagements​/customer​/{customer}​/{engagement}
```
Returns the engagement associated with the customer and engagement name combination.
```
```
GET    ​/api​/v1​/engagements​/customer​/{customer}​/{engagement}​/commits
```
Returns a list of commits associated with the customer and engagement name combination.
```
GET    ​/api​/v1​/engagements​/customer​/{customer}​/{engagement}​/hooks
```
Returns the webhooks for the engagement associated with the customer and engagement name combination.
```
GET    ​/api​/v1​/engagements​/customer​/{customer}​/{engagement}​/status
```
Returns the status data for the engagement associated with the customer and engagement name combination.
```
GET    ​/api​/v1​/engagements​/namespace​/{namespace}
```
Returns the engagement associated with the provided namespace.
```
DELETE ​/api​/v1​/engagements​/customer​/{customer}​/{engagement}
```
Deletes the engagement associated with the customer and engagement name combination.  Will only delete if engagement has not been lost.
```
DELETE ​/api​/v1​/engagements​/hooks
```
Deletes all webhooks in all configured engagement projects.

### Version Resource

The version resource exposes an API that allows a client to determine which version of the application is deployed.

```
GET  /api/v1/version
```

## Configuration

The preferred place to store non-sensitive data is in the application.properties.

Sensitive fields like the gitlab token and cluster credentials should be stored in a OpenShift secret at a minimum. Other environment specific information should be stored in environmental variables such as repository id for engagements and repository id for the config.

Deployment template will read from the above secret and inject following env variables. These are controlled from application.properties, so if a different env name is needed, change in the application properties file and the deployment template.

### Logging

| Name | Example Value | Required |
|------|---------------|----------|
| LODESTAR_LOGGING | DEBUG | False |

### GitLab

| Name | Example Value | Required |
|------|---------------|----------|
| GITLAB_API_URL | https://acmegit.com | True |
| DEPLOY_KEY | 0 | True |

### Commits

| Name | Example Value | Required |
|------|---------------|----------|
| COMMIT_FILTERED_EMAIL_LIST | bot@bot.com,tob@tob.com | False |

### Config Resource 

| Name | Example Value | Required |
|------|---------------|----------|
| CONFIG_REPOSITORY_ID | 1 | True |
| CONFIG_FILE | my-config.yml | True |
| CONFIG_GITLAB_REF | master | False |
| WEBHOOK_FILE | webhooks.yml | False |
| CONFIG_RELOAD | true | False |

### Engagements Resource

| Name | Example Value | Required |
|------|---------------|----------|
| ENGAGEMENTS_REPOSITORY_ID | 2 | True |
| WEBHOOK_DEFAULT_TOKEN | tolkien | False | 
| ENGAGEMENTS_PRESERVE | true | False |

### Version Resource

| Name | Example Value | Required |
|------|---------------|----------|
| GIT_API_GIT_COMMIT | a2adfk | False |
| GIT_API_GIT_TAG | v1.2 | False |

### Other

| Name | Example Value | Required |
|------|---------------|----------|
| ENV_ID | TEST | False |


## Development

See [the deployment README](deployment/README.md) for details on how to spin up a deployment for developing on OpenShift.


## Running the application

Prior to running the app, you need to create a **Personal Access Token** linked to your GitLab account.

You could edit your bash profile and make your Quarkus quacky by adding this neat emoji alias, then all you need to fire up your  app is run 🦆
```
echo "alias 🦆='./mvnw quarkus:dev -Dquarkus.http.port=8080'" >> ~/.zshrc
source ~/.zshrc

🦆
```

### Running the Application 

You can run your application using Quarkus using:

```

# logging
export LODESTAR_LOGGING=DEBUG

# gitlab
export GITLAB_API_URL=<The base url of your git api. ie https://gitlab.com>
export GITLAB_PERSONAL_ACCESS_TOKEN=<GitLab Personal Access Token>
export DEPLOY_KEY=<Deployment Key for Engagements>

# config 
export CONFIG_REPOSITORY_ID=<Git Repo id where the config files are>

# engagements
export ENGAGEMENTS_REPOSITORY_ID=<Parent project id where repos will be saved>

# package the application
./mvnw clean package

# run the application
java -jar target/lodestar-git-api-*-runner.jar
```

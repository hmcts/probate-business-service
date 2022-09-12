# Probate Business Service

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=uk.gov.hmcts.probate%3Abusiness-service&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=uk.gov.hmcts.probate%3Abusiness-service) [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=uk.gov.hmcts.probate%3Abusiness-service&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=uk.gov.hmcts.probate%3Abusiness-service) [![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=uk.gov.hmcts.probate%3Abusiness-service&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=uk.gov.hmcts.probate%3Abusiness-service) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=uk.gov.hmcts.probate%3Abusiness-service&metric=coverage)](https://sonarcloud.io/summary/new_code?id=uk.gov.hmcts.probate%3Abusiness-service)

The probate business service provides useful business logic to thefrontend service and others via RESTful APIs.

## Overview

<p align="center">
<a href="https://github.com/hmcts/probate-frontend">probate-frontend</a> • <a href="https://github.com/hmcts/probate-caveats-frontend">probate-caveats-frontend</a> • <a href="https://github.com/hmcts/probate-back-office">probate-back-office</a> • <a href="https://github.com/hmcts/probate-orchestrator-service">probate-orchestrator-service</a> • <b><a href="https://github.com/hmcts/probate-business-service">probate-business-service</a></b> • <a href="https://github.com/hmcts/probate-submit-service">probate-submit-service</a> • <a href="https://github.com/hmcts/probate-persistence-service">probate-persistence-service</a>
</p>

<br>

<p align="center">
  <img src="https://raw.githubusercontent.com/hmcts/reform-api-docs/master/docs/c4/probate/images/structurizr-probate-overview.png" width="800"/>
</p>

<details>
<summary>Citizen view</summary>
<img src="https://raw.githubusercontent.com/hmcts/reform-api-docs/master/docs/c4/probate/images/structurizr-probate-citizen.png" width="700">
</details>
<details>
<summary>Caseworker view</summary>
<img src="https://raw.githubusercontent.com/hmcts/reform-api-docs/master/docs/c4/probate/images/structurizr-probate-caseworker.png" width="700">
</details>

## Getting Started
### Prerequisites
- Java 11
- Gradle
- Docker

### Running the application
#### Building and Running the Business Service
Install dependencies and build the service by executing the following command:
```
$ ./gradlew clean build
```

Once the build has completed, you will find the new *.jar* in `build/libs`. You can run the *.jar* with the following command:
```
$ java -jar build/libs/business-service-1.0.1.jar
```

### Local Development using docker

## Docker environment

Bring up the environment:

```
# rebuild every time you make changes
./gradlew assemble

# first time only
npx @hmcts/probate-dev-env --create

# start the dev env
npx @hmcts/probate-dev-env
```

### API documentation

API documentation is provided with Swagger:
 - `http://localhost:4400/swagger-ui.html` - UI to interact with the API resources

## Developing

### Unit tests

To run all unit tests please execute the following command:

```bash
$ ./gradlew test
```

### Coding style tests

To run all checks (including unit tests) please execute the following command:

```bash
$ ./gradlew check
```

## Versioning

We use [SemVer](http://semver.org/) for versioning.
For the versions available, see the tags on this repository.

## Troubleshooting

### IDE Settings

#### Project Lombok Plugin
When building the project in your IDE (eclipse or IntelliJ), Lombok plugin will be required to compile.

For IntelliJ IDEA, please add the Lombok IntelliJ plugin:
* Go to `File > Settings > Plugins`
* Click on `Browse repositories...`
* Search for `Lombok Plugin`
* Click on `Install plugin`
* Restart IntelliJ IDEA

Plugin setup for other IDE's are available on [https://projectlombok.org/setup/overview]

#### JsonMappingException when running tests in your IDE
Add the `-parameters` setting to your compiler arguments in your IDE (Make sure you recompile your code after).
This is because we use a feature of jackson for automatically deserialising based on the constructor.
For more info see: https://github.com/FasterXML/jackson-modules-java8/blob/a0d102fa0aea5c2fc327250868e1c1f6d523856d/parameter-names/README.md

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details.


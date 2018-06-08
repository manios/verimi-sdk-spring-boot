# sdk-integration-sample

## Description
This is a sample minimal implementation of the service provider integration
- Redirects to the authorization server
- Replies the authorization code
- Web server POST a token request with the authorization code
- Decode the token content

## Dependent services
- service-registry
- configuration-service
- user-status-command
- user-status-consume
- authentication-query-service ( Start this before user-status-query as it will fail to startup without this service )
- user-status-query
- authorization-service
- api-gateway ( optional if the authorization flow is going through the API Gateway )

## How to run
- mvn spring-boot:run

## Access the application

[http://localhost:9000](http://localhost:9000 "Jump to the service provider welcome page")

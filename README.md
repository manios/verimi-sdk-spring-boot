# Verimi Spring Boot Integration Sample

![Verimi logo](https://verimi.de/sandbox/img/logo.svg)

## Description
This is a sample minimal implementation of the service provider integration as downloaded from [https://verimi.de/sdk-example/sdk-integration-example.zip](https://verimi.de/sdk-example/sdk-integration-example.zip) on 2018-06-08T13:00:00+03:00. It demonstrates the following flow:
 
1. Redirects to the authorization server in order to login.
1. Replies with the authorization ```code```
1. Web server then uses the authorization ```code``` in a ```POST``` token request and receives the ```access_token```.
1. Decodes the ```access_token``` content (not working with current sandbox environment)
1. Retrieves the user baskets containing user information and prints them to log. (not working with current sandbox environment)

## API environments

Verimi offers two (2) environments for testing:

### 1 . Sandbox

Sandbox environment offers only the ability to login and get an ```access_token```. You cannot perform anything else there. Sandbox environment is freely accessible. You can find documentation about it [here](https://verimi.de/sandbox/html/sdk.html).

### 2. UAT - User Acceptance Testing

This is a testing environment which offers the complete functionality as described in the Verimi [API documentation](https://verimi.de/sandbox/html/sdk.html). This environment **is not** publicly accessible. In order to be granted access to the API you need to <u>contact Verimi</u> who will provide you with:

1. A personal client certificate which is a ```.p12``` file with a private key.
1. A ```client_id```.
1. A ```client_secret```.
1. The redirect urls you and Verimi have agreed for testing. For example if your server resides in ```http://localhost:8000``` and in ```https://mysite.com``` you have to inform Verimi explicitly about this information in order to **whitelist** these URLs.

By acquiring the aforementioned information, you can register a new account in [https://verimi-uat.coretransform.com/login](https://verimi-uat.coretransform.com/login). You can use whatever email you want as the email will be sent to a [Mailhog](https://github.com/mailhog/MailHog) test mail server located in: [https://sp-verimi-uat.coretransform.com/mailhog](https://sp-verimi-uat.coretransform.com/mailhog).

## How to run

### Requirements

1. JDK 1.8 installed
1. Apache Maven or an IDE which supports Maven , such as Eclipse Spring Tool Suite, IntelliJ IDEA, Netbeans.

### Sandbox

In order to run this sample Spring Boot application using [Verimi Sandbox](https://verimi.de/sandbox) environment you have to proceed to the following steps:
 
1. Edit ```src/main/resources/application.properties``` with a configuration like the following

    ```conf
    server.port: 9000

    oauth.api_environment=sandbox
    oauth.redirect_uri=http://localhost:9000/authorization/code?parameter=dummy1
    oauth.client_id=ACME
    oauth.client_secret=G|41|0an18ZIs_w

    # SANDBOX ENVIRONMENT
    oauth.authorization_server_protocol=https
    oauth.authorization_server_host=verimi.com
    oauth.authorization_server_port=443/dipp/api
    
    keyStore.location=
    keyStore.password=
    
    logging.level.com.dipp=DEBUG
    ```
1. Run :
   - From a shell: ```mvn spring-boot:run``` in order to compile and execute the application.
   - Run the ```SpringBootWebApplication``` class from your favourite IDE.

If everything is OK, then proceed to the next section.


### UAT - User Acceptance Testing

In order to run this sample Spring Boot application using Verimi UAT - User Acceptance Testing environment you have to proceed to the following steps:

1. Edit ```src/main/resources/application.properties``` with a configuration like the following

    ```conf
    server.port: 9000

    # This property accepts 2 values: "uat" or "sandbox". Default: "sandbox" 
    oauth.api_environment=uat
    oauth.redirect_uri=http://localhost:9000/authorization/code?parameter=dummy1
    oauth.client_id=clientIdVerimiGaveYou
    oauth.client_secret=clientSecretVerimiGaveYou

    # UAT ENVIRONMENT
    oauth.authorization_server_protocol=https
    oauth.authorization_server_host=verimi-uat.coretransform.com
    oauth.authorization_server_port=443/dipp/api

    ### Keystore
    keyStore.location=/home/sbobos/verimi-sdk-spring-boot/verimi-client-cert.p12
    keyStore.password=mysuperpassword


    logging.level.com.dipp=DEBUG
    ```
- Run :
   - From a shell: ```mvn spring-boot:run``` in order to compile and execute the application.
   - Run the ```SpringBootWebApplication``` class from your favourite IDE.

If everything is OK, then proceed to the next section.


## Access the application

The application will load on localhost on port 9000 by default (you can configure a different port in the ```src/main/resources/application.properties```. Open a browser on [http://localhost:9000](http://localhost:9000) and press the "Login with Verimi" button to proceed.

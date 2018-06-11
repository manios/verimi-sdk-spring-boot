# Verimi Spring Boot Integration Sample

![Verimi logo](https://verimi.de/sandbox/img/logo.svg)

## Description
This is a sample minimal implementation of the service provider integration as downloaded from [https://verimi.de/sdk-example/sdk-integration-example.zip](https://verimi.de/sdk-example/sdk-integration-example.zip) on 2018-06-08T13:00:00+03:00. It demonstrates the following flow:
 
1. Redirects to the authorization server
1. Replies the authorization code
1. Web server POST a token request with the authorization code
1. Decode the token content (not working with current sandbox environment)

## How to run

In order to run this sample Spring Boot application using [Verimi Sandbox](https://verimi.de/sandbox) environment you have to proceed to the following steps:
 
- Create a [custom .jks keystore](https://www.sslshopper.com/article-how-to-create-a-self-signed-certificate-using-java-keytool.html) using the command:

   ```
   keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass changeit -validity 360 -keysize 2048
   ```
   
   If you want to test and debug this .jks file using cUrl you can follow [this](https://stackoverflow.com/questions/32253909/curl-with-a-pkcs12-certificate-in-a-bash-script) approach.
- Install Maven
- Configure ```src/main/resources/application.properties``` with the location and the password of the newly generated .jks file. Example configuration:

   ```
   trustStore.location=/home/manios/javaprojects/verimi-sdk-integration-sample/keystore.jks
   trustStore.password=changeit
   
   keyStore.location=/home/manios/javaprojects/verimi-sdk-integration-sample/keystore.jks
   keyStore.password=changeit
   ```
- Run ```mvn spring-boot:run``` in order to compile and execute the application.

If everything is OK, then proceed to the next section.

## Access the application

The application will load on localhost on port 9000 be default. Open a browser on [http://localhost:9000](http://localhost:9000) and press the "Login with Verimi" button to proceed.

# reCAPTCHA-V2-java
[![Build Status](https://opensource.triology.de/jenkins/buildStatus/icon?job=triologygmbh-github/reCAPTCHA-V2-java/develop)](https://opensource.triology.de/jenkins/blue/organizations/jenkins/triologygmbh-github%2FreCAPTCHA-V2-java/branches/)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=de.triology.recaptchav2-java%3Arecaptchav2-java&metric=alert_status)](https://sonarcloud.io/dashboard?id=de.triology.recaptchav2-java%3Arecaptchav2-java)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=de.triology.recaptchav2-java%3Arecaptchav2-java&metric=coverage)](https://sonarcloud.io/dashboard?id=de.triology.recaptchav2-java%3Arecaptchav2-java)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=de.triology.recaptchav2-java%3Arecaptchav2-java&metric=sqale_index)](https://sonarcloud.io/dashboard?id=de.triology.recaptchav2-java%3Arecaptchav2-java)
Lightweight Java Bindings for reCAPTCHA V2. See [Verifying the user's response  |  reCAPTCHA  |  Google Developers](https://developers.google.com/recaptcha/docs/verify).
Why lightweight? Provides a minimalist API and imposes no transitive dependencies on its user, except for [SLF4J](https://www.slf4j.org/).

## Prerequisites

* Get a public and a secret API key [from Google](http://www.google.com/recaptcha/admin).
* Set up your client side to display the reCAPTCHA V2 widget, **containing the public API key**, as described [here](https://developers.google.com/recaptcha/docs/display).
* Send the response token to your server.

## Usage

Add the [latest stable version](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.triology.recaptchav2-java%22%20AND%20a%3A%22recaptchav2-java%22) to the dependency management tool of your choice.

```XML
<dependency>
    <groupId>de.triology.recaptchav2-java</groupId>
    <artifactId>recaptchav2-java</artifactId>
    <version>1.0.2</version>
</dependency>
```

[![Maven Central](https://img.shields.io/maven-central/v/de.triology.recaptchav2-java/recaptchav2-java.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.triology.recaptchav2-java%22%20AND%20a%3A%22recaptchav2-java%22)

You can also get snapshot versions from our [snapshot repository](https://oss.sonatype.org/content/repositories/snapshots/de/triology/recaptchav2-java/recaptchav2-java/) (for the most recent commit on develop branch).
To do so, add the following repo to your `pom.xml` or `settings.xml`:
```xml
<repository>
    <id>snapshots-repo</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <releases><enabled>false</enabled></releases>
    <snapshots><enabled>true</enabled></snapshots>
</repository>
```

On your server:
* Create a new instance of `ReCaptcha`,
* passing the reCAPTCHA **private API key** (the one beloging to the public key used in your client code to generate the response).
* Then validate the token sent by the client, you'll receive a boolean response.

It's as simple as that.

````java
String response = "03AEHxwuyM-dll21GpJuJ65tGT6SVEvQEO3tvLfyxbbgBCaSdOLRQBT4Py-jMjGxplhE1wo7nn7Y6zRNgqUufFTnYDdqzYDTupfZkgx0LppSC3_eBKkODMopBaSBeeGMlt_wzkqWes5tAo34t2LmS0fGdwsE_feGJ_NsrB29NsUNAO78FGyL5DpL7f8K5dnh9Q_6QiN5Qg0MapUEu2w30r-GOI7MfVDMF7qk7wDwbM8uZmoIMn8AenNVKsZY0yEP6ghGVTBhtFvBVaD6jiHXeKztnAX1oLAvPa0jh9sJe20Dwk4jtmuemWKLI";
String secret = "sMSd8L8jlFrKGHdtbXePOphPfhJO_oA4A0sfvw0i";

new ReCaptcha(secret).isValid(response);
````

For trying out, see [TestServer](src/test/java/de/triology/recaptchav2java/TestServer.java).
Run it with these env vars

* SECRET
* SITE_KEY
* PORT

### Error Handling
In case there are not technical problems `Recaptcha.isValid()` always returns a boolean. Otherwise a `ReCaptchaException` is thrown.
If you need insight into the underlying HTTP traffic you best set the log level of all loggers `de.triology.recaptchav2java` to `TRACE` using your favorite [SLF4J](https://www.slf4j.org/) implementation.


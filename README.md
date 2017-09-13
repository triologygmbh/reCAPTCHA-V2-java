# reCAPTCHA-V2-java
[![Build Status](https://opensource.triology.de/jenkins/buildStatus/icon?job=triologygmbh-github/reCAPTCHA-V2-java/master)](https://opensource.triology.de/jenkins/blue/organizations/jenkins/triologygmbh-github%2FreCAPTCHA-V2-java/branches)
[![JitPack](https://jitpack.io/v//triologygmbh/reCAPTCHA-V2-java.svg)](https://jitpack.io/#triologygmbh/reCAPTCHA-V2-java)

Lightweight Java Bindings for reCAPTCHA V2. See [Verifying the user's response  |  reCAPTCHA  |  Google Developers](https://developers.google.com/recaptcha/docs/verify).  
Why lightweight? Provides a minimalist API and imposes no transitive dependencies on its user, except for [SLF4J](https://www.slf4j.org/).

## Prerequisites

* Get a public and a secret API key [from Google](http://www.google.com/recaptcha/admin).   
* Set up your client side to display the reCAPTCHA V2 widget, **containing the public API key**, as described [here](https://developers.google.com/recaptcha/docs/display).
* Send the response token to your server.

## Usage 

Get it via [JitPack](https://jitpack.io/#triologygmbh/reCAPTCHA-V2-java), for example using maven.

Add the following maven repository to your POM.xml

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Then add the actual dependency

```xml
<dependency>
    <groupId>com.github.triologygmbh</groupId>
    <artifactId>reCAPTCHA-V2-java</artifactId>
    <version>1.0.0</version>
</dependency>
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

### Error Handling
In case there are not technical problems `Recaptcha.isValid()` always returns a boolean. Otherwise a `ReCaptchaException` is thrown.  
If you need insight into the underlying HTTP traffic you best set the log level of all loggers `de.triology.recaptchav2java` to `TRACE` using your favorite [SLF4J](https://www.slf4j.org/) implementation.


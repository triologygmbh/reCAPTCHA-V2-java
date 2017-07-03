/**
 * MIT License
 *
 * Copyright (c) 2017 TRIOLOGY GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.triology.recaptchav2java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Function;

/**
 * HTTP-related logic for reCAPTCHA.
 */
class Http {
    private static final Logger LOG = LoggerFactory.getLogger(Http.class);
    private Http() {}

    static String post(String url, String urlParameters) {
        return withConnectionTo(url, connection -> {

            sendPostRequest(connection, urlParameters);

            return receiveResponse(connection);
        });
    }

    private static String withConnectionTo(String url, Function<HttpURLConnection, String> runnable) {
        HttpURLConnection con = null;
        try {
            LOG.trace("Opening connection to {}", url);
            con = openConnection(url);
            return runnable.apply(con);
        } finally {
            if (con != null) {
                LOG.trace("Closing connection to {}", url);
                con.disconnect();
            }
        }
    }

    private static HttpURLConnection openConnection(String url) {
        try {
            return (HttpURLConnection) new URL(url).openConnection();
        } catch (IOException e) {
            throw new ReCaptchaException("Unable to create URL for posting", e);
        }
    }

    private static void sendPostRequest(HttpURLConnection con, String bodyParams) {
        try {
            sendPostRequestWithExceptions(con, bodyParams);
        } catch (IOException e) {
            throw new ReCaptchaException("I/O error while sending the POST request", e);
        }
    }

    private static void sendPostRequestWithExceptions(HttpURLConnection con, String parameters) throws IOException {
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        LOG.trace("Posting parameters {} to url {}", parameters, con.getURL());
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(parameters);
            wr.flush();
        }
    }

    private static String receiveResponse(HttpURLConnection con) {
        try {
            return receiveResponseWithExceptions(con);
        } catch (IOException e) {
            throw new ReCaptchaException("I/O error receiving the response ", e);
        }
    }

    private static String receiveResponseWithExceptions(HttpURLConnection con) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            LOG.trace("Receiving response from {}. Status Code: {}", con.getURL(),  con.getResponseCode());
            String response = toString(in);
            LOG.trace("Received response from {}: {}", con.getURL(), response);
            return response;
        }
    }

    private static String toString(BufferedReader in) throws IOException {
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        return response.toString();
    }
}

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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;


public class TestServer {

    private static final String SECRET = System.getenv("RECAPTCHA_SECRET");
    private static final String SITE_KEY = System.getenv("RECAPTCHA_SITE_KEY");
    public static final int PORT = Integer.parseInt(System.getenv("PORT"));

    public static void main(String[] args) throws Exception {
        if (SECRET == null || SECRET.isEmpty()) {
            System.err.println("Missing Env Var \"RECAPTCHA_SECRET\"");
            System.exit(1);
        }
        if (SITE_KEY == null || SITE_KEY.isEmpty()) {
            System.err.println("Missing Env Var \"RECAPTCHA_SITE_KEY\"");
            System.exit(2);
        }
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new IndexHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Starting Server on http://localhost:" + PORT);
    }

    static class IndexHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            if (t.getRequestMethod().equals("POST")) {
                handlePost(t);
            } else {
                printIndex(t);
            }
        }

        private void printIndex(HttpExchange t) throws IOException {
            String response = "<html>\n" +
                "  <head>\n" +
                "    <title>reCAPTCHA demo: Simple page</title>\n" +
                "    <script src=\"https://www.google.com/recaptcha/api.js\" async defer></script>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <form action=\"?\" method=\"POST\">\n" +
                "      <div class=\"g-recaptcha\" data-sitekey=\"" + SITE_KEY + "\"></div>\n" +
                "      <br/>\n" +
                "      <input type=\"submit\" value=\"Submit\">\n" +
                "    </form>\n" +
                "  </body>\n" +
                "</html>\n";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void handlePost(HttpExchange t) throws IOException {
            StringWriter writer = new StringWriter();
            IOUtils.copy(t.getRequestBody(), writer,  "UTF-8");

            String captchaResponseToken = writer.toString().split("=")[1];
            boolean isValid = new ReCaptcha(SECRET).isValid(captchaResponseToken);
            System.out.println("Response Valid:" + isValid + ". Response token: " + captchaResponseToken);
            String response = "Token isValid: " + isValid;

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}

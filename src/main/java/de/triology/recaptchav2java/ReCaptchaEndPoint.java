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

import de.triology.recaptchav2java.com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the connection to the reCAPTCHA V2 endpoint.
 * See https://developers.google.com/recaptcha/docs/verify
 */
class ReCaptchaEndPoint {
    private static final Logger LOG = LoggerFactory.getLogger(ReCaptchaEndPoint.class);

    @VisibleForTesting
    static final String URL_PROTOCOL_HOST = "https://www.google.com";
    @VisibleForTesting
    static final String URL_QUERY_PART = "/recaptcha/api/siteverify";
    private static final String URL_ABSOLUTE = URL_PROTOCOL_HOST + URL_QUERY_PART;

    private static final String POST_PARAM_SECRET = "secret";
    private static final String POST_PARAM_TOKEN = "response";
    private final String secret;

    ReCaptchaEndPoint(String secret) {
        this.secret = secret;
    }

    boolean verify(String token) {
        LOG.debug("Verifying token {}", token);
        String response = Http.post(getAbsoluteUrl(), createUrlParameters(token));
        boolean success = new ReCaptchaJson(response).isSuccess();
        LOG.debug("Received response for token {}: success={}", token, success);
        return success;
    }

    private String createUrlParameters(String token) {
        return POST_PARAM_SECRET + "=" + secret + "&" +
            POST_PARAM_TOKEN + "=" + token;
    }

    @VisibleForTesting
    protected String getAbsoluteUrl() {
        return URL_ABSOLUTE;
    }
}

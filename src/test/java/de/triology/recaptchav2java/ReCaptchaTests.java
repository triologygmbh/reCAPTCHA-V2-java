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

class ReCaptchaTests {

    private static final String BODY_TEMPLATE = "{\n"
        + " \"success\": %s,\n"                             // true|false
        + " \"challenge_ts\": \"2017-06-26T09:31:42+0200\",\n"  // timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZ)
        + " \"hostname\": \"localhost\""                      // the hostname of the site where the reCAPTCHA was solved
        + "%s"                                              // optional error codes: ,"error-codes": [...]
        + "}";

    private static final String BODY_ERROR_CODES_TEMPLATE = ",\n\"error-codes\": [%s]";

    private ReCaptchaTests() {
    }

    static String createBodySuccess() {
        return String.format(BODY_TEMPLATE, "true", "");
    }

    static String createBodyFailure() {
        return String.format(BODY_TEMPLATE, "false",
            String.format(BODY_ERROR_CODES_TEMPLATE, "\"invalid-input-secret\", \"invalid-input-response\""));
    }
}

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

/**
 * Java abstraction for verifying a response token generate by Google's reCAPTCHA V2 client widget on server side.
 */
@SuppressWarnings("WeakerAccess") // This is the public API!
public class ReCaptcha {
    private final String secret;

    /**
     * Creates a new ReCaptcha service using a specific secret key.
     *
     * @param secret the server-side secret. Most not be {@code null}!
     */
    public ReCaptcha(String secret) {
        this.secret = secret;
    }

    /**
     * Validates a response token generate by Google's reCAPTCHA V2 client widget.
     *
     * @param captchaResponseToken the result of a captcha challenge.
     * @return {@code true} if the challenge was successful, otherwise {@code false}
     * @throws ReCaptchaException when something technically went wrong during captcha validation.
     */
    public boolean isValid(String captchaResponseToken) {
        return createReCaptchaEndPoint().verify(captchaResponseToken);
    }

    @VisibleForTesting
    protected ReCaptchaEndPoint createReCaptchaEndPoint() {
        return new ReCaptchaEndPoint(secret);
    }
}

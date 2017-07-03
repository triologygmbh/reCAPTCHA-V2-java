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


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.google.common.truth.Truth.assertThat;

import static de.triology.recaptchav2java.ReCaptchaTests.*;
import static de.triology.recaptchav2java.ReCaptchaEndPoint.*;

public class ReCaptchaTest {

    private static final String EXPECTED_RESPONSE_TOKEN = "SomeToken";
    private static final String EXPECTED_SECRET = "topSecret";
    private static final String IRRELEVANT_SECRET = "not relvant for this test";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Test
    public void isValid() throws Exception {
        ReCaptcha recaptcha = mockResponseSuccess();

        boolean reCaptchaResponse = recaptcha.isValid(EXPECTED_RESPONSE_TOKEN);

        assertThat(reCaptchaResponse).isTrue();
        verifyParameters();
    }

    @Test
    public void isValidFalse() throws Exception {
        ReCaptcha recaptcha = mockResponseFailure();

        boolean reCaptchaResponse = recaptcha.isValid(EXPECTED_RESPONSE_TOKEN);

        assertThat(reCaptchaResponse).isFalse();
        verifyParameters();
    }

    @Test
    public void isValidTransportErrorReceiving() throws Exception {
        ReCaptcha recaptcha = mockErrorReceiving();
        expectedException.expect(ReCaptchaException.class);
        expectedException.expectMessage("receiving");

        recaptcha.isValid(EXPECTED_RESPONSE_TOKEN);
    }

    @Test
    public void isValidTransportErrorSending() throws Exception {
        expectedException.expect(ReCaptchaException.class);
        expectedException.expectMessage("sending");

        ReCaptcha reCaptcha = mockErrorSending();

        reCaptcha.isValid(EXPECTED_RESPONSE_TOKEN);
    }

    private void verifyParameters() {
        verify(postRequestedFor(urlEqualTo(URL_QUERY_PART))
            .withRequestBody(containing("secret="+ EXPECTED_SECRET))
            .withRequestBody(containing("response="+EXPECTED_RESPONSE_TOKEN))
        );
    }

    private ReCaptcha mockErrorReceiving() {
        wireMockRule.setGlobalFixedDelay(Integer.MAX_VALUE);
        return new ReCaptchaWireMock();
    }

    private ReCaptcha mockErrorSending() {
        return new ReCaptchaFailingToConnect();
    }

    private ReCaptcha mockResponseSuccess() {
        mockResponse(createBodySuccess());
        return new ReCaptchaWireMock();
    }

    private ReCaptchaWireMock mockResponseFailure() {
        mockResponse(createBodyFailure());
        return new ReCaptchaWireMock();
    }

    private void mockResponse(String body) {
        stubFor(post(URL_QUERY_PART)
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(body)
                .withStatus(200)));
    }

    private class ReCaptchaWireMock extends ReCaptcha {
        ReCaptchaWireMock() {
            // Overwritten in createReCaptchaEndPoint()
            super(IRRELEVANT_SECRET);
        }

        @Override
        protected ReCaptchaEndPoint createReCaptchaEndPoint() {
            return new ReCaptchaEndPoint(EXPECTED_SECRET) {
                @Override
                protected String getAbsoluteUrl() {
                    return super.getAbsoluteUrl().replace(
                        URL_PROTOCOL_HOST,
                        String.format("http://localhost:%s", wireMockRule.port()));
                }
            };
        }
    }

    private static class ReCaptchaFailingToConnect extends ReCaptcha {
        ReCaptchaFailingToConnect() {
            // Overwritten in createReCaptchaEndPoint()
            super(IRRELEVANT_SECRET);
        }

        @Override
        protected ReCaptchaEndPoint createReCaptchaEndPoint() {
            // Secret is not needed, because request fails anyway
            return new ReCaptchaEndPoint(IRRELEVANT_SECRET) {
                @Override
                protected String getAbsoluteUrl() {
                    return super.getAbsoluteUrl().replace(URL_PROTOCOL_HOST, "http://localhost:0");
                }
            };
        }
    }

}

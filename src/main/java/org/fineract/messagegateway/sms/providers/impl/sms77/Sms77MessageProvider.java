/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.fineract.messagegateway.sms.providers.impl.sms77;

import com.squareup.okhttp.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.fineract.messagegateway.configuration.HostConfig;
import org.fineract.messagegateway.constants.MessageGatewayConstants;
import org.fineract.messagegateway.exception.MessageGatewayException;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.domain.SMSMessage;
import org.fineract.messagegateway.sms.providers.SMSProvider;
import org.fineract.messagegateway.sms.util.SmsMessageStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "sms77")
public class Sms77MessageProvider extends SMSProvider {

    private static final Logger logger = LoggerFactory.getLogger(Sms77MessageProvider.class);
    private static final String SCHEME = "https";

    private HashMap<String, OkHttpClient> restClients = new HashMap<>();
    private final String callBackUrl;

    private final StringBuilder builder;

    @Autowired
    public Sms77MessageProvider(final HostConfig hostConfig) {
        callBackUrl = String.format("%s://%s:%d/sms77/report/",
                hostConfig.getProtocol(), hostConfig.getHostName(), hostConfig.getPort());
        logger.info("Registering call back to sms77:" + callBackUrl);
        builder = new StringBuilder();
    }

    @Override
    public void sendMessage(SMSBridge smsBridgeConfig, SMSMessage message)
            throws MessageGatewayException {
        String statusCallback = callBackUrl + message.getId();
        OkHttpClient okHttpClient = getRestClient(smsBridgeConfig);
        String url = smsBridgeConfig.getConfigValue(
                MessageGatewayConstants.PROVIDER_URL
        ) + "/sms";
        String providerAuthToken = smsBridgeConfig.getConfigValue(
                MessageGatewayConstants.PROVIDER_AUTH_TOKEN
        );
        logger.info("URL.....{}", url);
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(
                    mediaType,
                    "{\"to\": " + message.getMobileNumber()
                            + ",\"from\": " + smsBridgeConfig.getPhoneNo()
                            + ",\"text\": \"" + message.getMessage()
                            + ",\"return_msg_id\": \"1\"}"
            );
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("SentWith", "message-gateway")
                    .addHeader("X-Api-Key", providerAuthToken)
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            logger.info(responseBody);
            String lines[] = responseBody.split("\\r?\\n");
            String apiCode = lines[0];
            String externalId = lines[1];
            message.setExternalId(externalId);
            SmsMessageStatusType deliveryStatus = "100" == apiCode
                    ? SmsMessageStatusType.SENT : SmsMessageStatusType.FAILED;
            message.setDeliveryStatus(deliveryStatus.getValue());
            message.setDeliveredOnDate(new Date());
            message.setResponse(responseBody);
        } catch (IOException e) {
            throw new MessageGatewayException(e.getMessage());
        }
    }

    private OkHttpClient getRestClient(final SMSBridge smsBridge) {
        String authorizationKey = encodeBase64(smsBridge);
        OkHttpClient client = this.restClients.get(authorizationKey);
        if (client == null) {
            client = this.get(smsBridge);
            this.restClients.put(authorizationKey, client);
        }
        return client;
    }

    OkHttpClient get(final SMSBridge smsBridgeConfig) {
        logger.debug("Creating a new sms77 Client ....");

        final OkHttpClient client = new OkHttpClient();
        return client;
    }
}

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

import org.fineract.messagegateway.sms.domain.SMSMessage;
import org.fineract.messagegateway.sms.repository.SmsOutboundMessageRepository;
import org.fineract.messagegateway.sms.util.CallbackEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms77")
public class Sms77ApiResource implements ApplicationEventPublisherAware {

    private static final Logger logger = LoggerFactory.getLogger(Sms77ApiResource.class);

    private final SmsOutboundMessageRepository smsOutboundMessageRepository;

    private ApplicationEventPublisher publisher;

    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Autowired
    public Sms77ApiResource(final SmsOutboundMessageRepository smsOutboundMessageRepository) {
        this.smsOutboundMessageRepository = smsOutboundMessageRepository;
    }

    @RequestMapping(
            value = "/report",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    public ResponseEntity<Void> updateDeliveryStatus(
            @ModelAttribute final Sms77ResponseData report) {
        String externalId = report.getMsg_id();
        SMSMessage message = this.smsOutboundMessageRepository.findByExternalId(externalId);
        Long messageId = message.getInternalId();

        if (message != null) {
            logger.debug("Status Callback received from sms77 for " + messageId + " with status:" + report.getStatus());
            message.setDeliveryStatus(Sms77Status.smsStatus(report.getStatus()).getValue());
            this.smsOutboundMessageRepository.save(message);
            publisher.publishEvent(new CallbackEvent(this, "UPDATE", message.getExternalId()));
        } else {
            logger.info("Message with Message id " + messageId + " Not found");
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

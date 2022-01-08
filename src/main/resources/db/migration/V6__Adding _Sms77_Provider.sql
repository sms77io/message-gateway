--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership. The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License. You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied. See the License for the
-- specific language governing permissions and limitations
-- under the License.
--

INSERT INTO `m_sms_bridge` (`tenant_id`, `tenant_phone_no`, `provider_key`, `country_code`, `provider_name`, `description`)
VALUES ((select id from m_tenants where tenant_id = 'default'), '555-1212', 'sms77', '+49', 'sms77', 'sms77 SMS gateway');

INSERT INTO `m_sms_bridge_configuration` (`sms_bridge_id`, `config_name`, `config_value`)
VALUES ((SELECT id from m_sms_bridge WHERE provider_key = 'sms77'), 'Provider_Url', 'https://gateway.sms77.io/api');

INSERT INTO `m_sms_bridge_configuration` (`sms_bridge_id`, `config_name`, `config_value`)
VALUES ((SELECT id from m_sms_bridge WHERE provider_key = 'sms77'), 'Provider_Auth_Token', '');


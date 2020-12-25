/*
 * Copyright 2020 JSquad AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.jsquad.rest;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.jsquad.batch.SlowMockBatch;
import se.jsquad.batch.SlowMockBatchImpl;
import se.jsquad.batch.status.BatchStatus;
import se.jsquad.batch.status.Status;
import se.jsquad.component.database.FlywayDatabaseMigration;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:test/application.properties",
        "classpath:activemq.properties",
        "classpath:test/configuration/configuration_test.properties",
        "classpath:test/configuration/openbank_jpa.properties",
        "classpath:test/configuration/security_jpa.properties"},
        properties = {"jasypt.encryptor.password = testencryption"})
public class OpenBankRestControllerTest {
    @MockBean
    private BrokerService brokerService;

    @MockBean
    private FlywayDatabaseMigration flywayDatabaseMigration;

    @Autowired
    private OpenBankRestController openBankRestController;

    @Autowired
    private SlowMockBatch slowMockBatch;

    @Test
    public void testGetSlowMockBatchStatus() throws NoSuchFieldException, IllegalAccessException {
        // Given
        int seconds = 0;

        Field field = SlowMockBatchImpl.class.getDeclaredField("sleepTime");
        field.setAccessible(true);

        field.set(slowMockBatch, seconds);

        // When
        ResponseEntity responseEntity = openBankRestController.getOpenBankBatchStatus();
        BatchStatus batchStatus = (BatchStatus) responseEntity.getBody();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Status.SUCCESS, batchStatus.getStatus());
        assertEquals("Batch job went just fine.", batchStatus.getMessage());
    }

}
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

package se.jsquad.actuator;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import se.jsquad.health.check.DeepSystemStatusResponse;
import se.jsquad.health.check.Dependencies;
import se.jsquad.health.check.HealthStatus;
import se.jsquad.health.check.ShallowSystemStatusResponse;

@Component
@Endpoint(id = "deep-system-status")
public class DeepSystemStatusIndicator {
    private Logger logger;

    private ShallowSystemStatusIndicator shallowSystemStatusIndicator;
    private HealthIndicator openbankDatabaseHealthIndicator;
    private HealthIndicator securityDatabaseHealthIndicator;


    public DeepSystemStatusIndicator(Logger logger,
                                     ShallowSystemStatusIndicator shallowSystemStatusIndicator,
                                     @Qualifier("openbankDatabaseHealthIndicator")
                                             HealthIndicator openbankDatabaseHealthIndicator,
                                     @Qualifier("securityDatabaseHealthIndicator")
                                             HealthIndicator securityDatabaseHealthIndicator) {
        this.logger = logger;
        this.shallowSystemStatusIndicator = shallowSystemStatusIndicator;
        this.openbankDatabaseHealthIndicator = openbankDatabaseHealthIndicator;
        this.securityDatabaseHealthIndicator = securityDatabaseHealthIndicator;
    }

    @ReadOperation
    public ResponseEntity<DeepSystemStatusResponse> getDeepSystemStatus() {
        return ResponseEntity.ok(checkDeepSystemStatus());
    }

    private DeepSystemStatusResponse checkDeepSystemStatus() {
        DeepSystemStatusResponse deepSystemStatusResponse = new DeepSystemStatusResponse();

        deepSystemStatusResponse.setStatus(HealthStatus.DOWN);

        ResponseEntity<ShallowSystemStatusResponse> responseEntity = shallowSystemStatusIndicator
                .getShallowSystemStatus();

        if (responseEntity.getStatusCode().is2xxSuccessful()
                && HealthStatus.UP.equals(responseEntity.getBody().getStatus())) {
            deepSystemStatusResponse.setService(responseEntity.getBody().getStatus());
        } else {
            deepSystemStatusResponse.setService(HealthStatus.DOWN);
        }

        Dependencies dependencies = new Dependencies();
        deepSystemStatusResponse.setDependencies(dependencies);

        dependencies.setSecurityDb(HealthStatus.DOWN);
        dependencies.setOpenbankDb(HealthStatus.DOWN);

        dependencies.setOpenbankDb(HealthStatus.fromValue(openbankDatabaseHealthIndicator.health()
                .getStatus().getCode()));

        dependencies.setSecurityDb(HealthStatus.fromValue(securityDatabaseHealthIndicator.health()
                .getStatus().getCode()));

        if (HealthStatus.UP.equals(dependencies.getOpenbankDb()) && HealthStatus.UP.equals(dependencies.getSecurityDb())
                && HealthStatus.UP.equals(deepSystemStatusResponse.getService())) {
            deepSystemStatusResponse.setStatus(HealthStatus.UP);
        }

        return deepSystemStatusResponse;
    }
}
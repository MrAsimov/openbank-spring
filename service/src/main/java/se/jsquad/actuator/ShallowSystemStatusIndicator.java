/*
 * Copyright 2021 JSquad AB
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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import se.jsquad.api.health.HealthStatus;
import se.jsquad.api.health.ShallowSystemStatusResponse;

@Component
@Endpoint(id = "shallowhealth")
public class ShallowSystemStatusIndicator {
    private HealthIndicator diskSpaceHealthIndicator;

    public ShallowSystemStatusIndicator(@Qualifier("diskSpaceHealthIndicator")
                                       HealthIndicator diskSpaceHealthIndicator) {
        this.diskSpaceHealthIndicator = diskSpaceHealthIndicator;
    }

    @ReadOperation
    public ResponseEntity<ShallowSystemStatusResponse> getShallowSystemStatus() {
        return ResponseEntity.ok(checkShallowSystemStatus());
    }

    private ShallowSystemStatusResponse checkShallowSystemStatus() {
        ShallowSystemStatusResponse shallowSystemStatusResponse = new ShallowSystemStatusResponse();

        shallowSystemStatusResponse.setStatus(HealthStatus.fromValue(diskSpaceHealthIndicator.health()
                .getStatus().getCode()));

        return shallowSystemStatusResponse;
    }
}
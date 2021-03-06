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

package se.jsquad.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.jsquad.entity.SystemProperty;
import se.jsquad.producer.OpenBankPersistenceUnitProducer;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class SystemPropertyRepositoryImpl extends OpenBankPersistenceUnitProducer implements SystemPropertyRepository {
    @Override
    @Transactional(transactionManager = "transactionManagerOpenBank", propagation = Propagation.REQUIRED)
    public void persistSystemProperty(SystemProperty systemProperty) {
        getEntityManager().persist(systemProperty);
    }

    @Override
    public List<SystemProperty> findAllUniqueSystemProperties() {
        TypedQuery<SystemProperty> query = getEntityManager().createNamedQuery(SystemProperty
                .FIND_ALL_UNIQUE_SYSTEM_PROPERTIES, SystemProperty.class);

        return query.getResultList();
    }

    @Override
    public void clearSecondaryLevelCache() {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }

    @Override
    public void refreshSecondaryLevelCache() {
        clearSecondaryLevelCache();
        findAllUniqueSystemProperties();
    }
}

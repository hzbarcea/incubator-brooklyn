/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.brooklyn.cm.salt;

import org.apache.brooklyn.api.entity.EntitySpec;
import org.apache.brooklyn.api.entity.drivers.DriverDependentEntity;
import org.apache.brooklyn.api.entity.drivers.EntityDriver;
import org.apache.brooklyn.api.entity.drivers.EntityDriverManager;
import org.apache.brooklyn.api.location.Location;
import org.apache.brooklyn.api.location.LocationSpec;
import org.apache.brooklyn.core.entity.Attributes;
import org.apache.brooklyn.core.entity.BrooklynConfigKeys;
import org.apache.brooklyn.core.entity.Entities;
import org.apache.brooklyn.core.entity.EntityAsserts;
import org.apache.brooklyn.core.entity.drivers.BasicEntityDriverManager;
import org.apache.brooklyn.core.entity.drivers.ReflectiveEntityDriverFactory.AbstractDriverInferenceRule;
import org.apache.brooklyn.core.entity.drivers.ReflectiveEntityDriverFactory.DriverInferenceForSshLocation;
import org.apache.brooklyn.core.location.SimulatedLocation;
import org.apache.brooklyn.core.test.BrooklynAppLiveTestSupport;
import org.apache.brooklyn.core.test.entity.TestApplication;
import org.apache.brooklyn.location.ssh.SshMachineLocation;
import org.apache.brooklyn.util.text.Strings;
import org.apache.brooklyn.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;


public class SaltInfrastructureTestSupport extends BrooklynAppLiveTestSupport {

    protected static final Logger LOG = LoggerFactory.getLogger(SaltInfrastructureTestSupport.class);

    // protected BrooklynLauncher launcher;
    protected Location testLocation;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        EntityDriverManager edm = mgmt.getEntityDriverManager();
        if (edm != null && edm instanceof BasicEntityDriverManager) {
        	BasicEntityDriverManager bedm = (BasicEntityDriverManager) edm;
        	bedm.getReflectiveDriverFactory().addRule(
        			DriverInferenceForSimulatedLocation.DEFAULT_IDENTIFIER, 
        			new DriverInferenceForSimulatedLocation());
        }
        testLocation = mgmt.getLocationManager().createLocation(LocationSpec.create(SimulatedLocation.class)
        		.configure(SaltInfrastructure.LOCATION_NAME, "simulated-loc")
        		.configure(BrooklynConfigKeys.SKIP_ON_BOX_BASE_DIR_RESOLUTION, true));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() throws Exception {
        if (mgmt != null) {
        	Entities.destroyAll(mgmt);
        }
        // if (launcher != null) launcher.terminate();
    }

    public static SaltInfrastructure deployAndWaitForInfrastructure(TestApplication app, Location location) {
        SaltInfrastructure infra = app.createAndManageChild(EntitySpec.create(SaltInfrastructure.class)
                .configure(SaltInfrastructure.LOCATION_NAME, "salt-infra-test")
                .displayName("SaltStack Managed Infrastructure"));
        LOG.info("Starting {} in {}", infra, location);
        app.start(ImmutableList.of(location));

        SaltLocation saltLocation = infra.getDynamicLocation();

        LOG.info("Waiting {} for {} to have started", Duration.THIRTY_SECONDS, infra);
        EntityAsserts.assertAttributeEqualsEventually(
        	ImmutableMap.of("timeout", Duration.THIRTY_SECONDS), infra, Attributes.SERVICE_UP, true);
        return infra;
    }

    public static class DriverInferenceForSimulatedLocation extends AbstractDriverInferenceRule {

        public static final String DEFAULT_IDENTIFIER = "simulated-location-driver-inference-rule";

        @Override
        public <D extends EntityDriver> String inferDriverClassName(DriverDependentEntity<D> entity, Class<D> driverInterface, Location location) {
            String driverInterfaceName = driverInterface.getName();
            if (!(location instanceof SimulatedLocation)) {
            	return null;
            }
            if (!driverInterfaceName.endsWith("Driver")) {
                throw new IllegalArgumentException(String.format("Driver name [%s] doesn't end with 'Driver'; cannot auto-detect SshDriver class name", driverInterfaceName));
            }
            return Strings.removeFromEnd(driverInterfaceName, "Driver") + "SimulatedDriver";
        }
    }


}

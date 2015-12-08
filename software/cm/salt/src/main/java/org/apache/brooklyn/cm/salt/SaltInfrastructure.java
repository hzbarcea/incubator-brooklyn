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
import org.apache.brooklyn.api.entity.ImplementedBy;
import org.apache.brooklyn.api.location.Location;
import org.apache.brooklyn.api.sensor.AttributeSensor;
import org.apache.brooklyn.cm.salt.impl.SaltInfrastructureImpl;
import org.apache.brooklyn.config.ConfigKey;
import org.apache.brooklyn.core.config.ConfigKeys;
import org.apache.brooklyn.core.entity.StartableApplication;
import org.apache.brooklyn.core.location.dynamic.LocationOwner;
import org.apache.brooklyn.core.sensor.AttributeSensorAndConfigKey;
import org.apache.brooklyn.util.core.flags.SetFromFlag;

import com.google.common.annotations.Beta;

@Beta
@ImplementedBy(SaltInfrastructureImpl.class)
public interface SaltInfrastructure extends 
        StartableApplication, 
        LocationOwner<SaltManagedLocation, SaltInfrastructure> {
/*
    // TODO: do we really want to redefine LOCATION_NAME (unless we want to override the default)
    @CatalogConfig(label = "Location Name", priority = 90)
    @SetFromFlag("locationName")
    ConfigKey<String> LOCATION_NAME = ConfigKeys.newConfigKeyWithDefault(
            LocationOwner.LOCATION_NAME.getConfigKey(), "salt-infra");
*/

    AttributeSensor<Location> INFRA_LOCATION = SaltAttributes.INFRA_LOCATION;

    @SuppressWarnings("rawtypes")
    @SetFromFlag("controllerSpec")
    AttributeSensorAndConfigKey<EntitySpec, EntitySpec> SALT_CONTROLLER_SPEC = ConfigKeys.newSensorAndConfigKey(
            EntitySpec.class, "salt.controller.spec", "Specification for the SaltStack controller",
            EntitySpec.create(SaltEntity.class));

    @SetFromFlag("controllerStart")
    ConfigKey<Boolean> SALT_CONTROLLER_START = ConfigKeys.newBooleanConfigKey(
            "salt.controller.start", "Setup the SaltStack controller (master/ssh)", Boolean.TRUE);

}

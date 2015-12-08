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
package org.apache.brooklyn.cm.salt.impl;

import java.util.Collection;
import java.util.Map;

import org.apache.brooklyn.api.entity.EntitySpec;
import org.apache.brooklyn.api.location.Location;
import org.apache.brooklyn.api.location.LocationDefinition;
import org.apache.brooklyn.api.location.LocationRegistry;
import org.apache.brooklyn.api.location.LocationSpec;
import org.apache.brooklyn.api.location.MachineLocation;
import org.apache.brooklyn.api.location.MachineProvisioningLocation;
import org.apache.brooklyn.api.location.NoMachinesAvailableException;
import org.apache.brooklyn.api.mgmt.ManagementContext;
import org.apache.brooklyn.cm.salt.SaltAttributes;
import org.apache.brooklyn.cm.salt.SaltEntity;
import org.apache.brooklyn.cm.salt.SaltInfrastructure;
import org.apache.brooklyn.cm.salt.SaltLocationResolver;
import org.apache.brooklyn.cm.salt.SaltManagedLocation;
import org.apache.brooklyn.core.entity.AbstractApplication;
import org.apache.brooklyn.core.entity.Attributes;
import org.apache.brooklyn.core.entity.Entities;
import org.apache.brooklyn.core.location.BasicLocationDefinition;
import org.apache.brooklyn.core.location.BasicLocationRegistry;
import org.apache.brooklyn.core.location.dynamic.DynamicLocation;
import org.apache.brooklyn.core.location.dynamic.LocationOwner;
import org.apache.brooklyn.location.ssh.SshMachineLocation;
import org.apache.brooklyn.util.collections.MutableMap;
import org.apache.brooklyn.util.text.Strings;
import org.python.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;


public class SaltInfrastructureImpl extends AbstractApplication implements SaltInfrastructure {

    private static final Logger LOG = LoggerFactory.getLogger(SaltInfrastructureImpl.class);

    @Override
    public void init() {
        registerLocationResolver();    // TODO: do we really need one?
        super.init();
        // sensors().set(null, 0);  // TODO: whatever we need to know from here
    }

    @Override
    public SaltManagedLocation getDynamicLocation() {
        return (SaltManagedLocation) sensors().get(LocationOwner.DYNAMIC_LOCATION);
    }

    @Override
    public SaltManagedLocation createLocation(Map<String, ?> flags) {
        String locationName = config().get(LOCATION_NAME);
        if (Strings.isBlank(locationName)) {
            String prefix = config().get(LOCATION_NAME_PREFIX);
            String suffix = config().get(LOCATION_NAME_SUFFIX);
            locationName = Joiner.on("-").skipNulls().join(prefix, getId(), suffix);
        }
        LOG.info("Creating SaltManagedLocation {}", locationName);
        LocationDefinition alreadyExists = getManagementContext().getLocationRegistry().getDefinedLocationByName(locationName);
        Preconditions.checkState(alreadyExists == null, "SaltManagedLocation already exists.");

        Location parent = sensors().get(INFRA_LOCATION);
        Preconditions.checkState(parent != null && parent instanceof MachineProvisioningLocation, 
                "Parent MachineProvisioningLocation not provided.");

        LocationSpec<SaltManagedLocation> spec = LocationSpec.create(SaltManagedLocation.class)
                .parent(parent)
                .configure(DynamicLocation.OWNER, this)
                .configure(LOCATION_NAME, locationName);
                // .configure(...)
        SaltManagedLocation ml =  getManagementContext().getLocationManager().createLocation(spec);
        
        ManagementContext.PropertiesReloadListener listener = SaltUtils.propertiesReloadListener(getManagementContext(), null);
        getManagementContext().addPropertiesReloadListener(listener);
        sensors().set(Attributes.PROPERTIES_RELOAD_LISTENER, listener);

        sensors().set(LocationOwner.LOCATION_NAME, ml.getId());
        sensors().set(LocationOwner.LOCATION_SPEC, spec.toString());
        // sensors().set(LocationOwner.LOCATION_DEFINITION, definition);
        sensors().set(LocationOwner.DYNAMIC_LOCATION, ml);

        LOG.info("Created SaltManagedLocation {}", ml);
        return ml;
    }

    @Override
    public boolean isLocationAvailable() {
        return getDynamicLocation() != null;
    }

    @Override
    public void deleteLocation() {
        // TODO: would SaltStack even work without its dynamicLocation?
    }

    private void registerLocationResolver() {
        // TODO: manage resolvers for multiple infrastructure instances?
        LocationRegistry registry = getManagementContext().getLocationRegistry();
        if (registry instanceof BasicLocationRegistry) {
            SaltLocationResolver resolver = new SaltLocationResolver();
            ((BasicLocationRegistry)registry).registerResolver(resolver);
            LOG.info("Explicitly registered SaltStack resolver: {}", resolver.toString());
        }
    }

    @Override
    public void doStart(Collection<? extends Location> locations) {
        // TODO: can SaltStack really support multiple locations?
        Preconditions.checkArgument(locations.size() == 1);
        Location parent = locations.iterator().next();
        sensors().set(INFRA_LOCATION, parent);

        Map<String, ?> flags = MutableMap.<String, Object> builder()
            .putAll(config().get(LOCATION_FLAGS))
            .build();
        createLocation(flags);

        super.doStart(locations);
        LOG.info("Started SaltStack infrastructure: {}", this.getId());
    }

    @Override
    public void postStart(Collection<? extends Location> locations) {
        if (config().get(SALT_CONTROLLER_START)) {
            // TODO: need to get a MachineLocation first to install the controller
            LOG.info("Starting SaltStack controller now on {}", this.getId());
            SaltManagedLocation ml = (SaltManagedLocation)sensors().get(LocationOwner.DYNAMIC_LOCATION);

            EntitySpec<SaltEntity> spec = EntitySpec.create(SaltEntity.class)
                    .configure(SaltEntity.SALT_INFRASTRUCTURE, this)
                    .configure(SaltEntity.SUGGESTED_VERSION, "stable");
            SaltEntity controller = addChild(spec);
            Entities.start(controller, ImmutableList.of(ml));
            sensors().set(SERVICE_UP, true);

            LOG.debug("Starting a new SaltController with spec {}", spec);

        }
    }
}

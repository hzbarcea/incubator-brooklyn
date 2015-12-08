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

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.brooklyn.api.location.Location;
import org.apache.brooklyn.api.location.MachineLocation;
import org.apache.brooklyn.api.location.MachineProvisioningLocation;
import org.apache.brooklyn.api.location.NoMachinesAvailableException;
import org.apache.brooklyn.core.location.AbstractLocation;
import org.apache.brooklyn.core.location.dynamic.DynamicLocation;
import org.apache.brooklyn.util.core.flags.SetFromFlag;
import org.python.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.Beta;

@Beta
public class SaltManagedLocation extends AbstractLocation implements 
        SaltLocation, 
        MachineProvisioningLocation<MachineLocation>, 
        DynamicLocation<SaltInfrastructure, SaltManagedLocation>, 
        Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(SaltManagedLocation.class);

    @SetFromFlag("owner")
    private SaltInfrastructure infrastructure;

    @Override
    public SaltInfrastructure getOwner() {
        return infrastructure;
    }

    @Override
    public MachineLocation obtain(Map<?, ?> flags) throws NoMachinesAvailableException {
        SaltInfrastructure infra = getOwner();
        Preconditions.checkNotNull(infra);
        Location infraLocation = infra.sensors().get(SaltAttributes.INFRA_LOCATION);
        Preconditions.checkState(infraLocation != null && infraLocation instanceof MachineProvisioningLocation);

        MachineProvisioningLocation<?> mpl = (MachineProvisioningLocation<?>) infraLocation;
        return mpl.obtain(flags);
    }

    @Override
    public MachineProvisioningLocation<MachineLocation> newSubLocation(Map<?, ?> newFlags) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void release(MachineLocation machine) {
        // TODO Auto-generated method stub
    }

    @Override
    public Map<String, Object> getProvisioningFlags(Collection<String> tags) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        
    }

}

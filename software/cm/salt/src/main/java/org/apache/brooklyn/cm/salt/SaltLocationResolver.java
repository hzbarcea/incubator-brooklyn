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

import java.util.Map;

import org.apache.brooklyn.api.location.Location;
import org.apache.brooklyn.api.location.LocationRegistry;
import org.apache.brooklyn.api.location.LocationResolver.EnableableLocationResolver;
import org.apache.brooklyn.api.mgmt.ManagementContext;
import org.python.google.common.base.Preconditions;

public class SaltLocationResolver implements EnableableLocationResolver {

    public static final String SALT = "salt";
    public static final String SALT_INFRASTRUCTURE_SPEC = SALT + ":%s";

    private ManagementContext managementContext;

    @Override
    public void init(ManagementContext managementContext) {
        this.managementContext = Preconditions.checkNotNull(managementContext, "managementContext");
    }

    @Override
    public String getPrefix() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean accepts(String spec, LocationRegistry registry) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Location newLocationFromString(Map locationFlags, String spec,
            LocationRegistry registry) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return false;
    }


}

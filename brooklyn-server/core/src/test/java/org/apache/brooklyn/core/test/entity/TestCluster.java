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
package org.apache.brooklyn.core.test.entity;

import java.util.List;

import org.apache.brooklyn.api.entity.EntityLocal;
import org.apache.brooklyn.api.entity.ImplementedBy;
import org.apache.brooklyn.config.ConfigKey;
import org.apache.brooklyn.core.config.ConfigKeys;
import org.apache.brooklyn.entity.group.DynamicCluster;

/**
* Mock cluster entity for testing.
*/
@ImplementedBy(TestClusterImpl.class)
public interface TestCluster extends DynamicCluster, EntityLocal {
    
    ConfigKey<Integer> MAX_SIZE = ConfigKeys.newIntegerConfigKey("testCluster.maxSize", "Size after which it will throw InsufficientCapacityException", Integer.MAX_VALUE);
    
    List<Integer> getSizeHistory();
    
    List<Integer> getDesiredSizeHistory();
}

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

import org.apache.brooklyn.api.mgmt.TaskAdaptable;
import org.apache.brooklyn.api.mgmt.TaskFactory;
import org.apache.brooklyn.config.ConfigKey;
import org.apache.brooklyn.core.config.ConfigKeys;
import org.apache.brooklyn.core.config.MapConfigKey;
import org.apache.brooklyn.core.config.SetConfigKey;

import com.google.common.annotations.Beta;
import com.google.common.reflect.TypeToken;

/**
 * {@link ConfigKey}s used to configure Salt entities.
 *
 * @see SaltConfigs
 */
@Beta
public interface SaltConfig {

    MapConfigKey<String> SALT_FORMULAS = new MapConfigKey<String>(String.class, "brooklyn.salt.formulaUrls",
            "Map of Salt formula URLs");
    SetConfigKey<String> SALT_SSH_RUN_LIST = new SetConfigKey<String>(String.class, "brooklyn.salt.ssh.runList", 
            "Set of SaltSsh commands to run");
    MapConfigKey<Object> SALT_SSH_LAUNCH_ATTRIBUTES = new MapConfigKey<Object>(Object.class, "brooklyn.salt.ssh.launch.attributes", "TODO");

    @SuppressWarnings("serial")
    ConfigKey<TaskFactory<? extends TaskAdaptable<Boolean>>> IS_RUNNING_TASK = ConfigKeys.newConfigKey(
            new TypeToken<TaskFactory<? extends TaskAdaptable<Boolean>>>() {}, 
            "salt.driver.isRunningTask");

    /**
     * The {@link SaltEntity} entity.
     */
    SaltEntity getSaltSshEntity();

}

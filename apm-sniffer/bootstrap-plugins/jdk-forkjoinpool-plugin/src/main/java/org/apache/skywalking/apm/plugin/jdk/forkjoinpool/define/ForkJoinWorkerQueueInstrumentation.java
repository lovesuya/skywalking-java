/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.apm.plugin.jdk.forkjoinpool.define;

import static net.bytebuddy.matcher.ElementMatchers.named;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.v2.ClassInstanceMethodsEnhancePluginDefineV2;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.v2.InstanceMethodsInterceptV2Point;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

public class ForkJoinWorkerQueueInstrumentation extends ClassInstanceMethodsEnhancePluginDefineV2 {

    private static final String FORK_JOIN_WORKER_QUEUE_CLASS = "java.util.concurrent.ForkJoinPool$WorkQueue";

    private static final String FORK_JOIN_WORKER_QUEUE_RUN_TASK_METHOD = "runTask";
    private static final String FORK_JOIN_WORKER_QUEUE_RUN_TASK_METHOD_INTERCEPTOR = "org.apache.skywalking.apm.plugin.jdk.forkjoinpool.ForkJoinWorkerQueueMethodInterceptor";

    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(FORK_JOIN_WORKER_QUEUE_CLASS);
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override
    public InstanceMethodsInterceptV2Point[] getInstanceMethodsInterceptV2Points() {
        return new InstanceMethodsInterceptV2Point[]{
                new InstanceMethodsInterceptV2Point() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named(FORK_JOIN_WORKER_QUEUE_RUN_TASK_METHOD);
                    }

                    @Override
                    public String getMethodsInterceptorV2() {
                        return FORK_JOIN_WORKER_QUEUE_RUN_TASK_METHOD_INTERCEPTOR;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                }
        };
    }

    @Override
    public boolean isBootstrapInstrumentation() {
        return true;
    }
}

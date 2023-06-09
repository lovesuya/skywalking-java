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

package org.apache.skywalking.apm.plugin.jdk.forkjoinpool;

import java.lang.reflect.Method;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.ContextSnapshot;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.v2.InstanceMethodsAroundInterceptorV2;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.v2.MethodInvocationContext;
import org.apache.skywalking.apm.network.trace.component.ComponentsDefine;

public class ForkJoinWorkerQueueMethodInterceptor implements InstanceMethodsAroundInterceptorV2 {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
            MethodInvocationContext context) throws Throwable {
        AbstractSpan span = ContextManager.createLocalSpan(generateOperationName(objInst, method));
        span.setComponent(ComponentsDefine.JDK_THREADING);
        context.setContext(span);
        EnhancedInstance forkJoinTask = (EnhancedInstance) allArguments[0];
        if (forkJoinTask != null) {
            final Object storedField = forkJoinTask.getSkyWalkingDynamicField();
            if (storedField != null) {
                final ContextSnapshot contextSnapshot = (ContextSnapshot) storedField;
                ContextManager.continued(contextSnapshot);
            }
        }
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
            Object ret, MethodInvocationContext context) throws Throwable {
        AbstractSpan span = (AbstractSpan) context.getContext();
        if (span != null) {
            ContextManager.stopSpan(span);
        }
        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
            Class<?>[] argumentsTypes, Throwable t, MethodInvocationContext context) {
        AbstractSpan span = (AbstractSpan) context.getContext();
        if (span != null) {
            span.log(t);
        }
    }

    private String generateOperationName(final EnhancedInstance objInst, final Method method) {
        return "ForkJoinPool/" + objInst.getClass().getName() + "/" + method.getName();
    }
}

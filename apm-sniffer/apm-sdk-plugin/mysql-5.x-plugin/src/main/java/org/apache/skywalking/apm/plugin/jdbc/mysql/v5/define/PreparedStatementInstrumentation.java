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

package org.apache.skywalking.apm.plugin.jdbc.mysql.v5.define;

import static net.bytebuddy.matcher.ElementMatchers.named;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.MultiClassNameMatch;
import org.apache.skywalking.apm.plugin.jdbc.mysql.Constants;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * {@link PreparedStatementInstrumentation} define that the mysql-5.x plugin intercepts the following methods in the
 * com.mysql.jdbc.PreparedStatement class:
 * 1. execute 2. executeQuery 3. executeUpdate 4. executeLargeUpdate
 */
public class PreparedStatementInstrumentation extends AbstractMysqlInstrumentation {

    private static final String SERVICE_METHOD_INTERCEPTOR = Constants.PREPARED_STATEMENT_EXECUTE_METHODS_INTERCEPTOR;
    public static final String MYSQL_PREPARED_STATEMENT_CLASS_NAME = "com.mysql.jdbc.PreparedStatement";
    public static final String MYSQL_SERVER_PREPARED_STATEMENT_CLASS_NAME = "com.mysql.jdbc.ServerPreparedStatement";

    @Override
    public final ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named("execute").or(named("executeQuery"))
                                           .or(named("executeUpdate"))
                                           .or(named("executeLargeUpdate"));
                }

                @Override
                public String getMethodsInterceptor() {
                    return SERVICE_METHOD_INTERCEPTOR;
                }

                @Override
                public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }

    @Override
    protected ClassMatch enhanceClass() {
        return MultiClassNameMatch.byMultiClassMatch(MYSQL_PREPARED_STATEMENT_CLASS_NAME, MYSQL_SERVER_PREPARED_STATEMENT_CLASS_NAME);
    }

}

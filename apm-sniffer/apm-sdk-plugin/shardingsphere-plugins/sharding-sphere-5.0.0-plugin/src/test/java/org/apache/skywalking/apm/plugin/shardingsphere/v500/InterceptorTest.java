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

package org.apache.skywalking.apm.plugin.shardingsphere.v500;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.shardingsphere.infra.binder.LogicSQL;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractTracingSpan;
import org.apache.skywalking.apm.agent.core.context.trace.TraceSegment;
import org.apache.skywalking.apm.agent.test.helper.SegmentHelper;
import org.apache.skywalking.apm.agent.test.tools.AgentServiceRule;
import org.apache.skywalking.apm.agent.test.tools.SegmentStorage;
import org.apache.skywalking.apm.agent.test.tools.SegmentStoragePoint;
import org.apache.skywalking.apm.agent.test.tools.SpanAssert;
import org.apache.skywalking.apm.agent.test.tools.TracingSegmentRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TracingSegmentRunner.class)
public class InterceptorTest {

    @SegmentStoragePoint
    private SegmentStorage segmentStorage;

    @Rule
    public AgentServiceRule serviceRule = new AgentServiceRule();

    private ProxyRootInvokeInterceptor proxyRootInvokeInterceptor;

    private JDBCRootInvokeInterceptor jdbcRootInvokeInterceptor;

    private ParseInterceptor parseInterceptor;

    private RouteInterceptor routeInterceptor;

    private RewriteInterceptor rewriteInterceptor;

    private ExecuteInterceptor executeInterceptor;

    @Before
    public void setUp() {
        proxyRootInvokeInterceptor = new ProxyRootInvokeInterceptor();
        jdbcRootInvokeInterceptor = new JDBCRootInvokeInterceptor();
        parseInterceptor = new ParseInterceptor();
        routeInterceptor = new RouteInterceptor();
        rewriteInterceptor = new RewriteInterceptor();
        executeInterceptor = new ExecuteInterceptor();
    }

    @Test
    public void assertProxyRootInvoke() {
        proxyRootInvokeInterceptor.beforeMethod(null, null, null, null, null);
        proxyRootInvokeInterceptor.afterMethod(null, null, null, null, null);
        assertThat(segmentStorage.getTraceSegments().size(), is(1));
        TraceSegment segment = segmentStorage.getTraceSegments().get(0);
        List<AbstractTracingSpan> spans = SegmentHelper.getSpans(segment);
        assertNotNull(spans);
        assertThat(spans.size(), is(1));
        assertThat(spans.get(0).getOperationName(), is("/ShardingSphere/ProxyRootInvoke/"));
    }

    @Test
    public void assertJDBCRootInvoke() {
        jdbcRootInvokeInterceptor.beforeMethod(null, null, null, null, null);
        jdbcRootInvokeInterceptor.afterMethod(null, null, null, null, null);
        assertThat(segmentStorage.getTraceSegments().size(), is(1));
        TraceSegment segment = segmentStorage.getTraceSegments().get(0);
        List<AbstractTracingSpan> spans = SegmentHelper.getSpans(segment);
        assertNotNull(spans);
        assertThat(spans.size(), is(1));
        assertThat(spans.get(0).getOperationName(), is("/ShardingSphere/JDBCRootInvoke/"));
    }

    @Test
    public void assertParse() {
        Object[] allArguments = new Object[] {
            "SELECT * FROM t_order",
            false
        };
        parseInterceptor.beforeMethod(null, null, allArguments, null, null);
        parseInterceptor.afterMethod(null, null, allArguments, null, null);
        assertThat(segmentStorage.getTraceSegments().size(), is(1));
        TraceSegment segment = segmentStorage.getTraceSegments().get(0);
        List<AbstractTracingSpan> spans = SegmentHelper.getSpans(segment);
        assertNotNull(spans);
        assertThat(spans.size(), is(1));
        assertThat(spans.get(0).getOperationName(), is("/ShardingSphere/parseSQL/"));
        SpanAssert.assertTag(spans.get(0), 0, "SELECT * FROM t_order");
    }

    @Test
    public void assertRoute() {
        Object[] allArguments = new Object[]{
                new LogicSQL(null, "SELECT * FROM t_order", Collections.emptyList()),
                null
        };
        routeInterceptor.beforeMethod(null, null, allArguments, null, null);
        routeInterceptor.afterMethod(null, null, allArguments, null, null);
        assertThat(segmentStorage.getTraceSegments().size(), is(1));
        TraceSegment segment = segmentStorage.getTraceSegments().get(0);
        List<AbstractTracingSpan> spans = SegmentHelper.getSpans(segment);
        assertNotNull(spans);
        assertThat(spans.size(), is(1));
        assertThat(spans.get(0).getOperationName(), is("/ShardingSphere/routeSQL/"));
    }

    @Test
    public void assertRewrite() {
        Object[] allArguments = new Object[]{
                "SELECT * FROM t_order",
                Collections.emptyList(),
                null,
                null
        };
        rewriteInterceptor.beforeMethod(null, null, allArguments, null, null);
        rewriteInterceptor.afterMethod(null, null, allArguments, null, null);
        assertThat(segmentStorage.getTraceSegments().size(), is(1));
        TraceSegment segment = segmentStorage.getTraceSegments().get(0);
        List<AbstractTracingSpan> spans = SegmentHelper.getSpans(segment);
        assertNotNull(spans);
        assertThat(spans.size(), is(1));
        assertThat(spans.get(0).getOperationName(), is("/ShardingSphere/rewriteSQL/"));
    }

    @Test
    public void assertExecute() {
        Object[] allArguments = new Object[]{
                null,
                null,
                new HashMap<>()
        };
        executeInterceptor.beforeMethod(null, null, allArguments, null, null);
        executeInterceptor.afterMethod(null, null, allArguments, null, null);
        assertThat(segmentStorage.getTraceSegments().size(), is(1));
        TraceSegment segment = segmentStorage.getTraceSegments().get(0);
        List<AbstractTracingSpan> spans = SegmentHelper.getSpans(segment);
        assertNotNull(spans);
        assertThat(spans.size(), is(1));
        assertThat(spans.get(0).getOperationName(), is("/ShardingSphere/executeSQL/"));
    }
}

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.tests;

import com.facebook.presto.Session;
import com.facebook.presto.tests.tpch.IndexedTpchPlugin;
import com.google.common.collect.ImmutableMap;

import java.nio.file.Paths;

import static com.facebook.presto.testing.TestingSession.testSessionBuilder;
import static com.facebook.presto.tpch.TpchMetadata.TINY_SCHEMA_NAME;

public class TestDistributedQueriesIndexed
        extends AbstractTestIndexedQueries
{
    public TestDistributedQueriesIndexed()
    {
        super(TestDistributedQueriesIndexed::createQueryRunner);
    }

    private static DistributedQueryRunner createQueryRunner()
            throws Exception
    {
        Session session = testSessionBuilder()
                .setCatalog("tpch_indexed")
                .setSchema(TINY_SCHEMA_NAME)
                .build();

        // set spill path so we can enable spill by session property
        ImmutableMap<String, String> extraProperties = ImmutableMap.of(
                "experimental.spiller-spill-path",
                Paths.get(System.getProperty("java.io.tmpdir"), "presto", "spills").toString());
        DistributedQueryRunner queryRunner = new DistributedQueryRunner.Builder(session)
                .setNodeCount(3)
                .setExtraProperties(extraProperties)
                .build();

        queryRunner.installPlugin(new IndexedTpchPlugin(INDEX_SPEC));
        queryRunner.createCatalog("tpch_indexed", "tpch_indexed");
        return queryRunner;
    }
}

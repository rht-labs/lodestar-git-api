package com.redhat.labs.omp.mocks;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.infinispan.server.hotrod.HotRodServer;
import org.infinispan.server.hotrod.test.HotRodTestingUtil;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.infinispan.test.fwk.TestResourceTracker;

public class MockHotRodServer {
	
	private MockHotRodServer() {}
	
	/**
	 * Not a singleton
	 */
	public static HotRodServer getHotRod() {
		TestResourceTracker.setThreadTestName("InfinispanServer");

        EmbeddedCacheManager ecm = TestCacheManagerFactory.createCacheManager(
                new GlobalConfigurationBuilder().nonClusteredDefault().defaultCacheName("default"),
                new ConfigurationBuilder());
        ecm.createCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME, new ConfigurationBuilder().indexing().build());

        ecm.getCache().put("a", "domedata");
        
        ecm.createCache("omp", new ConfigurationBuilder().indexing().build());

        HotRodServer hs =  HotRodTestingUtil.startHotRodServer(ecm, 11222);
        hs.setMarshaller(new org.infinispan.commons.marshall.JavaSerializationMarshaller());

        return hs;
	}
}

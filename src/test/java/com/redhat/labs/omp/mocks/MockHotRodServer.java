package com.redhat.labs.omp.mocks;

public class MockHotRodServer {
	
//	private MockHotRodServer() {}
//	
//	/**
//	 * Not a singleton
//	 */
//	public static HotRodServer getHotRod() {
//		TestResourceTracker.setThreadTestName("InfinispanServer");
//
//        EmbeddedCacheManager ecm = TestCacheManagerFactory.createCacheManager(
//                new GlobalConfigurationBuilder().nonClusteredDefault().defaultCacheName("default"),
//                new ConfigurationBuilder());
//        ecm.createCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME, new ConfigurationBuilder().indexing().build());
//
//        ecm.getCache().put("a", "domedata");
//        
//        ecm.createCache("omp", new ConfigurationBuilder().indexing().build());
//
//        HotRodServer hs =  HotRodTestingUtil.startHotRodServer(ecm, 11222);
//        hs.setMarshaller(new org.infinispan.commons.marshall.JavaSerializationMarshaller());
//
//        return hs;
//	}
}

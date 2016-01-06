//package com.estest.esBasic;
//
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.settings.*;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
//import org.elasticsearch.node.Node;
//import org.elasticsearch.node.NodeBuilder;
//
//import java.lang.reflect.Constructor;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by wklmogujie on 16/1/4.
// */
//public class getEmbeddedClient {
//    static Map<String, String> m = new HashMap<String, String>();
//    // 设置client.transport.sniff为true来使客户端去嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中，
//    static Settings settings =  new Settings.settingsBuilder().put(m).put("cluster.name",clusterName).put("client.transport.sniff", true).build();
//
//
//    Map<String, String> settingMap = new HashMap<String, String>();
//    settingMap.put("node.client", "false");
//    settingMap.put("node.data", "true");
//    settingMap.put("node.local", "true");
//    settingMap.put("cluster.name", "clasterName");
//    settingMap.put("node.name", "geloin");
//
//    Node node = NodeBuilder.nodeBuilder().settings(settings).node();
//    // 创建私有对象
//    private static TransportClient client;
//
//    static {
//        try {
//            Class<?> clazz = Class.forName(TransportClient.class.getName());
//            Constructor<?> constructor = clazz.getDeclaredConstructor(Settings.class);
//            constructor.setAccessible(true);
//            client = (TransportClient) constructor.newInstance(settings);
//            client.addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 取得实例
//    public static synchronized TransportClient getTransportClient() {
//        return client;
//    }
//}

package org.example;

import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;


public class Main {
    public static void main(String[] args) {
        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("127.0.0.1", 6379));
       // nodes.add(new HostAndPort("127.0.0.1", 7380));

        JedisCluster client = new JedisCluster(nodes);

    }
}
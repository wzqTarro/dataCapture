package com.data.utils;

import java.time.Duration;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.support.ConnectionPoolSupport;

/**
 * 集群redis工具类
 * @author Alex
 *
 */
public class ClusterRedisUtil {

	public static Logger logger = LoggerFactory.getLogger(ClusterRedisUtil.class);
	
	public static GenericObjectPool<StatefulRedisClusterConnection<String, String>> pool;
	
	public static StatefulRedisClusterConnection<String, String> conn; 
	
	public static RedisClusterClient client;
	
	private String hostName = "120.77.153.124";
	// @Value("${spring.redis.password}")
	private String password = "OIUBmgvR983VVTHvr=";

	// @Value("${spring.redis.lettuce.pool.max-idle}")
	private int maxIdle = 8;

	// @Value("${spring.redis.lettuce.pool.min-idle}")
	private int minIdle = 0;

	// @Value("${spring.redis.lettuce.pool.max-active}")
	private int maxActive = 8;

	// @Value("${spring.redis.lettuce.pool.max-wait}")
	private int maxWait = -1;
	
	@PostConstruct
	public void init() {
		logger.info("--->>>集群redis初始化开始<<<---");
		if(client == null) {
			synchronized (RedisClusterClient.class) {
				RedisURI redisUri = RedisURI.builder()
						.withHost(hostName).withPort(16379)
						.withHost(hostName).withPort(16380)
						.withHost(hostName).withPort(16381)
						.withHost(hostName).withPort(16382)
						.withHost(hostName).withPort(16383)
						.withHost(hostName).withPort(16384)
						.withPassword(password)
						.withTimeout(Duration.ofSeconds(60L))
						.build();
				client = RedisClusterClient.create(redisUri);
				GenericObjectPoolConfig config = new GenericObjectPoolConfig();
				config.setMaxIdle(maxIdle);
				config.setMinIdle(minIdle);
				config.setMaxTotal(maxActive);
				config.setMaxWaitMillis(maxWait);
				pool = ConnectionPoolSupport.createGenericObjectPool(() -> client.connect(), config);
				try {
					conn = pool.borrowObject();
					logger.info("--->>>集群redis初始化完成<<<---");
				} catch (Exception e) {
					logger.info("--->>>集群redis初始化异常: {}<<<---", e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 关闭服务器时 关闭连接
	 */
	@PreDestroy
	public static void shutDown() {
		pool.close();
		client.shutdown();
	}
	
	public static final RedisAdvancedClusterAsyncCommands<String, String> getCommands() {
		return conn.async();
	}
	
	/**
	 * 保存值
	 * @param key
	 * @param value
	 */
	public static void set(String key, String value) {
		RedisAdvancedClusterAsyncCommands<String, String> commands = getCommands();
		commands.set(key, value);
	}
	
	/**
	 * 保存值并设置时间
	 * @param key
	 * @param seconds 秒
	 * @param value
	 */
	public static void setex(String key, long seconds, String value) {
		RedisAdvancedClusterAsyncCommands<String, String> commands = getCommands();
		commands.setex(key, seconds, value);
	}
	
	/**
	 * 根据key值取value
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		try {
			RedisAdvancedClusterAsyncCommands<String, String> commands = getCommands();
			RedisFuture<String> future = commands.get(key);
			return future.get();
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 取得值的存活时间
	 * @param key
	 * @return
	 */
	public static Long ttl(String key) {
		try {
			RedisAdvancedClusterAsyncCommands<String, String> commands = getCommands();
			RedisFuture<Long> future = commands.ttl(key);
			return future.get();
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 给对应的key值设置存活时间
	 * @param key
	 * @param seconds 秒
	 * @return
	 */
	public static Boolean expire(String key, long seconds) {
		try {
			RedisAdvancedClusterAsyncCommands<String, String> commands = getCommands();
			RedisFuture<Boolean> future = commands.expire(key, seconds);
			return future.get();
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 判断key值是否存在
	 * @param key
	 * @return
	 */
	public static Boolean exists(String... key) {
		try {
			RedisAdvancedClusterAsyncCommands<String, String> commands = getCommands();
			return commands.exists(key).get() > 0 ? true : false;
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 自增
	 * @param key
	 * @return
	 */
	public static Long incr(String key) {
		try {
			RedisAdvancedClusterAsyncCommands<String, String> commands = getCommands();
			RedisFuture<Long> future = commands.incr(key);
			return future.get();
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 自减
	 * @param key
	 * @return
	 */
	public static Long decr(String key) {
		try {
			RedisAdvancedClusterAsyncCommands<String, String> commands = getCommands();
			RedisFuture<Long> future = commands.decr(key);
			return future.get();
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 根据key删除
	 * @param key
	 */
	public static void del(String... key) {
		RedisAdvancedClusterAsyncCommands<String, String> commands = getCommands();
		commands.del(key);
	}
	
	/**
	 * 根据key追加value
	 * @param key
	 * @param value
	 */
	public static void append(String key, String value) {
		RedisAdvancedClusterAsyncCommands<String, String> commands = getCommands();
		commands.append(key, value);
	}
	
	/**
	 * 返回类型值
	 * @param key
	 * @return
	 */
	public static String type(String key) {
		try {
			RedisAdvancedClusterAsyncCommands<String, String> commands = getCommands();
			RedisFuture<String> future = commands.type(key);
			return future.get();
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 存储map
	 * @param key
	 * @param map
	 */
	public static void hmset(String key, Map<String, String> map) {
		RedisAdvancedClusterAsyncCommands<String, String> commands = getCommands();
		commands.hmset(key, map);
	}
	
	/**
	 * 根据key得到map对象
	 * @param key
	 * @return
	 */
	public static Map<String, String> hgetall(String key) {
		try {
			RedisAdvancedClusterAsyncCommands<String, String> commands = getCommands();
			RedisFuture<Map<String, String>> future = commands.hgetall(key);
			return future.get();
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return null;
		
	}
}

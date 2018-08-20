package com.data.utils;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.support.ConnectionPoolSupport;

/**
 * redis工具类
 * 采用lettuce客户端
 * @author alex
 *
 */
@Component
@Scope("singleton")
public class RedisUtil {
	
	private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);
	
	private static GenericObjectPool<StatefulRedisConnection<String, String>> pool;
	
	public static RedisClient client;
	
	public static StatefulRedisConnection<String, String> conn;
	
	//@Value("${spring.redis.host}")
	private String host = "127.0.0.1";
	
	//@Value("${spring.redis.password}")
	private String password = "123456";
	
	//@Value("${spring.redis.port}")
	private int port = 6379;
	
	//@Value("${spring.redis.timeout}")
	private long timeout = 5000;
	
	//@Value("${spring.redis.database}")
	private int database = 0;
	
	//@Value("${spring.redis.lettuce.pool.max-idle}")
	private int maxIdle = 8;
	
	//@Value("${spring.redis.lettuce.pool.min-idle}")
	private int minIdle = 0;
	
	//@Value("${spring.redis.lettuce.pool.max-active}")
	private int maxActive = 8;
	
	//@Value("${spring.redis.lettuce.pool.max-wait}")
	private int maxWait = -1;
	
//	static {
//		RedisURI uri = new RedisURI();
//		uri.setHost(REDIS_HOST);
//		uri.setPassword(REDIS_PASSWORD);
//		uri.setPort(REDIS_PORT);
//		uri.setTimeout(REDIS_TIMEOUT);
//		uri.setDatabase(REDIS_DATABASE);
//		client = RedisClient.create(uri);
//		//pool = client.asyncPool(REDIS_MAX_IDLE, REDIS_MAX_IDLE);
//		pool = ConnectionPoolSupport.createGenericObjectPool(() -> client.connect(), new GenericObjectPoolConfig());
//		try {
//			conn = pool.borrowObject();
//		} catch (Exception e) {
//			logger.info("--->>>连接初始化失败" + e.getMessage());
//		}
//	}
	
	@PostConstruct
	public void init() {
		RedisURI uri = new RedisURI();
		uri.setHost(host);
		uri.setPassword(password);
		uri.setPort(port);
		uri.setTimeout(timeout);
		uri.setDatabase(database);
		client = RedisClient.create(uri);
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);
		config.setMaxWaitMillis(maxWait);
		pool = ConnectionPoolSupport.createGenericObjectPool(() -> client.connect(), config);
		try {
			conn = pool.borrowObject();
		} catch (Exception e) {
			logger.info("--->>>连接初始化失败" + e.getMessage());
		}
	}
	
	/**
	 * 得到异步连接命令对象
	 * @return
	 */
	public static RedisAsyncCommands<String, String> getCommands() {
		return conn.async();
	}
	
	/**
	 * 关闭服务器时 关闭连接
	 */
	@PreDestroy
	public static void shutDown() {
		pool.close();
		client.shutdown();
	}
	
	/**
	 * 保存值
	 * @param key
	 * @param value
	 */
	public static void set(String key, String value) {
		RedisAsyncCommands<String, String> commands = getCommands();
		commands.set(key, value);
	}
	
	/**
	 * 保存值并设置时间
	 * @param key
	 * @param seconds 秒
	 * @param value
	 */
	public static void setex(String key, long seconds, String value) {
		RedisAsyncCommands<String, String> commands = getCommands();
		commands.setex(key, seconds, value);
	}
	
	/**
	 * 根据key值取value
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		try {
			RedisAsyncCommands<String, String> commands = getCommands();
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
			RedisAsyncCommands<String, String> commands = getCommands();
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
			RedisAsyncCommands<String, String> commands = getCommands();
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
			RedisAsyncCommands<String, String> commands = getCommands();
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
			RedisAsyncCommands<String, String> commands = getCommands();
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
			RedisAsyncCommands<String, String> commands = getCommands();
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
		RedisAsyncCommands<String, String> commands = getCommands();
		commands.del(key);
	}
	
	/**
	 * 根据key追加value
	 * @param key
	 * @param value
	 */
	public static void append(String key, String value) {
		RedisAsyncCommands<String, String> commands = getCommands();
		commands.append(key, value);
	}
	
	/**
	 * 返回类型值
	 * @param key
	 * @return
	 */
	public static String type(String key) {
		try {
			RedisAsyncCommands<String, String> commands = getCommands();
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
		RedisAsyncCommands<String, String> commands = getCommands();
		commands.hmset(key, map);
	}
	
	/**
	 * 根据key得到map对象
	 * @param key
	 * @return
	 */
	public static Map<String, String> hgetall(String key) {
		try {
			RedisAsyncCommands<String, String> commands = getCommands();
			RedisFuture<Map<String, String>> future = commands.hgetall(key);
			return future.get();
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return null;
		
	}
}

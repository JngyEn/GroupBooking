package app.xmum.xplorer.backend.groupbooking.utils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public  class RedisUtil {
    @Resource
    private  StringRedisTemplate stringRedisTemplate;


    public  void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, String value, long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public void set(String key, Map<String,Object> m) {
        stringRedisTemplate.opsForHash().putAll(key, m);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public Map<Object, Object> getMap(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    public void expire(String key, long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.expire(key, timeout,timeUnit);
    }
    // 存在返回true，否则返回false
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
    public Long increase(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }
    public Long increase(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }
    // ============================== ZSet 相关方法 ==============================

    /**
     * 添加元素到 ZSet
     *
     * @param key   ZSet 的 key
     * @param value 元素值
     * @param score 分数（用于排序）
     * @return 是否添加成功
     */
    public Boolean zAdd(String key, String value, double score) {
        return stringRedisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 批量添加元素到 ZSet
     *
     * @param key    ZSet 的 key
     * @param values 元素值及其分数的 Map
     * @return 添加的元素数量
     */
    public Long zAdd(String key, Map<String, Double> values) {
        Set<ZSetOperations.TypedTuple<String>> tuples = values.entrySet().stream()
                .map(entry -> ZSetOperations.TypedTuple.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
        return stringRedisTemplate.opsForZSet().add(key, tuples);
    }

    /**
     * 获取 ZSet 中元素的分数
     *
     * @param key   ZSet 的 key
     * @param value 元素值
     * @return 元素的分数
     */
    public Double zScore(String key, String value) {
        return stringRedisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 获取 ZSet 中元素的排名（按分数从小到大排序）
     *
     * @param key   ZSet 的 key
     * @param value 元素值
     * @return 元素的排名（从 0 开始）
     */
    public Long zRank(String key, String value) {
        return stringRedisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 获取 ZSet 中元素的排名（按分数从大到小排序）
     *
     * @param key   ZSet 的 key
     * @param value 元素值
     * @return 元素的排名（从 0 开始）
     */
    public Long zReverseRank(String key, String value) {
        return stringRedisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * 获取 ZSet 中指定范围的元素（按分数从小到大排序）
     *
     * @param key ZSet 的 key
     * @param start 起始位置（包含）
     * @param end 结束位置（包含）
     * @return 元素集合
     */
    public Set<String> zRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取 ZSet 中指定范围的元素（按分数从大到小排序）
     *
     * @param key ZSet 的 key
     * @param start 起始位置（包含）
     * @param end 结束位置（包含）
     * @return 元素集合
     */
    public Set<String> zReverseRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取 ZSet 中指定分数范围的元素（按分数从小到大排序）
     *
     * @param key ZSet 的 key
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @return 元素集合
     */
    public Set<String> zRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 获取 ZSet 中指定分数范围的元素（按分数从大到小排序）
     *
     * @param key ZSet 的 key
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @return 元素集合
     */
    public Set<String> zReverseRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * 获取 ZSet 的元素数量
     *
     * @param key ZSet 的 key
     * @return 元素数量
     */
    public Long zSize(String key) {
        return stringRedisTemplate.opsForZSet().size(key);
    }

    /**
     * 获取 ZSet 中指定分数范围的元素数量
     *
     * @param key ZSet 的 key
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @return 元素数量
     */
    public Long zCount(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * 移除 ZSet 中的元素
     *
     * @param key    ZSet 的 key
     * @param values 要移除的元素值
     * @return 移除的元素数量
     */
    public Long zRemove(String key, Object... values) {
        return stringRedisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 移除 ZSet 中指定排名范围的元素
     *
     * @param key   ZSet 的 key
     * @param start 起始位置（包含）
     * @param end   结束位置（包含）
     * @return 移除的元素数量
     */
    public Long zRemoveRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * 移除 ZSet 中指定分数范围的元素
     *
     * @param key ZSet 的 key
     * @param min 最小分数（包含）
     * @param max 最大分数（包含）
     * @return 移除的元素数量
     */
    public Long zRemoveRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

}

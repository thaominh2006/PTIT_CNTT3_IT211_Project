package ra.edu.it211_project.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * FR-13-AF3: Token Blacklist sử dụng Redis thay vì Database
 * - Tránh tắc nghẽn cổ chai (bottleneck) khi nhiều request kiểm tra blacklist liên tục
 * - Redis hỗ trợ TTL (Time To Live) tự động xóa key khi token hết hạn,
 *   không cần job dọn dữ liệu định kỳ như khi lưu DB
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisTokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Thêm token vào blacklist với TTL = thời gian còn lại của token.
     * Khi TTL về 0, Redis tự động xóa key -> không tốn bộ nhớ vô hạn.
     */
    public void blacklistToken(String token, long expirationMillis) {
        String key = BLACKLIST_PREFIX + token;
        long ttlSeconds = Math.max(expirationMillis / 1000, 1);
        redisTemplate.opsForValue().set(key, "revoked", Duration.ofSeconds(ttlSeconds));
        log.info("[Redis] Token blacklisted with TTL = {}s", ttlSeconds);
    }

    /**
     * Kiểm tra token có trong blacklist không.
     * Đây là phép tra cứu O(1) trên Redis, nhanh hơn nhiều so với query DB
     * mỗi khi có request -> giải quyết vấn đề tắc nghẽn cổ chai (bottleneck).
     */
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }
}
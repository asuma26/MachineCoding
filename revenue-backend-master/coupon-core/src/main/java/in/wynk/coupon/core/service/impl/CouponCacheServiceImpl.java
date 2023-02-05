package in.wynk.coupon.core.service.impl;

import in.wynk.coupon.core.dao.entity.Coupon;
import in.wynk.coupon.core.dao.repository.CouponDao;
import in.wynk.coupon.core.service.ICouponCacheService;
import in.wynk.data.enums.State;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static in.wynk.common.constant.BaseConstants.IN_MEMORY_CACHE_CRON;
import static in.wynk.logging.BaseLoggingMarkers.APPLICATION_ERROR;

@Slf4j
@Service
public class CouponCacheServiceImpl implements ICouponCacheService {

    private final Map<String, Coupon> couponMap = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    @Autowired
    private CouponDao couponDao;

    @Scheduled(fixedDelay = IN_MEMORY_CACHE_CRON, initialDelay = IN_MEMORY_CACHE_CRON)
    @PostConstruct
    public void init() {
        loadCoupons();
    }

    public void loadCoupons() {
        try {
            List<Coupon> coupons = couponDao.findCouponByState(State.ACTIVE);
            if (writeLock.tryLock() && !CollectionUtils.isEmpty(coupons)) {
                Map<String, Coupon> localCouponMap = new ConcurrentHashMap<>();
                for (Coupon coupon : coupons) {
                    localCouponMap.put(coupon.getId(), coupon);
                }
                couponMap.putAll(localCouponMap);
            }
        } catch (Throwable th) {
            log.error(APPLICATION_ERROR, "Exception occurred while refreshing coupons cache. Exception: {}", th.getMessage(), th);
            throw th;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Collection<Coupon> getAllCoupons() {
        return couponMap.values();
    }

    public Collection<String> getAllCouponIds() {
        return couponMap.keySet();
    }

    @Override
    public Coupon getCouponById(String id) {
        return couponMap.get(id);
    }

}

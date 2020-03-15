package nil.ed.livechat.common.util.redis;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BinaryOperator;

/**
 * Created at 2020-03-09
 *
 * @author lidelin
 */

public class SynchronizedRedisCache<T> {

    private QueryFunc<T> getter;

    private UpdateFunc<T> setter;

    private UpdateFunc<T> updateSuccess;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private Lock readLock = lock.readLock();

    private Lock writeLock = lock.writeLock();

    private String[] keys = {":A", ":B"};

    private String key;

    private int curIndex = 0;

    public SynchronizedRedisCache(QueryFunc<T> getter,
            UpdateFunc<T> setter, String key) {
        this.getter = getter;
        this.setter = setter;
        this.key = key;
    }

    public UpdateFunc<T> getUpdateSuccess() {
        return updateSuccess;
    }

    public void setUpdateSuccess(UpdateFunc<T> updateSuccess) {
        this.updateSuccess = updateSuccess;
    }

    public void update() {
        this.setter.update(getCurKey(), getNextKey());
        try {
            writeLock.lock();
            updateIndex();
        } finally {
            writeLock.unlock();
        }
        if (updateSuccess != null) {
            updateSuccess.update(getNextKey(), getCurKey());
        }
    }

    public T query() {
        try {
            this.readLock.lock();
            return this.getter.query(getCurKey());
        } finally {
            this.readLock.unlock();
        }
    }

    private int nextIndex() {
        return (curIndex + 1) % keys.length;
    }

    private int updateIndex() {
        return curIndex = nextIndex();
    }

    private  String getCurKey() {
        return key + keys[curIndex];
    }

    private String getNextKey() {
        return key + keys[nextIndex()];
    }

    @FunctionalInterface
    public interface QueryFunc<T> {
        /**
         * 获取结果
         *
         * @param key 当前键
         * @return 结果
         */
        T query(String key);
    }

    public interface UpdateFunc<T> {
        /**
         * 更新值
         * @param oldKey 老的key
         * @param key 当前的key
         */
        void update(String oldKey, String key);
    }
}

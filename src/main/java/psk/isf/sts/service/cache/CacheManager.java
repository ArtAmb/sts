package psk.isf.sts.service.cache;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CacheManager {

	private long startTimeInMs = 0;
	private Thread thread = null;

	@Value("${cache.manager.intervalInMs:5000}")
	private Long sleepTimeInMs;
	private List<AbstractCache<?, ?>> caches = new LinkedList<>();

	private CacheManager() {
		thread = new Thread(() -> {
			try {
				Thread.sleep(sleepTimeInMs);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			removeExpiredObjectFromCaches();
		});
	}

	public void removeExpiredObjectFromCaches() {
		long nowInMS = System.currentTimeMillis();
		long passedMs = nowInMS - startTimeInMs;
		long passedSec = passedMs / 1000;

		for (AbstractCache<?, ?> cache : caches) {
			cache.removeIfExpired(passedSec);
		}

		startTimeInMs = nowInMS;
	}

	public void addNewCache(AbstractCache<?, ?> cache) {
		caches.add(cache);
	}

	public void startCacheControlling() {
		thread.start();
	}

	// private CacheManager instance = null;
	// synchronized public CacheManager getInstance() {
	//
	// if (instance == null) {
	// instance = new CacheManager();
	// }
	//
	// return instance;
	// }

}

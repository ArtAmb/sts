package psk.isf.sts.service.cache;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import psk.isf.sts.service.cache.CacheObject.CacheObjectBuilder;

public abstract class AbstractCache<KEY, T> {
	private HashMap<KEY, CacheObject<T>> cacheHashMap = new HashMap<>();
	private List<KEY> keyLinkedList = new LinkedList<>();
	private Long durationInSec = null;

	public AbstractCache(CacheManager cacheManager) {
		cacheManager.addNewCache(this);
	}

	public T get(KEY id) {
		T result = null;
		CacheObject<T> cacheObj = cacheHashMap.get(id);
		result = cacheObj != null ? cacheObj.getObject() : null;

		try {
			if (result == null) {
				result = getWithoutCache(id);
				put(id, result);
			}

		} catch (Throwable e) {
			System.out.println("ERROR -> " + e.getMessage());
			result = getWithoutCache(id);
		}

		return result;
	}

	private void put(KEY key, T object) {
		CacheObjectBuilder<T> builder = new CacheObject.CacheObjectBuilder<T>();

		if (durationInSec != null)
			keyLinkedList.add(key);

		cacheHashMap.put(key, builder.object(object).duration(durationInSec).build());
	}

	protected abstract T getWithoutCache(KEY id);

	protected void setDuration(Long duration) {
		durationInSec = duration;
	}

	void remove(KEY id) {
		cacheHashMap.remove(id);
	}

	public void removeIfExpired(Long howManyPassed) {
		if (durationInSec == null)
			return;

		for (int i = 0; i < keyLinkedList.size();) {
			KEY key = keyLinkedList.get(0);

			CacheObject<T> obj = cacheHashMap.get(key);
			if (obj == null) {
				keyLinkedList.remove(i);
				continue;
			}
			Long newDuration = obj.getDuration() - howManyPassed;

			if (newDuration <= 0) {
				remove(key);
				keyLinkedList.remove(i);
				continue;
			}

			obj.setDuration(newDuration);
			++i;
		}

	}

	public void synchronize(KEY key, T object) {
		try {

		} catch (Throwable t) {
			System.out.println("ERROR: CANNOT SYNCHRONIZE -> " + t.getMessage());
		}
	}
}

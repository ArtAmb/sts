package psk.isf.sts.service.cache.serial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.repository.SerialRepository;
import psk.isf.sts.service.cache.AbstractCache;
import psk.isf.sts.service.cache.CacheManager;

@Service
public class SerialCacheService extends AbstractCache<Long, SerialElement> {

	@Autowired
	private SerialRepository serialRepository;

	@Autowired
	public SerialCacheService(CacheManager cacheManager, @Value("${serial.cache.duration}") Long duration) {
		super(cacheManager);
		if (duration == -1)
			duration = null;

		setDuration(duration);
	}

	@Override
	protected SerialElement getWithoutCache(Long id) {
		return serialRepository.findOne(id);
	}

}

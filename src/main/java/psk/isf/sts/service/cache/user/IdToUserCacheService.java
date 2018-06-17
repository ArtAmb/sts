package psk.isf.sts.service.cache.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.UserRepository;
import psk.isf.sts.service.cache.AbstractCache;
import psk.isf.sts.service.cache.CacheManager;

@Service
public class IdToUserCacheService extends AbstractCache<Long, User> {

	@Autowired
	private UserRepository userRepository;

	@Value("${user.cache.duration:-1}")
	private Long duration;

	@Autowired
	public IdToUserCacheService(CacheManager cacheManager) {
		super(cacheManager);
		if (duration == -1)
			duration = null;

		setDuration(duration);
	}

	@Override
	protected User getWithoutCache(Long id) {
		return userRepository.findOne(id);
	}

}

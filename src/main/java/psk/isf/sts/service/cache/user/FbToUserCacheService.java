package psk.isf.sts.service.cache.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.registration.UserSourceSystem;
import psk.isf.sts.repository.UserRepository;
import psk.isf.sts.service.cache.AbstractCache;
import psk.isf.sts.service.cache.CacheManager;

@Service
public class FbToUserCacheService extends AbstractCache<String, User> {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	public FbToUserCacheService(CacheManager cacheManager, @Value("${serial.cache.duration}") Long duration) {
		super(cacheManager);
		if (duration == -1)
			duration = null;

		setDuration(duration);
	}

	@Override
	protected User getWithoutCache(String extId) {
		return userRepository.findByExtIdAndSourceSystem(extId, UserSourceSystem.FACEBOOK);
	}

}

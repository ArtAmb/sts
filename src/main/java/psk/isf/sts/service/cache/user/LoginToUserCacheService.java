package psk.isf.sts.service.cache.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.authorization.UserRepository;
import psk.isf.sts.service.cache.AbstractCache;
import psk.isf.sts.service.cache.CacheManager;

@Service
public class LoginToUserCacheService extends AbstractCache<String, User> {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	public LoginToUserCacheService(CacheManager cacheManager, @Value("${serial.cache.duration}") Long duration) {
		super(cacheManager);
		if (duration == -1)
			duration = null;

		setDuration(duration);
	}

	@Override
	protected User getWithoutCache(String login) {
		return userRepository.findByLogin(login);
	}

}

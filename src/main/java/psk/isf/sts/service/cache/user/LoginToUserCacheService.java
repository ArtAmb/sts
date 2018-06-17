package psk.isf.sts.service.cache.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.UserRepository;
import psk.isf.sts.service.cache.AbstractCache;
import psk.isf.sts.service.cache.CacheManager;

@Service
public class LoginToUserCacheService extends AbstractCache<String, User> {

	@Autowired
	private UserRepository userRepository;

	@Value("${user.cache.duration:-1}")
	private Long duration;

	@Autowired
	public LoginToUserCacheService(CacheManager cacheManager) {
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

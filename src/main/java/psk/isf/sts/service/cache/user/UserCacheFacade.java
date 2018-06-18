package psk.isf.sts.service.cache.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.registration.User;

@Service
public class UserCacheFacade {

	@Autowired
	private IdToUserCacheService idToUserCacheService;

	@Autowired
	private LoginToUserCacheService loginToUserCacheService;

	@Autowired
	private FbToUserCacheService fbToUserCacheService;

	public User getById(Long id) {
		return idToUserCacheService.get(id);
	}

	public User getByLogin(String login) {
		return loginToUserCacheService.get(login);
	}

	public User getByFbId(String fbId) {
		return fbToUserCacheService.get(fbId);
	}

	public void synchronize(User user) {
		String login = user.getLogin();
		Long id = user.getId();
		String fbId = user.getExtId();

		loginToUserCacheService.synchronize(login, user);
		idToUserCacheService.synchronize(id, user);
		if (fbId != null)
			fbToUserCacheService.synchronize(fbId, user);
	}

}

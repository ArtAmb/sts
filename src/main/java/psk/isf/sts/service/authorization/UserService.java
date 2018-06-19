package psk.isf.sts.service.authorization;

import java.io.IOException;
import java.util.Collection;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import psk.isf.sts.entity.contract.Contract;
import psk.isf.sts.entity.myserial.MySerial;
import psk.isf.sts.entity.picture.Picture;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.registration.UserSourceSystem;
import psk.isf.sts.entity.serial.SerialElement;
import psk.isf.sts.repository.ContractRepository;
import psk.isf.sts.repository.authorization.UserRepository;
import psk.isf.sts.repository.myserial.MySerialRepository;
import psk.isf.sts.service.PictureService;
import psk.isf.sts.service.cache.user.UserCacheFacade;

@Service
public class UserService {

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Value("${serial.cache.enabled}")
	private Boolean isCacheEnabled;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserCacheFacade userCacheFacade;

	@Autowired
	private ContractRepository contractRepository;

	@Autowired
	private PictureService pictureService;

	@Autowired
	private MySerialRepository mySerialRepository;

	public User findById(Long id) {
		if (isCacheEnabled) {
			return userCacheFacade.getById(id);
		}

		return userRepo.findOne(id);
	}

	public User findByLogin(String login) {
		if (isCacheEnabled) {
			return userCacheFacade.getByLogin(login);
		}

		return userRepo.findByLogin(login);
	}

	public User saveUser(User user) {
		if (isCacheEnabled) {
			userCacheFacade.synchronize(user);
		}
		return userRepo.save(user);
	}

	public User setThumbnailForUserByLogin(String login, MultipartFile thumbnail)
			throws IllegalStateException, IOException {
		User user = findByLogin(login);
		if (StringUtils.isNullOrEmpty(thumbnail.getOriginalFilename()))
			return user;

		Picture picture = pictureService.savePicture(login, thumbnail);
		user.setThumbnail(picture);
		return saveUser(user);
	}

	public void deleteUser(User user) {
		userRepo.delete(user.getId());
	}

	public String activateProducerAccount(User user) {
		RandomStringGenerator rsg = new RandomStringGenerator(8);
		String rawPassword = rsg.rand();

		user.setPassword(encoder.encode(rawPassword));
		user.setReal(true);
		user.setDisabled(true);
		saveUser(user);

		return rawPassword;
	}

	public void makeProducerAccountFullActive(User user) {
		user.setDisabled(false);
		saveUser(user);
	}

	public void removeNotRegisteredProducerAccount(User user, Contract contract) {
		contractRepository.delete(contract.getId());
		deleteUser(user);
	}

	public User findFacebookUserByFbId(String fbId) {
		if (isCacheEnabled) {
			userCacheFacade.getByFbId(fbId);
		}
		return userRepo.findByExtIdAndSourceSystem(fbId, UserSourceSystem.FACEBOOK);
	}

	public Collection<User> findAll() {
		return userRepo.findAll();
	}

	public long updateCurentlyWatchedEpisodeAndReturnLeftTime(User user) {
		Long currTime = System.currentTimeMillis();
		if (user.getCurrentlyWatchedEpisode() != null) {
			long epDurationMS = user.getCurrentlyWatchedEpisode().getDurationInSec() * 1000;
			Long epFinishTime = user.getWatchingEpisodeStartTimeInMs() + epDurationMS;
			long leftTimeMS = epFinishTime - currTime;
			if (leftTimeMS <= 0) {
				MySerial mySerial = mySerialRepository.findByUserAndSerial(user, user.getCurrentlyWatchedEpisode());
				mySerial.setWatched(true);
				mySerialRepository.save(mySerial);

				user.setWatchingEpisodeStartTimeInMs(null);
				user.setCurrentlyWatchedEpisode(null);
				saveUser(user);
			}

			return leftTimeMS;
		}

		return -1;
	}

	public void watchNow(User user, SerialElement episode) {
		MySerial mySerial = mySerialRepository.findByUserAndSerial(user, episode);
		if(mySerial == null) {
			throw new IllegalStateException("Ten serial musi zostac dodany do 'moich'");
		}
		if(mySerial.isWatched()) {
			throw new IllegalStateException("Ten odcinek juz zostal obejrzany");
		}
		user.setWatchingEpisodeStartTimeInMs(System.currentTimeMillis());
		user.setCurrentlyWatchedEpisode(episode);
		saveUser(user);
	}

}

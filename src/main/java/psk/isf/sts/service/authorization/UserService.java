package psk.isf.sts.service.authorization;

import java.io.IOException;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import psk.isf.sts.entity.Contract;
import psk.isf.sts.entity.Picture;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.ContractRepository;
import psk.isf.sts.repository.UserRepository;
import psk.isf.sts.service.PictureService;

@Service
public class UserService {

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ContractRepository contractRepository;

	@Autowired
	private PictureService pictureService;

	public User findById(Long id) {
		return userRepo.findOne(id);
	}

	public User findByLogin(String login) {
		return userRepo.findByLogin(login);
	}

	public User saveUser(User user) {
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

}

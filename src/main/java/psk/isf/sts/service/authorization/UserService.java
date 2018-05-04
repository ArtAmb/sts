package psk.isf.sts.service.authorization;

import java.io.IOException;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import psk.isf.sts.entity.Picture;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.UserRepository;
import psk.isf.sts.service.PictureService;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PictureService pictureService;

	public User findById(Long id) {
		return userRepo.findById(id).get();
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
		userRepo.deleteById(user.getId());
	}

}

package psk.isf.sts.controller.rest;

import java.io.File;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import psk.isf.sts.entity.picture.Picture;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.serial.SerialElement;
import psk.isf.sts.repository.PictureRepository;
import psk.isf.sts.service.authorization.UserService;
import psk.isf.sts.service.series.SerialService;

@RestController
public class MainRestController {

	@Autowired
	private PictureRepository pictureRepo;

	@Autowired
	private UserService userService;
	
	@Autowired
	private SerialService serialService;

	@GetMapping("rest/")
	public MultipartFile getFileByPictureId(@PathVariable Long id) {
		Picture picture = pictureRepo.findOne(id);
		File file = new File(picture.getPath());

		return null;
	}

	@GetMapping("/rest/user/instance")
	public User getUserInstance(Principal principal) {
		return userService.findByLogin(principal.getName());
	}

	@GetMapping("/rest/serialElement/{id}/progress")
	public Long countProgress(@PathVariable Long id, Principal principal) {
		User user = userService.findByLogin(principal.getName());
		
		return serialService.countProgressForSeason(id, user);
	}
}

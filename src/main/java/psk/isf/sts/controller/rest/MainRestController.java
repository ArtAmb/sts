package psk.isf.sts.controller.rest;

import java.io.File;
import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import psk.isf.sts.entity.picture.Picture;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.serial.SerialElement;
import psk.isf.sts.repository.PictureRepository;
import psk.isf.sts.repository.myserial.MySerialRepository;
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

	@Autowired
	private MySerialRepository mySerialRepository;

	@GetMapping("rest/")
	public MultipartFile getFileByPictureId(@PathVariable Long id) {
		Picture picture = pictureRepo.findOne(id);
		File file = new File(picture.getPath());

		return null;
	}

	@GetMapping("/rest/user/instance")
	public User getUserInstance(HttpSession session, Principal principal) {
		// User user = userService.findByLogin(principal.getName());
		// if(user.getSourceSystem().equals(UserSourceSystem.FACEBOOK) &&
		// connectionRepository.findPrimaryConnection(Facebook.class) == null) {
		// System.out.println("getUserInstance -> connection with FB has been cut. I am
		// invalidating session!");
		// session.invalidate();
		// }

		return userService.findByLogin(principal.getName());
	}

	@GetMapping("/rest/serialElement/{id}/progress")
	public Long countProgress(@PathVariable Long id, Principal principal) {
		User user = userService.findByLogin(principal.getName());

		userService.updateCurentlyWatchedEpisodeAndReturnLeftTime(user);

		return serialService.countProgressForSeason(id, user);
	}

	@PostMapping("/rest/serialElement/{id}/watchNow")
	public String watchNow(@PathVariable Long id, Principal principal) {
		User user = userService.findByLogin(principal.getName());
		SerialElement episode = serialService.findById(id);
		try {

			if (user.getCurrentlyWatchedEpisode() != null) {
				long leftTimeMS = userService.updateCurentlyWatchedEpisodeAndReturnLeftTime(user);
				if (leftTimeMS <= 0) {
					userService.watchNow(user, episode);
					return "Ogladasz teraz " + episode.getTitle();
				}
				float leftTimeSEC = leftTimeMS / 1000.0f;
				float leftTimeMin = leftTimeSEC / 60;

				return "Obecnie ogladasz " + user.getCurrentlyWatchedEpisode().getTitle() + ". Spróbuj za "
						+ (int) leftTimeMin + " minuty " + (int) (leftTimeSEC - (60 * (int) leftTimeMin)) + " sekund.";
			}

			userService.watchNow(user, episode);
		} catch (IllegalStateException e) {
			return e.getMessage();
		}
		return "Ogladasz teraz " + episode.getTitle();
	}

	@PostMapping("/rest/logout")
	public void logout(HttpSession session, Principal principal) {
		session.invalidate();

		// connectionRepository.findPrimaryConnection(Facebook.class);
		// MultiValueMap<String, Connection<?>> connections =
		// connectionRepository.findAllConnections();
		// for (Entry<String, List<Connection<?>>> con : connections.entrySet()) {
		// System.out.println(con.getKey());
		//
		// }
		// restTemplate.postForEntity(selfUrl + "/logout", null, Void.class);
	}

}

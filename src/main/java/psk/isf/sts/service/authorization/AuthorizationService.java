package psk.isf.sts.service.authorization;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import psk.isf.sts.entity.Picture;
import psk.isf.sts.entity.UserType;
import psk.isf.sts.entity.registration.Role;
import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.registration.UserSourceSystem;
import psk.isf.sts.repository.UserRepository;
import psk.isf.sts.service.authorization.dto.AuthorizationDTO;
import psk.isf.sts.service.authorization.dto.UserDTO;

@Service
public class AuthorizationService {

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	private RestTemplate restTemplate = new RestTemplate();

	@Value("${sts.self.url}")
	private String selfUrl;
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserService userService;

	public boolean createNewUser(AuthorizationDTO dto, UserDTO userDTO) throws Exception {
		validate(dto);

		User user = User.builder()
				.login(dto.getLogin())
				.displayLogin(dto.getLogin())
				.password(encoder.encode(dto.getPassword()))
				.email(dto.getEmail())
				.roles(Arrays.asList(userDTO.getRole()))
				.userType(userDTO.getUserType())
				.real(userDTO.isReal())
				.build();

		userRepo.save(user);
		return true;

	}

	public void validate(AuthorizationDTO dto) throws Exception {
		if (StringUtils.isNullOrEmpty(dto.getEmail())) {
			throw new Exception("Email nie moze byc pusty");
		}
		if (StringUtils.isNullOrEmpty(dto.getLogin())) {
			throw new Exception("Login nie moze byc pusty");
		}
		if (StringUtils.isNullOrEmpty(dto.getPassword())) {
			throw new Exception("Hasło nie może być puste");
		}
		if (StringUtils.isNullOrEmpty(dto.getRepeatedPassword())) {
			throw new Exception("Prosze powtorzyc hasło");
		}
		if (!dto.getPassword().equals(dto.getRepeatedPassword())) {
			throw new Exception("Hasła się róznią!");
		}

		if (userRepo.existsByLogin(dto.getLogin())) {
			throw new Exception("Uzytkownik o takim loginie juz istnieje");
		}

		if (userRepo.existsByEmail(dto.getEmail())) {
			throw new Exception("Konto na ten mail juz isntnieje");
		}

	}

	public void validateForProducer(AuthorizationDTO dto) throws Exception {
		if (StringUtils.isNullOrEmpty(dto.getEmail())) {
			throw new Exception("Email nie moze byc pusty");
		}
		if (StringUtils.isNullOrEmpty(dto.getLogin())) {
			throw new Exception("Login nie moze byc pusty");
		}
		if (StringUtils.isNullOrEmpty(dto.getNip())) {
			throw new Exception("Nip nie moze byc pusty");
		}
		if (StringUtils.isNullOrEmpty(dto.getPhoneNumber())) {
			throw new Exception("Telefon nie moze byc pusty");
		}
		if (StringUtils.isNullOrEmpty(dto.getCompanyName())) {
			throw new Exception("Nazwa firmy jest wymagana");
		}
		if (StringUtils.isNullOrEmpty(dto.getAddress())) {
			throw new Exception("Adres jest wymagany");
		}
		if (userRepo.existsByLogin(dto.getLogin())) {
			throw new Exception("Uzytkownik o takim loginie juz istnieje");
		}

		if (userRepo.existsByEmail(dto.getEmail())) {
			throw new Exception("Konto na ten mail juz isntnieje");
		}

	}

	public User createNewProducer(AuthorizationDTO dto) throws Exception {
		validateForProducer(dto);

		RandomStringGenerator randomStringGenerator = new RandomStringGenerator(8);
		String password = randomStringGenerator.rand();

		User user = User.builder()
				.login(dto.getLogin())
				.displayLogin(dto.getLogin())
				.password(encoder.encode(password))
				.email(dto.getEmail())
				.roles(Arrays.asList(Roles.ROLE_PRODUCER.toRole()))
				.nip(dto.getNip())
				.phoneNumber(dto.getPhoneNumber())
				.address(dto.getAddress())
				.companyName(dto.getCompanyName())
				.userType(UserType.PRODUCER)
				.disabled(true)
				.real(false)
				.build();

		return userRepo.save(user);
	}

	public String getFbUserLogin(org.springframework.social.facebook.api.User fbProfile) {
		return "fbUser_" + fbProfile.getId();
	}

	public User createFacebookUser(org.springframework.social.facebook.api.User fbProfile) {
		String fbuserLogin = getFbUserLogin(fbProfile);
		Picture thumbnail = Picture.builder().fromOurServer(false).name("fbPicture_" + fbProfile.getId())
				.path("http://graph.facebook.com/" + fbProfile.getId() + "/picture?type=square").build();

		List<Role> roleList = new LinkedList<>();
		roleList.add(Roles.ROLE_VIEWER.toRole());

		User user = User.builder().login(fbuserLogin)
				.displayLogin(fbProfile.getFirstName() + " " + fbProfile.getLastName())
				.password(encoder.encode(new RandomStringGenerator(8).rand()))
				.email(fbProfile.getEmail())
				.roles(roleList)
				.userType(UserType.VIEWER)
				.sourceSystem(UserSourceSystem.FACEBOOK)
				.active(true)
				.real(true)
				.thumbnail(thumbnail)
				.extId(fbProfile.getId())
				.build();

		return userRepo.save(user);
	}

	public String createSingInFbUserUrl(org.springframework.social.facebook.api.User fbProfile) {
		User user = userService.findFacebookUserByFbId(fbProfile.getId());
		String rawPassword = new RandomStringGenerator(8).rand();

		user.setPassword(encoder.encode(rawPassword));
		userRepo.save(user);

		return String.format("/login?login=%s&password=%s", user.getLogin(), rawPassword);
	}

	public void singInFbUser(User user) {
		String rawPassword = new RandomStringGenerator(8).rand();

		user.setPassword(encoder.encode(rawPassword));
		userRepo.save(user);

		restTemplate.postForObject(selfUrl + String.format("/login?login=%s&password=%s", user.getLogin(), rawPassword),
				null, Void.class);
	}

}

package psk.isf.sts.service.authorization;

import java.util.Arrays;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.UserType;
import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.UserRepository;
import psk.isf.sts.service.authorization.dto.AuthorizationDTO;
import psk.isf.sts.service.authorization.dto.UserDTO;

@Service
public class AuthorizationService {

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	private UserRepository userRepo;

	public boolean createNewUser(AuthorizationDTO dto, UserDTO userDTO) throws Exception {
		validate(dto);

		User user = User.builder().login(dto.getLogin()).password(encoder.encode(dto.getPassword()))
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
	
	public void createNewProducer(AuthorizationDTO dto) throws Exception {
		validate(dto);

		RandomStringGenerator randomStringGenerator = new RandomStringGenerator(8);
		String password = randomStringGenerator.rand();
		
		User user = User.builder()
				.login(dto.getLogin())
				.password(encoder.encode(password))
				.email(dto.getEmail())
				.roles(Arrays.asList(Roles.ROLE_PRODUCER.toRole()))
				.nip(dto.getNip())
				.phoneNumber(dto.getPhoneNumber())
				.userType(UserType.PRODUCER)
				.disabled(true)
				.real(false)
				.build();

		
		
		userRepo.save(user);

	}


}

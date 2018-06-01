package psk.isf.sts.service.authorization;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.UserType;
import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.UserRepository;

@Service
public class FacebookConnectionSignup implements ConnectionSignUp {

	@Autowired
	private UserRepository userRepository;

	@Override
	public String execute(Connection<?> connection) {
		User user = new User();
		user.setLogin(connection.getDisplayName());
		user.setPassword(new RandomStringGenerator(8).rand());
		user.setRoles(Arrays.asList((Roles.ROLE_VIEWER.toRole())));
		user.setUserType(UserType.VIEWER);
		userRepository.save(user);
		return user.getLogin();
	}
}
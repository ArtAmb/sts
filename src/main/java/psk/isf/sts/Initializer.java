package psk.isf.sts;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import psk.isf.sts.entity.registration.Role;
import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.RoleRepository;
import psk.isf.sts.repository.UserRepository;

@Component
public class Initializer {

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private UserRepository userRepo;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Transactional
	void addSystemUsers() {
		for (Role role : Roles.toRoleValues()) {
			roleRepo.save(role);
		}

		List<Role> developerRoles = new LinkedList<>();
		developerRoles.add(Roles.ROLE_ADMIN.toRole());
		developerRoles.add(Roles.ROLE_CLIENT.toRole());
		developerRoles.add(Roles.ROLE_PRODUCER.toRole());

		userRepo.save(User.builder()
				.login("client")
				.email("client@test.pl")
				.password(encoder.encode("test"))
				.roles(Collections.singletonList((Roles.ROLE_CLIENT.toRole())))
				.build());
		
		userRepo.save(User.builder()
				.login("admin")
				.email("admin@test.pl")
				.password(encoder.encode("test"))
				.roles(Collections.singletonList((Roles.ROLE_ADMIN.toRole())))
				.build());
		
		userRepo.save(User.builder()
				.login("producer")
				.email("producer@test.pl")
				.password(encoder.encode("test"))
				.roles(Collections.singletonList((Roles.ROLE_PRODUCER.toRole())))
				.build());
		
		userRepo.save(User.builder()
				.login("developer")
				.email("developer@test.pl")
				.password(encoder.encode("test"))
				.roles(developerRoles)
				.build());
	}

	@PostConstruct
	void initApplication() {
		addSystemUsers();
	}

}

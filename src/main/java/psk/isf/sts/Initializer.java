package psk.isf.sts;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import psk.isf.sts.entity.Actor;
import psk.isf.sts.entity.Genre;
import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.entity.SerialElementType;
import psk.isf.sts.entity.UserType;
import psk.isf.sts.entity.registration.Role;
import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.ActorRepository;
import psk.isf.sts.repository.GenreRepository;
import psk.isf.sts.repository.RoleRepository;
import psk.isf.sts.repository.SerialRepository;
import psk.isf.sts.repository.UserRepository;

@Component
public class Initializer {

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ActorRepository actorRepo;

	@Autowired
	private SerialRepository serialRepository;

	@Autowired
	private GenreRepository genreRepository;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Transactional
	void addSystemUsers() {
		for (Role role : Roles.toRoleValues()) {
			roleRepo.save(role);
		}

		List<Role> developerRoles = new LinkedList<>();
		developerRoles.add(Roles.ROLE_ADMIN.toRole());
		developerRoles.add(Roles.ROLE_VIEWER.toRole());
		developerRoles.add(Roles.ROLE_PRODUCER.toRole());

		userRepo.save(User.builder().login("viewer").email("viewer@test.pl").password(encoder.encode("test"))
				.roles(Collections.singletonList((Roles.ROLE_VIEWER.toRole()))).userType(UserType.VIEWER).build());

		userRepo.save(User.builder().login("admin").email("admin@test.pl").password(encoder.encode("test"))
				.roles(Collections.singletonList((Roles.ROLE_ADMIN.toRole()))).userType(UserType.PRODUCER).build());

		userRepo.save(User.builder().login("producer").email("producer@test.pl").password(encoder.encode("test"))
				.roles(Collections.singletonList((Roles.ROLE_PRODUCER.toRole()))).build());

		userRepo.save(User.builder().login("developer").email("developer@test.pl").password(encoder.encode("test"))
				.roles(developerRoles).build());
	}

	void addSerials() {
		User user = userRepo.findByLogin("producer");

		List<Actor> actorList = Arrays.asList(
				actorRepo.save(Actor.builder().name("Arnold").surname("Arnoldowski").build()),
				actorRepo.save(Actor.builder().name("Teodor").surname("Teodorski").build()),
				actorRepo.save(Actor.builder().name("Agnieszka").surname("Anieszkowska").build()),
				actorRepo.save(Actor.builder().name("Klaudia").surname("Klaudiowska").build()));

		Genre thiller = genreRepository.save(Genre.builder().name("Thriller").build());
		Genre dramat = genreRepository.save(Genre.builder().name("Dramat").build());
		Genre horror = genreRepository.save(Genre.builder().name("Horror").build());
		Genre przygodowy = genreRepository.save(Genre.builder().name("Przygodowy").build());

		serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("Wikingowie")
				.description("Historia o tym co wikingowie jedza, piją i co zabijają :D").producer(user).active(true)
				.actors(actorList).genres(Arrays.asList(dramat, przygodowy)).build());

		serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("Manaty")
				.description("Historia o tym co manaty jedza, piją i co zabijają :D").producer(user).active(true)
				.actors(actorList).genres(Arrays.asList(dramat, przygodowy, horror)).build());

		serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("Kanibale")
				.description("Historia o tym co kanibale jedza, piją i co zabijają :D").producer(user).active(true)
				.actors(actorList).genres(Arrays.asList(thiller, przygodowy)).build());

		serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("Ptaki")
				.description("Historia o tym co ptaki jedza, piją i co zabijają :D").producer(user).active(true)
				.actors(actorList).genres(Arrays.asList(thiller, horror)).build());

	}

	@PostConstruct
	void initApplication() {
		addSystemUsers();
		addSerials();
	}

}

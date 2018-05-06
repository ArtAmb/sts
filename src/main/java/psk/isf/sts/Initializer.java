package psk.isf.sts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import psk.isf.sts.entity.Actor;
import psk.isf.sts.entity.Genre;
import psk.isf.sts.entity.Picture;
import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.entity.SerialElementType;
import psk.isf.sts.entity.UserType;
import psk.isf.sts.entity.registration.Role;
import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.ActorRepository;
import psk.isf.sts.repository.GenreRepository;
import psk.isf.sts.repository.PictureRepository;
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

	@Autowired
	private PictureRepository pictureRepo;

	@Value("${sts.path.to.contract.templates}")
	private String pathToContractTemplate;

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

		userRepo.save(User.builder().login("newproducer").email("psk-isf-sts@wp.pl").password(encoder.encode("test"))
				.roles(Collections.singletonList((Roles.ROLE_PRODUCER.toRole()))).companyName("TestFirmaNewProducer")
				.address("Tajna 5/12 Kielce").phoneNumber("123456789").disabled(true).build());

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

		Picture mJakMilosc = pictureRepo.save(Picture.builder().name("m_jak_milosc.png").build());
		Picture pierwszaMilosc = pictureRepo.save(Picture.builder().name("pierwsza_milosc.jpg").build());
		Picture naWspolnej = pictureRepo.save(Picture.builder().name("na_wspolnej.jpg").build());
		Picture przyjaciolki = pictureRepo.save(Picture.builder().name("przyjaciolki.jpg").build());

		Genre thiller = genreRepository.save(Genre.builder().name("Thriller").build());
		Genre dramat = genreRepository.save(Genre.builder().name("Dramat").build());
		Genre horror = genreRepository.save(Genre.builder().name("Horror").build());
		Genre przygodowy = genreRepository.save(Genre.builder().name("Przygodowy").build());

		serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("M jak miłość")
				.description("Polski serial telewizyjny. Akcja rozgrywa się i tak dalej").producer(user).active(true)
				.actors(actorList).genres(Arrays.asList(dramat, przygodowy)).thumbnail(mJakMilosc).build());

		serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("Pierwsza Miłość")
				.description("Polski serial telewizyjny. Akcja rozgrywa się i tak dalej").producer(user).active(true)
				.actors(actorList).genres(Arrays.asList(dramat, przygodowy, horror)).thumbnail(pierwszaMilosc).build());

		serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("Na wspólnej")
				.description("Polski serial telewizyjny. Akcja rozgrywa się i tak dalej").producer(user).active(true)
				.actors(actorList).genres(Arrays.asList(thiller, przygodowy)).thumbnail(naWspolnej).build());

		serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("Przyjaciółki")
				.description("Polski serial telewizyjny. Akcja rozgrywa się i tak dalej").producer(user).active(true)
				.actors(actorList).genres(Arrays.asList(thiller, horror)).thumbnail(przyjaciolki).build());

	}

	void setContractTemplate() throws IOException {
		File file = new File(pathToContractTemplate + "/contract-template-1.txt");
		if (file.exists())
			return;

		File source = new File("src/main/resources/static/contract-templates/contract-template-1.txt");
		// File destination = new File(pathToContractTemplate + File.separator);

		Files.copy(source.toPath(), file.toPath());
	}

	@PostConstruct
	void initApplication() throws IOException {
		addSystemUsers();
		addSerials();
		setContractTemplate();
	}

}

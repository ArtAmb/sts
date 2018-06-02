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

import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import psk.isf.sts.entity.Actor;
import psk.isf.sts.entity.Genre;
import psk.isf.sts.entity.Picture;
import psk.isf.sts.entity.Playlist;
import psk.isf.sts.entity.PlaylistElement;
import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.entity.SerialElementType;
import psk.isf.sts.entity.UserType;
import psk.isf.sts.entity.registration.Role;
import psk.isf.sts.entity.registration.Roles;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.repository.ActorRepository;
import psk.isf.sts.repository.GenreRepository;
import psk.isf.sts.repository.PictureRepository;
import psk.isf.sts.repository.PlaylistElementRepository;
import psk.isf.sts.repository.PlaylistRepository;
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
	
	@Autowired
	PlaylistRepository playlistRepo;
	
	@Autowired
	PlaylistElementRepository playlistElementRepo;

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

		Picture witoldPyrkosz = pictureRepo.save(Picture.builder().name("Witold_Pyrkosz.jpg").build());
		Picture teresaLipowska = pictureRepo.save(Picture.builder().name("Teresa_Lipowska.jpg").build());
		Picture kacperKuszewski = pictureRepo.save(Picture.builder().name("Kacper_Kuszewski.jpg").build());
		Picture noPhoto = pictureRepo.save(Picture.builder().name("no_photo.jpg").build());

		List<Actor> actorListMJakMilosc = Arrays.asList(
				actorRepo.save(Actor.builder().name("Witold").surname("Pyrkosz").thumbnail(witoldPyrkosz).build()),
				actorRepo.save(Actor.builder().name("Teresa").surname("Lipowska").thumbnail(teresaLipowska).build()),
				actorRepo.save(Actor.builder().name("Kacper").surname("Kuszewski").thumbnail(kacperKuszewski).build()));

		List<Actor> actorList = Arrays.asList(
				actorRepo.save(Actor.builder().name("Arnold").surname("Arnoldowski").thumbnail(noPhoto).build()),
				actorRepo.save(Actor.builder().name("Teodor").surname("Teodorski").thumbnail(noPhoto).build()),
				actorRepo.save(Actor.builder().name("Agnieszka").surname("Anieszkowska").thumbnail(noPhoto).build()),
				actorRepo.save(Actor.builder().name("Klaudia").surname("Klaudiowska").thumbnail(noPhoto).build()));

		Picture mJakMilosc = pictureRepo.save(Picture.builder().name("m_jak_milosc.jpg").build());
		Picture pierwszaMilosc = pictureRepo.save(Picture.builder().name("pierwsza_milosc.jpg").build());
		Picture naWspolnej = pictureRepo.save(Picture.builder().name("na_wspolnej.jpg").build());
		Picture przyjaciolki = pictureRepo.save(Picture.builder().name("przyjaciolki.jpg").build());

		Genre thiller = genreRepository.save(Genre.builder().name("Thriller").build());
		Genre dramat = genreRepository.save(Genre.builder().name("Dramat").build());
		Genre horror = genreRepository.save(Genre.builder().name("Horror").build());
		Genre przygodowy = genreRepository.save(Genre.builder().name("Przygodowy").build());
		Genre sciFi = genreRepository.save(Genre.builder().name("sci-fi").build());

		
		
		
		
		SerialElement mJakMiloscSerial = serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("M jak miłość")
				.description("M jak Miłość to serial opisujący zagmatwane losy trzy-pokoleniowej rodziny Mostowiaków. Lucjan i Barbara są rodzicami czworga dorosłych ludzi: Marty, Marka, Małgosi i Marysi. ").producer(user).active(true)
				.actors(actorListMJakMilosc).genres(Arrays.asList(dramat, przygodowy)).thumbnail(mJakMilosc).build());
		
		SerialElement mJakMiloscSeason1 = serialRepository.save(SerialElement.builder().elementType(SerialElementType.SEASON).parent(mJakMiloscSerial)
				.title("Sezon 1 M jak miłość").description("Pierwszy sezon...").producer(user).active(true).thumbnail(noPhoto).build());
		
		serialRepository.save(SerialElement.builder().elementType(SerialElementType.SEASON).parent(mJakMiloscSerial)
				.title("Sezon 2 M jak miłość").description("Drugi sezon...").producer(user).active(true).thumbnail(noPhoto).build());
		
		serialRepository.save(SerialElement.builder().elementType(SerialElementType.EPISODE).parent(mJakMiloscSeason1)
				.title("Odcinek 1 - Nie ma jak w domu ").description("Poznajmy rodzinę Mostowiaków! ").producer(user).active(true).thumbnail(noPhoto).build());
		
		serialRepository.save(SerialElement.builder().elementType(SerialElementType.EPISODE).parent(mJakMiloscSeason1)
				.title("Odcinek 2 - Rozterki ").description("Hanka ma probem z kartonami. Czy Marek jej pomoże? ").producer(user).active(true).thumbnail(noPhoto).build());
		
		serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("Pierwsza Miłość")
				.description("Pierwsza miłość jest serialem produkcji polskiej. Akcja skupia się na Wrocławiu gdzie zamieszkuje Marysia, świeżo upieczona studentka medycyny.").producer(user).active(true)
				.actors(actorList).genres(Arrays.asList(dramat, przygodowy, horror)).thumbnail(pierwszaMilosc).build());

		serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("Na wspólnej")
				.description("Losy siedmiu byłych wychowanków domu dziecka oraz ich najbliższych. ").producer(user).active(true)
				.actors(actorList).genres(Arrays.asList(thiller, przygodowy)).thumbnail(naWspolnej).build());

		serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("Przyjaciółki")
				.description("Polski serial telewizyjny. Akcja rozgrywa się i tak dalej").producer(user).active(true)
				.actors(actorList).genres(Arrays.asList(thiller, horror)).thumbnail(przyjaciolki).build());

		serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("Orphan Black")
				.description(
						"Uliczna oszustka, Sarah, będąc świadkiem samobójstwa dziewczyny, która wygląda tak jak ona, postanawia przejąć jej tożsamość, przez co trafia w sam środek śmiertelnego spisku.")
				.producer(user).active(true).actors(actorList).genres(Arrays.asList(dramat, sciFi)).thumbnail(noPhoto)
				.build());

		
	}

	void setContractTemplate() throws IOException {
		File file = new File(pathToContractTemplate + "/contract-template-1.txt");
		if (file.exists())
			return;

		File source = new File("src/main/resources/static/contract-templates/contract-template-1.txt");
		// File destination = new File(pathToContractTemplate + File.separator);

		Files.copy(source.toPath(), file.toPath());
	}

	



	void addPlaylist() {
		
		User user = userRepo.findByLogin("producer");
		
		Genre komedia = genreRepository.save(Genre.builder().name("komedia").build());
		Genre historyczny = genreRepository.save(Genre.builder().name("historyczny").build());
		Genre kulinarny = genreRepository.save(Genre.builder().name("kulinarny").build());
		
		Picture noPhoto = pictureRepo.save(Picture.builder().name("no_photo.jpg").build());
		
		List<Actor> actorList = Arrays.asList(
				actorRepo.save(Actor.builder().name("Aktor1").surname("A").thumbnail(noPhoto).build()),
				actorRepo.save(Actor.builder().name("Aktor2").surname("B").thumbnail(noPhoto).build()),
				actorRepo.save(Actor.builder().name("Aktor3").surname("C").thumbnail(noPhoto).build()),
				actorRepo.save(Actor.builder().name("Aktor4").surname("D").thumbnail(noPhoto).build()));
		
		SerialElement serial1 = serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("Ewa gotuje")
				.description("Polski serial telewizyjny. Akcja rozgrywa się i tak dalej").producer(user).active(true)
				.actors(actorList).genres(Arrays.asList(kulinarny)).thumbnail(noPhoto).build());
		
		SerialElement serial2 = serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("Jak poznałem Waszą matkę")
				.description("Polski serial telewizyjny. Akcja rozgrywa się i tak dalej").producer(user).active(true)
				.actors(actorList).genres(Arrays.asList(komedia)).thumbnail(noPhoto).build());
			
		SerialElement serial3 = serialRepository.save(SerialElement.builder().elementType(SerialElementType.SERIAL).title("Gra o tron")
				.description("Polski serial telewizyjny. Akcja rozgrywa się i tak dalej").producer(user).active(true)
				.actors(actorList).genres(Arrays.asList(historyczny)).thumbnail(noPhoto).build());
				
		
	

		PlaylistElement element1 = PlaylistElement.builder().serialElement(serial1).build();
		PlaylistElement element2 = PlaylistElement.builder().serialElement(serial2).build();
		PlaylistElement element3 = PlaylistElement.builder().serialElement(serial3).build();
		
		PlaylistElement element4 = PlaylistElement.builder().serialElement(serial1).build();
		PlaylistElement element5 = PlaylistElement.builder().serialElement(serial2).build();
		PlaylistElement element6 = PlaylistElement.builder().serialElement(serial3).build();
		
		
		element1.setPrevious(null);
		element1.setNext(element2);
		element2.setPrevious(element1);
		element2.setNext(element3);
		element3.setPrevious(element2);
		element3.setNext(null);
		
		element4.setPrevious(null);
		element4.setNext(element5);
		element5.setPrevious(element4);
		element5.setNext(element6);
		element6.setPrevious(element5);
		element6.setNext(null);
		
		
		playlistElementRepo.save(element1);
		playlistElementRepo.save(element2);
		playlistElementRepo.save(element3);
		playlistElementRepo.save(element4);
		playlistElementRepo.save(element5);
		playlistElementRepo.save(element6);
		
		List<PlaylistElement> elements = new LinkedList<>();
		List<PlaylistElement> elements2 = new LinkedList<>();
		
		elements.add(element1);
		elements.add(element2);
		elements.add(element3);
		
		elements2.add(element5);
		elements2.add(element6);
		elements2.add(element4);

		Playlist playlist =  Playlist.builder().name("Pierwsza playlista").user(userRepo.findByLogin("viewer")).elements(elements).build();
		Playlist playlist2 =  Playlist.builder().name("Druga playlista").user(userRepo.findByLogin("viewer")).elements(elements2).build();
		Playlist playlist3 =  Playlist.builder().name("Trzecia playlista").user(userRepo.findByLogin("viewer")).build();
		
		playlistRepo.save(playlist);
		playlistRepo.save(playlist2);
		playlistRepo.save(playlist3);
	}

	
	@PostConstruct
	void initApplication() throws IOException {
		addSystemUsers();
		addSerials();
		setContractTemplate();
		addPlaylist();
	}
}

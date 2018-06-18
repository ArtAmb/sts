package psk.isf.sts.service.reminder;

import java.sql.Date;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import psk.isf.sts.entity.myserial.MySerial;
import psk.isf.sts.entity.myserial.MySerialConfig;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.serial.SerialElement;
import psk.isf.sts.repository.myserial.MySerialConfigRepository;
import psk.isf.sts.repository.myserial.MySerialRepository;
import psk.isf.sts.service.authorization.UserService;
import psk.isf.sts.service.email.EmailService;
import psk.isf.sts.service.email.dto.EmailDTO;
import psk.isf.sts.service.reminder.dto.Reminder;
import psk.isf.sts.service.reminder.dto.UserWithReminders;
import psk.isf.sts.service.reminder.dto.UserWithSerialsDTO;
import psk.isf.sts.service.series.SerialService;

@Service
public class ReminderService {

	@Autowired
	private MySerialRepository mySerialRepo;

	@Autowired
	private MySerialConfigRepository mySerialConfigRepo;

	@Autowired
	private SerialService serialService;

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	@Transactional
	public void startCheckAndCreateRemindersThenSendNotifications() {
		Collection<UserWithReminders> usersWithReminders = findAllUsersWithReminders();

		for (UserWithReminders userWithReminders : usersWithReminders) {
			remaindAndSendNotifications(userWithReminders);
		}
	}

	private Collection<UserWithReminders> findAllUsersWithReminders() {

		Collection<User> allUsers = userService.findAll();

		Collection<UserWithSerialsDTO> userWithSerialsDTOs = allUsers.stream().map(this::createUserWithSerials)
				.collect(Collectors.toList());
		Collection<UserWithReminders> userWithReminders = createReminders(userWithSerialsDTOs);

		return userWithReminders;
	}

	private UserWithSerialsDTO createUserWithSerials(User user) {
		Collection<MySerialConfig> mySerialsConfig = mySerialConfigRepo.findByUserAndSendNotificationsTrue(user);
		Collection<MySerial> mySerials = mySerialsConfig.stream().map(MySerialConfig::getSerial)
				.collect(Collectors.toList());

		return UserWithSerialsDTO.builder().user(user).mySerials(mySerials).build();
	}

	private Collection<UserWithReminders> createReminders(Collection<UserWithSerialsDTO> userWithSerialsDTOs) {
		Collection<UserWithReminders> userWithReminders = new LinkedList<>();
		Date today = new Date(System.currentTimeMillis());

		for (UserWithSerialsDTO userWithSerialDTO : userWithSerialsDTOs) {
			User user = userWithSerialDTO.getUser();

			Collection<Reminder> reminders = userWithSerialDTO.getMySerials().stream().map(s -> {
				SerialElement lastEpisode = serialService.findNextEpisodeDate(s.getSerial());

				if (lastEpisode == null)
					return null;

				if (lastEpisode.getStartDate() == null)
					return null;

				if (!today.toString().equals(lastEpisode.getStartDate().toString()))
					return null;

				return Reminder.builder().user(user).serialTitle(s.getSerial().getTitle())
						.episodeTitle(lastEpisode.getTitle()).when(lastEpisode.getStartDate())
						.episodeNumber(lastEpisode.getOrdinalNumber())
						.seasonNumber(lastEpisode.getParent().getOrdinalNumber()).message(lastEpisode.getDescription())
						.build();
			}).filter(r -> r != null).collect(Collectors.toList());

			userWithReminders.add(UserWithReminders.builder().user(user).reminders(reminders).build());
		}

		return userWithReminders;
	}

	private void remaindAndSendNotifications(UserWithReminders userWithReminders) {
		User user = userWithReminders.getUser();

		if (user.getEmail() == null || userWithReminders.getReminders().isEmpty())
			return;

		StringBuffer buf = new StringBuffer();
		buf.append("Hej! Dzisiaj nowe odcinki seriali: \n\n");
		for (Reminder reminder : userWithReminders.getReminders()) {
			buf.append(String.format("-> '%s', Odcinek: s%se%s %s\n\nOpis:\n%s\n\n", reminder.getSerialTitle(),
					reminder.getSeasonNumber(), reminder.getEpisodeNumber(), reminder.getEpisodeTitle(),
					reminder.getMessage()));
		}

		EmailDTO emailDTO = EmailDTO.builder().to(user.getEmail()).subject("STS Przypomienia o premierach!")
				.content(buf.toString()).build();

		emailService.sendSimpleMessage(emailDTO);
	}

	public static void main(String[] args) {
		System.out.println("2018-06-16".equals(new Date(System.currentTimeMillis()).toString()));
	}

}

package psk.isf.sts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import psk.isf.sts.service.reminder.ReminderService;

@SpringBootApplication
@EnableScheduling
public class StsApplication {

	@Autowired
	private ReminderService reminderService;

	public static void main(String[] args) {
		SpringApplication.run(StsApplication.class, args);
	}

	// 0 0 0 * * ? -> everyDay at midnight
	// * * * ? * * -> everysec
	@Scheduled(cron = "* * * ? * *")
	public void startCheckAndCreateRemindersThenSendNotifications() {
		// reminderService.startCheckAndCreateRemindersThenSendNotifications();
		System.out.println("DZIALM i minela 1 sekunda");
	}
}

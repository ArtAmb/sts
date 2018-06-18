package psk.isf.sts;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import psk.isf.sts.service.cache.CacheManager;
import psk.isf.sts.service.reminder.ReminderService;

@SpringBootApplication
@EnableScheduling
public class StsApplication {

	@Autowired
	private ReminderService reminderService;

	@Autowired
	private CacheManager cacheManager;

	public static void main(String[] args) {
		SpringApplication.run(StsApplication.class, args);
	}

	@Value("${reminder.enabled}")
	private Boolean isReminderEnabled;

	@Scheduled(cron = "${reminder.cron.expression}")
	public void startCheckAndCreateRemindersThenSendNotifications() {
		if (isReminderEnabled) {
			System.out.println("Zamiarzm wyslac przypomienia");
			reminderService.startCheckAndCreateRemindersThenSendNotifications();
		}
	}

	@PostConstruct
	public void initCacheManager() {
		cacheManager.startCacheControlling();
	}
}

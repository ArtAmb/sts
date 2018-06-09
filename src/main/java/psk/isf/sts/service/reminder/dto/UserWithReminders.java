package psk.isf.sts.service.reminder.dto;

import java.util.Collection;

import lombok.Builder;
import lombok.Data;
import psk.isf.sts.entity.registration.User;

@Data
@Builder
public class UserWithReminders {

	private User user;
	private Collection<Reminder> reminders;

}

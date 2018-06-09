package psk.isf.sts.service.reminder.dto;

import java.sql.Date;

import lombok.Builder;
import lombok.Data;
import psk.isf.sts.entity.registration.User;

@Data
@Builder
public class Reminder {

	private User user;
	private String message;
	private Date when;
	private String episodeTitle;
	private String serialTitle;
	private Long episodeNumber;
	private Long seasonNumber;

}

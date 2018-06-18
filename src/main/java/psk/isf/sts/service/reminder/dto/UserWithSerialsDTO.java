package psk.isf.sts.service.reminder.dto;

import java.util.Collection;

import lombok.Builder;
import lombok.Data;
import psk.isf.sts.entity.myserial.MySerial;
import psk.isf.sts.entity.registration.User;

@Data
@Builder
public class UserWithSerialsDTO {

	private User user;
	private Collection<MySerial> mySerials;

}

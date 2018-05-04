package psk.isf.sts.service.authorization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.registration.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProducerDTO {
	private User user;
	private String rawPassword;

	public User toUser() {
		return user;
	}
}

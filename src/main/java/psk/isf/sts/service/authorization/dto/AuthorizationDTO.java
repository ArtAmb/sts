package psk.isf.sts.service.authorization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationDTO {
	private String email;
	private String login;
	private String password;
	private String repeatedPassword;
}

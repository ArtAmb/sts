package psk.isf.sts.service.authorization.dto;

import lombok.Builder;
import lombok.Data;
import psk.isf.sts.entity.UserType;
import psk.isf.sts.entity.registration.Role;

@Data
@Builder
public class UserDTO {

	private boolean real;
	private UserType userType;

	private Role role;

}

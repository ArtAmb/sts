package psk.isf.sts.entity.registration;

import java.util.Collection;
import java.util.LinkedList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true, nullable = false)
	private String login;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, unique = true)
	private String email;

	@ManyToMany
	@Builder.Default
	private Collection<Role> roles = new LinkedList<>();
	@Builder.Default
	private boolean active = true;

	@Builder.Default
	private boolean firstTimeLogIn = false;

	@Builder.Default
	private boolean vip = false;

	public boolean hasRole(String roleName) {
		for (Role role : roles) {
			if (role.equals(roleName))
				return true;
		}
		return false;
	}
}
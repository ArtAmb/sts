package psk.isf.sts.entity.registration;

import java.util.Collection;
import java.util.LinkedList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import psk.isf.sts.entity.Contract;
import psk.isf.sts.entity.Gallery;
import psk.isf.sts.entity.Picture;
import psk.isf.sts.entity.SerialElement;
import psk.isf.sts.entity.UserType;

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

	@Column(nullable = true, unique = true)
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

	@Enumerated(EnumType.STRING)
	private UserType userType;

	@ManyToMany
	private Collection<Gallery> galleries;
	@ManyToOne
	private Picture thumbnail;

	private String companyName;
	private String nip;
	private String phoneNumber;
	private String address;

	@ManyToOne
	private Contract contract;

	@Builder.Default
	private boolean disabled = false;

	@Builder.Default
	private boolean real = true;

	@ManyToOne
	private SerialElement currentlyWatchedEpisode;

	public boolean hasRole(String roleName) {
		for (Role role : roles) {
			if (role.equals(roleName))
				return true;
		}
		return false;
	}
}
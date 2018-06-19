package psk.isf.sts.entity.registration;

import java.util.Collection;
import java.util.LinkedList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import psk.isf.sts.entity.contract.Contract;
import psk.isf.sts.entity.picture.Picture;
import psk.isf.sts.entity.serial.SerialElement;

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

	private String displayLogin;

	@Column(nullable = false)
	private String password;

	@Column(nullable = true, unique = true)
	private String email;

	@ManyToMany(fetch = FetchType.EAGER)
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

	// @ManyToMany(fetch = FetchType.EAGER)
	// private Collection<Gallery> galleries;

	@ManyToOne(cascade = { CascadeType.ALL })
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
	
	private Long watchingEpisodeStartTimeInMs;

	public boolean hasRole(String roleName) {
		for (Role role : roles) {
			if (role.getName().equals(roleName))
				return true;
		}
		return false;
	}

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private UserSourceSystem sourceSystem = UserSourceSystem.STS;

	private String extId;
}
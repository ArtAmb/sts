package psk.isf.sts.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Builder;
import lombok.Data;
import psk.isf.sts.entity.registration.User;

@Entity
@Data
@Builder
public class MySerial {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private User user;

	@ManyToOne
	private SerialElement serial;

	private boolean trace;
	private boolean sendNotifications;
	private boolean showDescriptions;

}

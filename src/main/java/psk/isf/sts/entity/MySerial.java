package psk.isf.sts.entity;

import java.sql.Timestamp;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.SerialElement.SerialElementBuilder;
import psk.isf.sts.entity.registration.User;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

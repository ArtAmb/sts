package psk.isf.sts.entity.myserial;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.registration.User;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MySerialConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private User user;

	@ManyToOne(cascade = CascadeType.ALL)
	private MySerial serial;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "config_id")
	private Collection<MySerial> mySerialElements;

	private boolean trace;
	private boolean sendNotifications;
	private boolean showDescriptions;

}

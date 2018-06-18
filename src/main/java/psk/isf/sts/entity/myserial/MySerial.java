package psk.isf.sts.entity.myserial;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.serial.SerialElement;

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

	@ManyToOne(fetch = FetchType.EAGER)
	private SerialElement serial;

	@ManyToOne
	private MySerialConfig config;

	private boolean watched;
}

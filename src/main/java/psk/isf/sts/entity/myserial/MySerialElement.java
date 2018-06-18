package psk.isf.sts.entity.myserial;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import psk.isf.sts.entity.serial.SerialElement;

@Entity
@Data
public class MySerialElement {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private SerialElement serialElement;

	@Enumerated(EnumType.STRING)
	private WatchingState watchingState;

}

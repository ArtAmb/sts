package psk.isf.sts.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import psk.isf.sts.entity.registration.User;
import psk.isf.sts.entity.serial.SerialElement;

@Entity
@Data
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private String content;
	private Timestamp date;

	@ManyToOne
	@JoinColumn(name = "serialElement", nullable = false)
	private SerialElement serialElement;

	private boolean accepted;

	private String rejectionCause;

	@ManyToOne
	private User lastEditUser;

}

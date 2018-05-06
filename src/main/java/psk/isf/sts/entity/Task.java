package psk.isf.sts.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.registration.User;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private User owner;

	@ManyToOne
	private User producer;

	@ManyToOne
	private Contract contract;

	@Enumerated(EnumType.STRING)
	private TaskType type;

	@Enumerated(EnumType.STRING)
	private TaskState state;

	private Timestamp date;

	private String comments;
}

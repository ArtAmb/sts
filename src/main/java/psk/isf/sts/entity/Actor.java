package psk.isf.sts.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.picture.Picture;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Actor {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;
	private String surname;
	
	@ManyToOne
	private Picture thumbnail;

}

package psk.isf.sts.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class PlaylistElement {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private SerialElement serialElement;

	@ManyToOne
	private PlaylistElement next;

	@ManyToOne
	private PlaylistElement previous;

	@ManyToOne(cascade = { CascadeType.ALL })
	private Playlist playlist;

}

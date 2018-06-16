package psk.isf.sts.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistElement {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private SerialElement serialElement;

	@ManyToOne(cascade = { CascadeType.ALL })
	private PlaylistElement next;

	@ManyToOne
	private PlaylistElement previous;

	@ManyToOne(cascade = { CascadeType.ALL })
	private Playlist playlist;

	public SimplePlaylistElement toSimplePlaylistElement() {
		return SimplePlaylistElement.builder().id(id).serialElement(serialElement).playlist(playlist).build();
	}
}

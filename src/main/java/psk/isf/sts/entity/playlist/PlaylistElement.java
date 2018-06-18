package psk.isf.sts.entity.playlist;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.serial.SerialElement;
import psk.isf.sts.entity.serial.SimplePlaylistElement;

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

	@ManyToOne
	private PlaylistElement next;

	@ManyToOne
	private PlaylistElement previous;

	@ManyToOne
	private Playlist playlist;

	public SimplePlaylistElement toSimplePlaylistElement() {
		return SimplePlaylistElement.builder().id(id).serialElement(serialElement).playlist(playlist).build();
	}
}

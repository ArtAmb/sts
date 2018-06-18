package psk.isf.sts.entity.serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.playlist.Playlist;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimplePlaylistElement {

	private Long id;
	private SerialElement serialElement;
	private Playlist playlist;
	
}

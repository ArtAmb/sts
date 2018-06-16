package psk.isf.sts.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimplePlaylistElement {

	private Long id;
	private SerialElement serialElement;
	private Playlist playlist;
	
}

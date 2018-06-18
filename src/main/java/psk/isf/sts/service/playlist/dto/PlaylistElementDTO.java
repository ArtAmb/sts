package psk.isf.sts.service.playlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.Playlist;
import psk.isf.sts.entity.PlaylistElement;
import psk.isf.sts.entity.SerialElement;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistElementDTO {

	private SerialElement serialElement;
	private PlaylistElement next;
	private PlaylistElement previous;
	private Playlist playlist;
	
}

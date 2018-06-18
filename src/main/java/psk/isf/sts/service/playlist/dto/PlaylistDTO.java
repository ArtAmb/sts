package psk.isf.sts.service.playlist.dto;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.playlist.PlaylistElement;
import psk.isf.sts.entity.registration.User;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistDTO {

	private String name;
	private User user;
	private Collection <PlaylistElement> elements;
	
}
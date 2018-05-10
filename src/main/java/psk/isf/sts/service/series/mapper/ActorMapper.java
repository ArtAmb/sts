package psk.isf.sts.service.series.mapper;

import psk.isf.sts.entity.Actor;
import psk.isf.sts.service.series.dto.ActorDTO;

public class ActorMapper {

	public static ActorDTO map(Actor a) {
		return ActorDTO.builder()
				.id(a.getId())
				.name(a.getName())
				.surname(a.getSurname())
				.thumbnailUrl(a.getThumbnail() != null ? a.getThumbnail().toURL() : null)
				.build();
	}
}

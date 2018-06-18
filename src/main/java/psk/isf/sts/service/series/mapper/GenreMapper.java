package psk.isf.sts.service.series.mapper;

import psk.isf.sts.entity.serial.Genre;
import psk.isf.sts.service.series.dto.GenreDTO;

public class GenreMapper {

	public static GenreDTO map(Genre g) {
		return GenreDTO.builder()
				.id(g.getId())
				.name(g.getName())
				.build();
	}
}

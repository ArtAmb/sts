package psk.isf.sts.entity.serial;

import java.sql.Date;
import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.Actor;
import psk.isf.sts.entity.Comment;
import psk.isf.sts.entity.picture.Gallery;
import psk.isf.sts.entity.picture.ViewPicture;
import psk.isf.sts.entity.registration.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleSerialElement {

	private Long id;
	private String title;
	private String description;
	private State state;
	private SerialElementType elementType;
	private float rating;
	private Collection<Actor> actors;
	private SerialElement parent;
	private Collection<Genre> genres;
	private Collection<Comment> comments;
	private Date startDate;
	private Long durationInSec;
	private Long daysToNextEpisode;
	private boolean active;
	private String linkToWatch;
	private User producer;
	private Gallery gallery;
	private ViewPicture thumbnail;
	private Long ordinalNumber;
	// private Collection<SerialElement> elements;
	// private SerialElement parent;

}

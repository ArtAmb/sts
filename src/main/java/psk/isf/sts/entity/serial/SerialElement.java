package psk.isf.sts.entity.serial;

import java.sql.Date;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import psk.isf.sts.entity.Actor;
import psk.isf.sts.entity.Comment;
import psk.isf.sts.entity.picture.Gallery;
import psk.isf.sts.entity.picture.Picture;
import psk.isf.sts.entity.registration.User;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SerialElement {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(nullable = false)
	private String title;
	private String description;
	private Long ordinalNumber;

	@Enumerated(EnumType.STRING)
	private State state;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SerialElementType elementType;

	private float rating;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Collection<SerialElement> elements;

	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	private Collection<Actor> actors;

	@ManyToOne
	private SerialElement parent;

	@ManyToMany
	private Collection<Genre> genres;

	@OneToMany
	@JoinColumn(name = "serialElement")
	private Collection<Comment> comments;

	private Date startDate;
	@Builder.Default
	private Long durationInSec = 0l;

	private Long daysToNextEpisode;
	@Builder.Default
	private boolean active = true;
	private String linkToWatch;

	@ManyToOne
	private User producer;

	@ManyToOne
	private Gallery gallery;

	@ManyToOne
	private Picture thumbnail;

	public SimpleSerialElement toSimpleSerialElement() {
		return SimpleSerialElement.builder().id(id).title(title).description(description).state(state)
				.elementType(elementType).rating(rating).elementType(elementType).actors(actors).parent(parent)
				.genres(genres).comments(comments).startDate(startDate).durationInSec(durationInSec)
				.daysToNextEpisode(daysToNextEpisode).active(active).linkToWatch(linkToWatch).producer(producer)
				.gallery(gallery).thumbnail(thumbnail != null ? thumbnail.toViewPicture() : null)
				.ordinalNumber(ordinalNumber).build();
	}
}
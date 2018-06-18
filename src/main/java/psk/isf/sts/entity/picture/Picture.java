package psk.isf.sts.entity.picture;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Picture {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;
	private String path;
	@Builder.Default
	private boolean fromOurServer = true;

	public String toURL() {
		if(fromOurServer)
			return "/images/" + name;
		else 
			return path;
	}
	
	public ViewPicture toViewPicture() {
		return ViewPicture.builder()
				.id(id)
				.name(name)
				.url(toURL())
				.build();
	}
}

package psk.isf.sts.service.cache;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CacheObject<T> {
	private T object;
	private Long duration;
}

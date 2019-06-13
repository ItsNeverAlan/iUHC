package spg.lgdev.uhc.player.rating;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RatingHistory {

	private final UUID uuid;
	private final int ratingChanged;
	private final RatingChangeReason reason;
	private final long changedTime = System.currentTimeMillis();

}

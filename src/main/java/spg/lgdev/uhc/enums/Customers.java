package spg.lgdev.uhc.enums;

import lombok.Getter;
import spg.lgdev.uhc.config.CachedConfig;

public enum Customers {

	MitwOffical, WambaJamba, EzVape, NONE;

	@Getter
	private static final Customers currentCustomer = get();

	private static Customers get() {
		try {
			return valueOf(CachedConfig.getHWID());
		} catch (final Exception e) {
			return Customers.NONE;
		}
	}

}

package spg.lgdev.uhc.enums;

public enum ServerVersion {

	v1_7_R4(0),
	v1_8_R1(1),
	v1_8_R3(2),
	v1_9_R1(3),
	v1_9_R2(4),
	v1_10_R1(5),
	v1_11_R1(6),
	v1_12_R1(7);
	private static ServerVersion currentVer;
	private int loc;

	private ServerVersion(final int loc) {
		this.loc = loc;
	}

	public static void set(final ServerVersion b) {
		currentVer = b;
	}

	public static boolean is(final ServerVersion b) {
		return currentVer.loc == b.loc;
	}

	public static boolean isUnder(final ServerVersion first, final ServerVersion second) {
		return first.loc < second.loc;
	}

	public static boolean isHigher(final ServerVersion first, final ServerVersion second) {
		return first.loc > second.loc;
	}

	public static ServerVersion get() {
		return currentVer;
	}

	public static String getNMSPackage() {
		return currentVer.name();
	}

	public static boolean is1_7() {
		return is(ServerVersion.v1_7_R4);
	}
}


package spg.lgdev.uhc.handler;

public class Permissions {

    public static final String PREFIX = "ultimateuhc.";
    public static final String ADMIN = PREFIX + "admin";
    public static final String FREEZE = PREFIX + "freeze";
    public static final String FREEZE_BYPASS = FREEZE + ".bypass";
    public static final String SPECTATORCHAT = PREFIX + "spectatorchat";
    public static final String HELPOP = PREFIX + "helpop";
    public static final String HELPOP_COOLDOWN_BYPASS = HELPOP + ".bypass";
    public static final String ADMINCHAT = PREFIX + "adminchat";
    public static final String MUTECHAT = PREFIX + "mutechat";
    public static final String RESPAWN = PREFIX + "respawn";
    public static final String TOGGLE_STAFF = PREFIX + "togglestaff";
    public static final String STATS_CHANGER = PREFIX + "statschanger";
    public static final String TEAMNAME_COLOR_ABLE = PREFIX + "namecolor";
    public static final String CHANGE_TEAM_NAME = PREFIX + "teamname";
    public static final String CHAT = PREFIX + "chat";
    public static final String COLOR = CHAT + ".color";
    public static final String MUTECHAT_BYPASS = CHAT + ".mutebypass";
    public static final String BLOCKED_CMD_BYPASS = PREFIX + "blockedcmd.bypass";
    public static final String WHITELIST_BYPASS = PREFIX + "whitelist.bypass";
    public static final String FULL_BYPASS = PREFIX + "fullbypass";
    public static final String SPECTATE = PREFIX + "spectate.";
    public static final String DEATHKICK_BYPASS = PREFIX + "deathkick.bypass";
    public static final String LATE_SCATTER = PREFIX + "latescatter";

    public static String getSpectate(int borderRadius) {
        return SPECTATE + borderRadius;
    }
}

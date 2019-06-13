package spg.lgdev.uhc.enums;

public enum ScoreboardStyle {

    LOAD(1),
    LOBBY(2),
    TELEPORT(3),
    SOLO(5),
    TEAM(4),
    SOLOMATCH(7),
    TEAMMATCH(6),
    FINISH(8);

    private int status;

    ScoreboardStyle(int type) {
        this.status = type;
    }

    public int getID() {
        return status;
    }

}

package psk.isf.sts.entity;

public enum State {
	FINISHED("Zakonczony"), RUNNING("W trakcie"), COMING("Nadchodzacy");
	
	private final String displayName;

    State(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

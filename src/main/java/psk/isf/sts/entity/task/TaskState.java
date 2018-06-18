package psk.isf.sts.entity.task;

public enum TaskState {
	NEW("Nowe"), IN_PROGRESS("W trakcie"), CLOSED("Zamkniete");

	private String displayName;

	private TaskState(String displayName) {
		this.displayName = displayName;
	}

	public String toDisplayName() {
		return displayName;
	}

}

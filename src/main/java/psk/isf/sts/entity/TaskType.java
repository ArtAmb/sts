package psk.isf.sts.entity;

public enum TaskType {
	ACCEPT_CONTRACT("Akceptacja umowy"), ERROR_SERVICE("Obsługa błędów");
	private String displayName;

	private TaskType(String displayName) {
		this.displayName = displayName;
	}

	public String toDisplayName() {
		return displayName;
	}
}

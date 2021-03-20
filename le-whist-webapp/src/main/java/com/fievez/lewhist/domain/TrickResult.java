package com.fievez.lewhist.domain;

public class TrickResult {

	private final int value;
	private final TrickResultType type;

	private TrickResult(int value, TrickResultType type) {
		this.value = value;
		this.type = type;
	}

	public static TrickResult win(int value) {
		return new TrickResult(value, TrickResultType.TAKING);
	}

	public static TrickResult distribution(int value) {
		return new TrickResult(value, TrickResultType.DISTRIBUTING);
	}

	public int getWinner() {
		return value;
	}

	public boolean isTakingType() {
		return type.equals(TrickResultType.TAKING);
	}

	public boolean isDistributingType() {
		return type.equals(TrickResultType.DISTRIBUTING);
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		TrickResult that = (TrickResult) o;

		if(value != that.value) return false;
		return type == that.type;
	}

	@Override
	public int hashCode() {
		int result = value;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "TrickResult{" +
				"value=" + value +
				", type=" + type +
				'}';
	}

	public enum TrickResultType {

		DISTRIBUTING,
		TAKING

	}

}

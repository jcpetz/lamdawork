package com.customcollector;

public class StringCombiner {
	private final StringBuilder sb;
	private final String delim;
	private final String prefix;
	private final String suffix;

	public StringCombiner(final String delim, final String prefix, final String suffix) {
		this.delim = delim;
		this.prefix = prefix;
		this.suffix = suffix;
		sb = new StringBuilder();
	}

	public StringCombiner add(String elem) {
		if (sb.length() == 0) {
			sb.append(prefix);
		} else {
			sb.append(delim);
		}
		sb.append(elem);
		return this;
	}

	public StringCombiner merge(StringCombiner other) {
		sb.append(other.sb);
		return this;
	}

	public String getDelim() {
		return delim;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}
	
	@Override
	public String toString() {
		return sb.toString() + suffix;
	}
	

}

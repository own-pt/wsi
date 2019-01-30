package no.uio.ifi.pgstore.gui;

import java.util.Comparator;

public class CountedHost implements Comparator<CountedHost> {

	private final int count;
	private final String host;

	public CountedHost(int count, String host) {
		super();
		this.count = count;
		this.host = host;
	}

	@Override
	public int compare(CountedHost o1, CountedHost o2) {
		return Double.compare(o2.getCount(), o1.getCount());
	}

	public int getCount() {
		return count;
	}

	public String getHost() {
		return host;
	}

}

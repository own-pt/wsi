package no.uio.ifi.wsi.sparql;

import lombok.Value;

@Value
public class SearchInfo {

	private long time;
	private int graphs;
	private int matches;

	public String toString(int i) {
		double estimatedTime = time;
		estimatedTime = estimatedTime / 1000.0;
		estimatedTime = Math.round(estimatedTime * 10.0) / 10.0;

		double percent = 100.0 * ((double) graphs) / ((double) i);
		percent = Math.round(percent * 100.0) / 100.0;
		return matches + " total matches in " + graphs + " distinct graphs ("
				+ percent + "% of all " + i + " graphs) (" + estimatedTime
				+ " seconds)";
	}
}

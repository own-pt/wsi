package no.uio.ifi.wsi.search.language;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.Data;

@Data
public class WeightedString implements Comparator<WeightedString> {

	public static int level(List<WeightedString> value) {
		int level = 110;
		for (WeightedString w : value) {
			int wx = 0;
			if (w.getLevel() == 0)
				wx = level(w.getValues());
			else
				wx = w.getLevel();
			if (wx < level)
				level = wx;
		}
		return level;
	}

	public static String toUnionString(List<WeightedString> value) {
		String first = value.get(0).toSparqlString();
		if (value.size() == 1) {
			return first;
		}
		return "{ " + first + " } UNION { " + toUnionString(value.subList(1, value.size())) + " }";
	}

	public static long weight(List<WeightedString> value) {
		long weight = 0;
		for (WeightedString w : value) {
			double wx = 0;
			if (w.getValues() != null)
				wx = weight(w.getValues());
			else
				wx = w.getWeight();
			weight += wx;
		}
		return weight;
	}

	private int level;
	private String value;
	private List<WeightedString> values;

	private long weight;

	public WeightedString(int level, String value, long weight) {
		super();
		this.level = level;
		this.value = value;
		this.weight = weight;
	}

	public WeightedString(List<WeightedString> values_) {
		super();
		values = values_;
		weight = weight(values);
	}

	@Override
	public int compare(WeightedString o1, WeightedString o2) {
		int compare = Integer.compare(o1.getLevel(), o2.getLevel());
		if (compare != 0)
			return compare;
		return Long.compare(o1.getWeight(), o2.getWeight());
	}

	public String toSparqlString() {
		if (value != null)
			return value;
		Collections.sort(values, values.get(0));
		System.out.println(values);
		StringBuilder b = new StringBuilder();

		for (WeightedString s : values) {
			String x = s.toSparqlString();
			b.append(x);
			if (x.endsWith("}"))
				b.append("\n");
			else {
				if (!x.endsWith(" .\n"))
					b.append(" .\n");
			}
		}
		return b.toString();
	}
}

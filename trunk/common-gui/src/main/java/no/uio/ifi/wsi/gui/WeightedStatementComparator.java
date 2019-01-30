package no.uio.ifi.wsi.gui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;

public class WeightedStatementComparator implements Comparator<WeightedStatement> {

	private final List<String> subject;

	public WeightedStatementComparator(List<WeightedStatement> ws) {
		Collections.sort(ws, ws.get(0));

		subject = Lists.newArrayList();
		for (WeightedStatement w : ws) {
			if (subject.contains(w.getSubject()))
				continue;
			subject.add(w.getSubject());
		}

	}

	@Override
	public int compare(WeightedStatement o1, WeightedStatement o2) {
		Integer i1 = subject.indexOf(o1.getSubject());
		Integer i2 = subject.indexOf(o2.getSubject());
		if (i1.intValue() == i2.intValue())
			return o1.getWeight().compareTo(o2.getWeight());
		return i1.compareTo(i2);
	}
}

package no.uio.ifi.wsi.gui;

import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;

public class FormatSorter implements Comparator<String> {
	private List<String> order;

	public FormatSorter() {
		order = Lists.newArrayList();
		order.add("mrs");
		order.add("eds");
		order.add("dm");
	}

	@Override
	public int compare(String o1, String o2) {
		int pos1 = order.indexOf(o1);
		int pos2 = order.indexOf(o2);
		if (pos1 == -1) {
			if (pos2 == -1) {
				return o1.compareTo(o2);
			} else {
				return 1;
			}
		} else {
			if (pos2 == -1) {
				return -1;
			} else {
				return (pos1 < pos2) ? -1 : ((pos1 == pos2) ? 0 : 1);
			}
		}
	}

}

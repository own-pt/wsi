package no.uio.ifi.wsi.gui;

import java.util.Comparator;

public class WeightedStatement implements Comparator<WeightedStatement> {

	public static final String[] PREDICATES = new String[] { "name", "label", "form", "lemma", "pos" };

	private static int index(String predicate2) {

		int i = 0;
		for (String s : PREDICATES) {
			if (s.equals(predicate2))
				return i;
			i++;
		}
		return i;
	}

	private final String predicate;
	private final String statement;
	private final String subject;

	private final Long weight;

	public WeightedStatement(String subject, String predicate, String statement, long weight) {
		super();
		this.subject = subject;
		this.predicate = predicate;
		this.statement = statement;
		this.weight = weight;
	}

	@Override
	public int compare(WeightedStatement o1, WeightedStatement o2) {
		int p1 = index(o1.getPredicate());
		int p2 = index(o1.getPredicate());
		if (p1 == p2)
			return o1.getWeight().compareTo(o2.getWeight());
		return Double.compare(p1, p2);
	}

	public String getPredicate() {
		return predicate;
	}

	public String getStatement() {
		return statement;
	}

	public String getSubject() {
		return subject;
	}

	public Long getWeight() {
		return weight;
	}

}

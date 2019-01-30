package no.uio.ifi.wsi.search.language;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class OperatorParse extends Parse {

	private Parse left;
	private char operator;
	private Parse right;

	public OperatorParse(char operator_) {
		operator = operator_;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

}

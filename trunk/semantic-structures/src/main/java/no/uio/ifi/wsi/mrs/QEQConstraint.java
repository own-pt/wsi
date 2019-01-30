package no.uio.ifi.wsi.mrs;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;

@Data
public class QEQConstraint implements Constraint {

	private final Variable arg1;
	private final Variable arg2;

	public QEQConstraint(Variable arg1, Variable arg2) {
		super();
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	@Override
	public List<Variable> argumenrs() {
		List<Variable> arg = Lists.newArrayList();
		arg.add(arg1);
		arg.add(arg2);
		return arg;
	}

	@Override
	public String name() {
		return "qeq";
	}

}

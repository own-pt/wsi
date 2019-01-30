/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.uio.ifi.wsi.sparql;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

/**
 * 
 * @author milen
 */
public class ResultInstanceSorter implements Comparator<ResultInstance> {

	private final List<String> field;

	public ResultInstanceSorter(Result res) {
		field = Lists.newArrayList();
		List<String> vars = Lists.newArrayList();
		List<Integer> nums = Lists.newArrayList();

		for (ResultInstance i : res.getInstances()) {
			for (String s : i.getNames()) {
				Integer intV = Ints.tryParse(s);
				if (intV == null) {
					if (!vars.contains(s)) {
						vars.add(s);
					}
				} else {
					if (!nums.contains(intV)) {
						nums.add(intV);
					}
				}
			}
		}
		Collections.sort(nums);
		Collections.sort(vars);
		field.addAll(vars);
		for (Integer i : nums) {
			field.add(i.toString());
		}
	}

	@Override
	public int compare(ResultInstance o1, ResultInstance o2) {
		for (String s : field) {
			String v1 = o1.value(s);
			String v2 = o2.value(s);
			if (v1 == null) {
				if (v2 == null)
					continue;
				else
					return -1;
			}
			if (v2 == null)
				return 1;
			return Double.compare(Double.parseDouble(v1), Double.parseDouble(v2));
		}
		return 0;
	}

}

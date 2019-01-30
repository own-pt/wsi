package no.uio.ifi.wsi.search.language.impl.generic;

import java.util.List;
import java.util.Map;

import no.uio.ifi.wsi.search.language.Parse;
import no.uio.ifi.wsi.search.language.RoleParse;
import no.uio.ifi.wsi.search.language.SparqlGenerator;
import no.uio.ifi.wsi.search.language.TempData;
import no.uio.ifi.wsi.search.language.WeightedString;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GenericSparqlGenerator extends SparqlGenerator {

	private Map<String, GenericPattern> map;
	private GenericConfig conf;

	public GenericSparqlGenerator(GenericConfig conf_) {
		super();
		conf = conf_;
		map = Maps.newHashMap();
		for (GenericPattern p : conf.getPatterns())
			map.put(p.getName(), p);
	}

	@Override
	public WeightedString addRole(String id, String text, long weight,
			RoleParse r) {
		String rel = text;
		if (!text.contains("+")) {
			rel = prefix() + ":" + rel;
		} else {
			rel = "<" + namespace() + "#" + rel + ">";

		}
		rel = "?" + id + " " + rel + " ?" + r.getTo();
		return new WeightedString(conf.getRoleLevel(), rel, weight);
	}

	@Override
	public WeightedString toSparqlNode(Parse p, String annotation, TempData temp) {
		GenericNodeParse n = (GenericNodeParse) p;
		List<WeightedString> ws = Lists.newArrayList();
		String id = n.getId();
		if (id == null)
			id = getId(temp);
		temp.getVariables().add(id);

		if (n.getNonPatternText() != null) {
			ws.addAll(addType(id, conf.getTextLevel(), n.getNonPatternText(),
					conf.getTextName()));
		}

		for (String name : n.getValues().keySet()) {
			GenericPattern gp = map.get(name);
			if (gp.isBoolean()) {
				String s = "?" + id + " " + annotation + ":" + name
						+ " \"true\"^^xsd:boolean";
				ws.add(new WeightedString(gp.getLevel(), s, 0));
			} else
				ws.addAll(addType(id, gp.getLevel(), n.getValues().get(name),
						name));
		}
		for (RoleParse r : n.getRoles()) {
			if (r.getTo() == null)
				r.setTo(getId(temp));
			temp.getVariables().add(r.getTo());
			WeightedString toadd = addRole(id, r, conf.getRoleName());
			ws.add(toadd);
		}
		return new WeightedString(ws);
	}

	@Override
	public String prefix() {
		return conf.getPrefix();
	}

	@Override
	public String name() {
		return conf.getNamespace();
	}
}

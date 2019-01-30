package no.uio.ifi.wsi.generator.rdf;

import java.util.List;

import no.uio.ifi.wsi.SemanticStructure;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory.Interface;
import com.hp.hpl.jena.sparql.core.Quad;

public abstract class RDFGenerator {

	private final Interface factory;

	public RDFGenerator() {
		factory = ResourceFactory.getInstance();
	}

	public abstract List<Quad> convert(SemanticStructure structure);

	public Quad createDatatypeBooleanPropety(String resource, String relation,
			Boolean s, String sid) {
		return Quad.create(createResource(sid).asNode(),
				createResource(resource).asNode(), createPropery(relation)
						.asNode(),
				factory.createTypedLiteral("" + s, XSDDatatype.XSDboolean)
						.asNode());
	}

	public Quad createDatatypeStringPropety(String resource, String relation,
			String s, String sid) {
		return Quad.create(createResource(sid).asNode(),
				createResource(resource).asNode(), createPropery(relation)
						.asNode(),
				factory.createTypedLiteral(s, XSDDatatype.XSDstring).asNode());
	}

	public Quad createObjectProperty(String resource, String p,
			String resources2, String sid) {

		return Quad.create(createResource(sid).asNode(),
				createResource(resource).asNode(), createPropery(p).asNode(),
				createResource(resources2).asNode());
	}

	public Property createPropery(String relation) {
		return factory.createProperty(relation);
	}

	public Resource createResource(String resource) {
		return factory.createResource(resource);
	}
}

package no.uio.ifi.wsi.index;

import java.io.File;
import java.io.IOException;

import no.uio.ifi.wsi.SemanticStructure;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class LuceneStructureStorage extends StructureStorage {

	private final IndexSearcher documents;

	public int structureCount() {
		return documents.getIndexReader().numDocs();
	}

	public LuceneStructureStorage(String path, SemanticStructure struct) {
		super(struct);
		try {
			documents = new IndexSearcher(DirectoryReader.open(FSDirectory
					.open(new File(path + "index"))));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public SemanticStructure getDocument(String id) {
		try {
			TopDocs td = documents.search(new TermQuery(new Term("id", id)), 1);
			Document doc = documents.doc(td.scoreDocs[0].doc);
			System.out.println(doc.get("svg"));
			SemanticStructure s = getStructure().newInstance();
			s.setStructure(doc.get("structure"));
			s.setText(doc.get("text"));
			s.setId(doc.get("id"));
			s.setSvg(doc.get("svg"));
			System.out.println(s.getSvg());
			s.load();
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
}

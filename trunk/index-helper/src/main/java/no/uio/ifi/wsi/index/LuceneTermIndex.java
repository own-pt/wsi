package no.uio.ifi.wsi.index;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.FSDirectory;

import com.google.common.collect.Lists;

@Log4j
public class LuceneTermIndex implements TermIndex {

	private final IndexSearcher counts;

	public LuceneTermIndex(String path) {
		try {
			counts = new IndexSearcher(DirectoryReader.open(FSDirectory
					.open(new File(path + "count"))));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	public List<TermCount> searchTerm(String query, boolean regex,
			String field, int totalLimit) {
		try {
			System.out.println(field);
			query = query.toLowerCase();
			Query q = null;
			if (regex) {
				q = new WildcardQuery(new Term(field, query));
			} else {
				q = new TermQuery(new Term(field, query));
			}
			TopDocs td = counts.search(q, totalLimit);
			List<TermCount> out = Lists.newArrayList();
			for (int i = 0; i < td.scoreDocs.length; i++) {
				Document doc = counts.doc(td.scoreDocs[i].doc);
				String term = doc.getField("value").stringValue();
				long count = doc.getField("count").numericValue().longValue();
				out.add(new TermCount(count, term, field));
			}
			return out;
		} catch (IOException e) {
			e.printStackTrace();
			log.debug(e.getMessage(), e);
		}
		return null;
	}

	public TermCount weight(String text, String type) {

		text = text.toLowerCase();
		Term term = new Term(type, text);
		TermQuery query = new TermQuery(term);
		try {
			TopDocs td = counts.search(query, 1);
			if (td.scoreDocs.length == 0) {
				return null;
			}
			Document doc = counts.doc(td.scoreDocs[0].doc);
			return new TermCount(Long.parseLong(doc.get("count")),
					doc.get("value"), type);
		} catch (IOException e) {
			e.printStackTrace();
			log.debug(e.getMessage(), e);
		}
		return null;

	}
}

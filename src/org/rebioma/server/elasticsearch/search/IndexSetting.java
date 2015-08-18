package org.rebioma.server.elasticsearch.search;

import java.io.IOException;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.rebioma.server.elasticsearch.json.JsonFileUtility;

/**
 * 
 * @author Mikajy
 *
 */
public class IndexSetting {
	
	public static String getSettingsAsString()  throws IOException{
		String setting = JsonFileUtility.getSettingContent();
		return setting;
	}
	public static XContentBuilder getSettings() throws IOException{
		XContentBuilder settings = XContentFactory.jsonBuilder().humanReadable(true).startObject()
				.field("number_of_shards", 1)
				.startObject("analysis")
					.startObject("filter")
						.startObject("code")
							.field("type", "pattern_capture")
							.field("preserve_original", 1)
							// http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#field_summary
							.array("patterns", "(\\p{Ll}+|\\p{Lu}\\p{Ll}+|\\p{Lu}+|\\p{Lu}+)", "(\\d+)")
						.endObject()
						.startObject("english_simple_stop")
							.field("type", "stop")
							.field("stopwords", "_english_")
						.endObject()
						.startObject("english_simple_stemmer")
							.field("type", "stemmer")
							.field("language", "english")
						.endObject()
						.startObject("remove_doublon")
							.field("type", "pattern_replace")
							.field("pattern", "(\\p{Alpha})\\1")
							.field("replacement", "$1")
						.endObject()
						.startObject("shingle_filter")
							.field("type","shingle")
							.field("min_shingle_size", 2)
							.field("max_shingle_size", 2)
							.field("output_unigrams", false)
						.endObject()
					.endObject()
					
					.startObject("analyzer")//custom analyzer http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-custom-analyzer.html
						.startObject("code")//Analyse de code source
							.field("type", "custom")
							.field("char_filter", "html_strip")
							.field("tokenizer", "pattern")
							.array("filter", "code", "lowercase")
						.endObject()
						.startObject("no_doublon")
							.field("type", "custom")
							.field("char_filter", "html_strip")
							.field("tokenizer", "pattern")
							.array("filter", "lowercase", "remove_doublon")
						.endObject()
						.startObject("english_no_doublon")
							.field("type", "custom")
							.field("tokenizer", "standard")
							.array("filter", "lowercase", "english_simple_stop", "remove_doublon", "english_simple_stemmer")
						.endObject()
						.startObject("bigram_analyzer")
							.field("type", "custom")
							.field("tokenizer", "standard")
							.array("filter", "lowercase", "english_simple_stop", "english_simple_stemmer", "shingle_filter")
						.endObject()
					.endObject()
				.endObject()
			.endObject();
			return settings;
	}
}

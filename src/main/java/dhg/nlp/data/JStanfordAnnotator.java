package dhg.nlp.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class JStanfordAnnotator {

	StanfordCoreNLP pipeline;

	public JStanfordAnnotator(String posModelLocation, String nerModelLocation) {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization,
		// NER, parsing, and coreference resolution
		Properties props = new Properties();
		// TODO: , ner");
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		props.put("pos.model", posModelLocation);
		props.put("ner.model", nerModelLocation);
		this.pipeline = new StanfordCoreNLP(props);
	}

	public static class JStanfordAnnotatedToken {
		String word;
		String lemma;
		String pos;
		String ne;

		public JStanfordAnnotatedToken(String word, String lemma, String pos, String ne) {
			this.word = word;
			this.lemma = lemma;
			this.pos = pos;
			this.ne = ne;
		}
	}

	public List<List<JStanfordAnnotatedToken>> annotate(String text) {

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		List<List<JStanfordAnnotatedToken>> sentences = new ArrayList<List<JStanfordAnnotatedToken>>();
		for (CoreMap sentence : document.get(SentencesAnnotation.class)) {
			List<JStanfordAnnotatedToken> tokens = new ArrayList<JStanfordAnnotatedToken>();
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				String lemma = token.get(LemmaAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);
				// TODO: token.get(NamedEntityTagAnnotation.class);
				String ne = "";

				tokens.add(new JStanfordAnnotatedToken(word, lemma, pos, ne));
			}
			sentences.add(tokens);
		}

		return sentences;
	}
}

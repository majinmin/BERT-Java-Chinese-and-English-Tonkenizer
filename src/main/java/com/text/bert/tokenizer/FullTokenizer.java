package com.text.bert.tokenizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

import com.google.common.io.Resources;
import  com.text.bert.tokenizer.My_Map;

/**
 * A port of the BERT FullTokenizer in the <a href="https://github.com/google-research/bert">BERT GitHub Repository</a>.
 *
 * It's used to segment input sequences into the BERT tokens that exist in the model's vocabulary. These tokens are later converted into inputIds for the model.
 *
 * It basically just feeds sequences to the {@link com.robrua.nlp.bert.BasicTokenizer} then passes those results to the
 * {@link com.robrua.nlp.bert.WordpieceTokenizer}
 *
 * @author Rob Rua (https://github.com/robrua)
 * @version 1.0.3
 * @since 1.0.3
 *
 * @see <a href="https://github.com/google-research/bert/blob/master/tokenization.py">The Python tokenization code this is ported from</a>
 */
public class FullTokenizer extends Tokenizer {
    private static final boolean DEFAULT_DO_LOWER_CASE = false;

    private static My_Map loadVocabulary(final Path file) {
        final My_Map vocabulary = new My_Map();
        try(BufferedReader reader = Files.newBufferedReader(file, Charset.forName("UTF-8"))) {
            int index = 0;
            String line;
            while((line = reader.readLine()) != null) {
                vocabulary.put(line.trim(), index++);
            }
        } catch(final IOException e) {
            throw new RuntimeException(e);
        }
        return vocabulary;
    }

    private static Path toPath(final String resource) {
        try {
        	System.out.println(resource);
            return Paths.get(Resources.getResource(resource).toURI());
        } catch(final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private final BasicTokenizer basic;
    private final My_Map vocabulary;
    private final WordpieceTokenizer wordpiece;

    /**
     * Creates a BERT {@link com.robrua.nlp.bert.FullTokenizer}
     *
     * @param vocabulary
     *        the BERT vocabulary file to use for tokenization
     * @since 1.0.3
     */
    public FullTokenizer(final File vocabulary) {
        this(Paths.get(vocabulary.toURI()), DEFAULT_DO_LOWER_CASE);
    }

    /**
     * Creates a BERT {@link com.robrua.nlp.bert.FullTokenizer}
     *
     * @param vocabulary
     *        the BERT vocabulary file to use for tokenization
     * @param doLowerCase
     *        whether to convert sequences to lower case during tokenization
     * @since 1.0.3
     */
    public FullTokenizer(final File vocabulary, final boolean doLowerCase) {
        this(Paths.get(vocabulary.toURI()), doLowerCase);
    }

    /**
     * Creates a BERT {@link com.robrua.nlp.bert.FullTokenizer}
     *
     * @param vocabularyPath
     *        the path to the BERT vocabulary file to use for tokenization
     * @since 1.0.3
     */
    public FullTokenizer(final Path vocabularyPath) {
        this(vocabularyPath, DEFAULT_DO_LOWER_CASE);
    }

    /**
     * Creates a BERT {@link com.robrua.nlp.bert.FullTokenizer}
     *
     * @param vocabularyPath
     *        the path to the BERT vocabulary file to use for tokenization
     * @param doLowerCase
     *        whether to convert sequences to lower case during tokenization
     * @since 1.0.3
     */
    public FullTokenizer(final Path vocabularyPath, final boolean doLowerCase) {
        vocabulary = loadVocabulary(vocabularyPath);
        basic = new BasicTokenizer(doLowerCase);
        wordpiece = new WordpieceTokenizer(vocabulary);
    }

    /**
     * Creates a BERT {@link com.robrua.nlp.bert.FullTokenizer}
     *
     * @param vocabularyResource
     *        the resource path to the BERT vocabulary file to use for tokenization
     * @since 1.0.3
     */
    public FullTokenizer(final String vocabularyResource) {
        this(toPath(vocabularyResource), DEFAULT_DO_LOWER_CASE);
    }

    /**
     * Creates a BERT {@link com.robrua.nlp.bert.FullTokenizer}
     *
     * @param vocabularyResource
     *        the resource path to the BERT vocabulary file to use for tokenization
     * @param doLowerCase
     *        whether to convert sequences to lower case during tokenization
     * @since 1.0.3
     */
    public FullTokenizer(final String vocabularyResource, final boolean doLowerCase) {
        this(toPath(vocabularyResource), doLowerCase);
    }

    /**
     * Converts BERT sub-tokens into their inputIds
     *
     * @param tokens
     *        the tokens to convert
     * @return the inputIds for the tokens
     * @since 1.0.3
     */

    public int[] convert(final String[] tokens) {
        
		return Arrays.stream(tokens).mapToInt(vocabulary::getMy).toArray();
    }

    @Override
    public String[] tokenize(final String sequence) {
        return Arrays.stream(wordpiece.tokenize(basic.tokenize(sequence)))
            .flatMap(Stream::of)
            .toArray(String[]::new);
    }

    @Override
    public String[][] tokenize(final String... sequences) {
        return Arrays.stream(basic.tokenize(sequences))
            .map((final String[] tokens) -> Arrays.stream(wordpiece.tokenize(tokens))
                .flatMap(Stream::of)
                .toArray(String[]::new))
            .toArray(String[][]::new);
    }
    

    private List<String> myMap_splitToken(final String token) {
    	
    	//System.out.println(token);
    	List<String> singleStringSplit =  new ArrayList<>();
        final char[] characters = token.toCharArray();
        if(characters.length > WordpieceTokenizer.DEFAULT_MAX_CHARACTERS_PER_WORD) {
            //return Stream.of(WordpieceTokenizer.DEFAULT_UNKNOWN_TOKEN);
        	singleStringSplit.add(WordpieceTokenizer.DEFAULT_UNKNOWN_TOKEN);
        	return singleStringSplit;
        }
        final Stream.Builder<String> subtokens = Stream.builder();
        int start = 0;
        int end = 0;
        while(start < characters.length) {
            end = characters.length;
            
            boolean found = false;
            while(start < end) {
                final String substring = (start > 0 ? "##" : "") + String.valueOf(characters, start, end - start);
                if(vocabulary.containsKey(substring)) {
                    subtokens.accept(substring);
                    singleStringSplit.add(substring);
                    //System.out.println(substring + "\t" + start + "\t" + end);
                    start = end;
                    found = true;
                    break;
                }
                end--;
            }
            if(!found) {
                subtokens.accept(WordpieceTokenizer.DEFAULT_UNKNOWN_TOKEN);
                singleStringSplit.add(WordpieceTokenizer.DEFAULT_UNKNOWN_TOKEN);
                //System.out.println(WordpieceTokenizer.DEFAULT_UNKNOWN_TOKEN + "\t" + start + "\t" + end);
                break;
            }
            start = end;
        }
        //System.out.println(subtokens);
        Stream<String> subtokens_results = subtokens.build();
        //System.out.println(subtokens);
        return singleStringSplit;
    }
    
    public  List<List<Integer>> getMapOfOrginToNewTokens(final String... sequences) {
    	String[][] base_token = basic.tokenize(sequences);
    	
    	List<List<Integer>> all_orgin_positions_fix_with_new = new ArrayList<>();
    	for (int i = 0; i < base_token.length; i++) {
    		
    		//原始位置
    		List<Integer> orgin_positions_fix_with_new = new ArrayList<Integer>(); 
    		//token化的新位置
    		//List<Integer> new_positions = new ArrayList<Integer>();
    		
    		for (int j = 0; j < base_token[i].length; j++) {
				String temp_token = base_token[i][j];
    			//System.out.println("temp_token " + "\t" +  temp_token);
				List<String> single_string_split_words = this.myMap_splitToken(temp_token);
				for (String string : single_string_split_words) {
					orgin_positions_fix_with_new.add(j);
				}
			}
    		all_orgin_positions_fix_with_new.add(orgin_positions_fix_with_new);
		}
    	
    	return all_orgin_positions_fix_with_new;
    }
    
}

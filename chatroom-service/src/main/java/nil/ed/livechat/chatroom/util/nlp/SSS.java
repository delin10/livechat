package nil.ed.livechat.chatroom.util.nlp;

import java.util.List;
import java.util.stream.Collectors;

import com.hankcs.hanlp.seg.Dijkstra.DijkstraSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;

/**
 * @author delin10
 * @since 2019/10/24
 **/
public class SSS {
    public static void main(String[] args) {
        String text = "鸡掰你这个鸡巴鬼，鸡鸡鸡鸡鸡鸡鸡巴真小,真是个傻逼，操你大爷，操尼玛";
        Segment segment = new DijkstraSegment().enableCustomDictionary(false);
        List<Term> termList = segment.seg(text);
        System.out.println(termList);
        System.out.println("1");
        List<Word> wordList = WordSegmenter.segWithStopWords(text, SegmentationAlgorithm.FullSegmentation);
        System.out.println("2");
        WordSegmenter.segWithStopWords(text, SegmentationAlgorithm.FullSegmentation);
        System.out.println("3");
        WordSegmenter.segWithStopWords(text, SegmentationAlgorithm.FullSegmentation);
        System.out.println("4");
        WordSegmenter.segWithStopWords(text, SegmentationAlgorithm.FullSegmentation);
        System.out.println("5");
        WordSegmenter.segWithStopWords(text, SegmentationAlgorithm.FullSegmentation);
        String wordWithPy = wordList.stream()
                .map(word -> String.format("[%s, %s]", word.getText(), word.getFullPinYin()))
                .collect(Collectors.joining(" "));
        System.out.println(wordWithPy);
        StanfordCoreNLP stanfordCoreNLP = new StanfordCoreNLP("StanfordCoreNLP-chinese");
        Annotation document = new Annotation(text);
        stanfordCoreNLP.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        StringBuilder result = new StringBuilder();
        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                result.append(word).append(" ");
            }
        }
        System.out.println(result);
    }
}

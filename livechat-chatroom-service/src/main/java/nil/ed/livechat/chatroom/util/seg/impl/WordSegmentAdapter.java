package nil.ed.livechat.chatroom.util.seg.impl;

import java.util.List;

import nil.ed.livechat.chatroom.util.seg.SegmentAdapter;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;

/**
 * @author delin10
 * @since 2019/10/24
 **/
public class WordSegmentAdapter implements SegmentAdapter {
    @Override
    public List<String> seg(String text) {
         WordSegmenter.seg(text, SegmentationAlgorithm.FullSegmentation);
         return null;
    }
}

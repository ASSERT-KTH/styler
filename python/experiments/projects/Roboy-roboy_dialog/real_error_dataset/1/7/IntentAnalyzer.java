package roboy.linguistics.sentenceanalysis;

import roboy.ros.RosMainNode;

/**
 * Calls a machine learning model to determine if the utterance of the other person represents
 * one of the learned intents. Stores the highest scoring intent in the Linguistics.INTENT feature
 * and the score in the Linguistics.INTENT_DISTANCE feature.
 */
public class IntentAnalyzer implements Analyzer {
    private RosMainNode ros;

    public IntentAnalyzer(RosMainNode ros) {
        this.ros = ros;
    }

    @Override
    public Interpretation analyze(Interpretation sentence) {
        Object[] intent = (Object[]) ros.DetectIntent(sentence.getSentence());
        if(intent.length == 2) {
            try {
                sentence.setIntent(intent[0].toString());
                sentence.setIntentDistance(intent[1].toString());
            } catch (RuntimeException e) {
                System.out.println("Exception while parsing intent response: " + e.getStackTrace());
            }
        }
        return sentence;
    }
}

package roboy.ros;

import roboy_communication_cognition.*;
import roboy_communication_control.*;

/**
 * Stores the different client addresses and corresponding ROS message types.
 */

enum RosServiceClients {

    SPEECHSYNTHESIS("roboy_speech_synthesis", "/roboy/cognition/speech/synthesis/talk", Talk._TYPE),
    GENERATIVE("roboy_gnlp", "/roboy/cognition/generative_nlp/answer", GenerateAnswer._TYPE),
    FACEDETECTION("roboy_vision", "/speech_synthesis/talk", DetectFace._TYPE),
    OBJECTRECOGNITION("roboy_vision", "/speech_synthesis/talk", RecognizeObject._TYPE),
    STT("roboy_speech_recognition", "/roboy/cognition/speech/recognition", RecognizeSpeech._TYPE),
    EMOTION("roboy_face","/roboy/cognition/face/emotion", ShowEmotion._TYPE),
    CREATEMEMORY("roboy_memory", "/roboy/cognition/memory/create", DataQuery._TYPE),
    UPDATEMEMORY("roboy_memory", "/roboy/cognition/memory/update", DataQuery._TYPE),
    GETMEMORY("roboy_memory", "/roboy/cognition/memory/get", DataQuery._TYPE),
    DELETEMEMORY("roboy_memory", "/roboy/cognition/memory/remove", DataQuery._TYPE),
    CYPHERMEMORY("roboy_memory", "/roboy/cognition/memory/cypher", DataQuery._TYPE),
    INTENT("roboy_intents", "/roboy/cognition/detect_intent", DetectIntent._TYPE),
    SNAPCHATFILTER("roboy_filters", "/roboy/cognition/apply_filter", ApplyFilter._TYPE);

    String rosPackage;
    String address;
    String type;


    RosServiceClients(String rosPackage, String address, String type) {
        this.rosPackage = rosPackage;
        this.address = address;
        this.type = type;

    }
}

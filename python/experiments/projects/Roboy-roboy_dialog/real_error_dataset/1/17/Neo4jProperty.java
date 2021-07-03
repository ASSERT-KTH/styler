package roboy.memory;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Contains the relations available in Neo4j database.
 * Respective questions should be added to the questions.json file
 * and used in the QuestionRandomizerState.
 */
public enum Neo4jProperty {
    name("name"),
    sex("sex"),
    full_name("full_name"),
    age("age"),
    skills("skills"),
    abilities("abilities"),
    future("future"),
    birthdate("birthdate"),
    facebook_id("facebook_id"),
    telegram_id("telegram_id"),
    slack_id("slack_id"),
    whatsapp_id("whatsapp_id"),
    line_id("line_id");

    public String type;

    Neo4jProperty(String type) {
        this.type = type;
    }

    private static final Map<String, Neo4jProperty> typeIndex =
            Maps.newHashMapWithExpectedSize(Neo4jProperty.values().length);

    static {
        for (Neo4jProperty property : Neo4jProperty.values()) {
            typeIndex.put(property.type, property);
        }
    }

    public static Neo4jProperty lookupByType(String type) {
        return typeIndex.get(type);
    }

    public static boolean contains(String type){
        return typeIndex.containsKey(type);
    }
}

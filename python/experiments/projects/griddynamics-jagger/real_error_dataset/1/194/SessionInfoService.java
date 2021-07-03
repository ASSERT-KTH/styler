package com.griddynamics.jagger.engine.e1.services;

import java.util.Set;

/** Service gives ability to create and modify session metadata(such as session comment)
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details Where this service is available you can find in chapter: @ref Main_ListenersAndServices_group @n
 * @n
 * @par Example - working with session comments:
 * @dontinclude ProviderOfLoadScenarioListener.java
 * @skip  begin: following section is used for docu generation - work with session comments
 * @until end: following section is used for docu generation - work with session comments
 * @n
 * @par Example - working with session tags:
 * @dontinclude ProviderOfLoadScenarioListener.java
 * @skip  begin: following section is used for docu generation - work with session tags
 * @until end: following section is used for docu generation - work with session tags
 *
 * @n
 * Full example code you can find in chapter @ref Main_CustomListenersExamples_group @n
 * @n
 * @ingroup Main_Services_group */
public interface SessionInfoService extends JaggerService{

    /** Returns current session comment
     * @author Gribov Kirill
     *
     * @n
     *@return session comment */
    String getComment();

    /** Set new session comment. Null value will be ignored.
     * @author Gribov Kirill
     * @n
     * @param comment - new session comment */
    void setComment(String comment);

    /** Append string to current session comment. Null value will be ignored.
     * @author Gribov Kirill
     * @n
     * @param st - string to append */
    void appendToComment(String st);

    /** Creates new or update existing tag
     *
     * @param tagName - a tag name which should be created or updated
     * @param tagDescription - a description of the tag
     */
    void saveOrUpdateTag(String tagName, String tagDescription);

    /** Marks session with tag. Previously set tags are not influenced
     *
     * @param tagName - a tag name which should be used to mark a session
     */
    void markSessionWithTag(String tagName);

    /** Returns tags which already marked the session
     *
     * @return set names of tags
     */
    Set<String> getSessionTags();

}

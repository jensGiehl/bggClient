package de.agiehl.bgg.model.forum;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Top-level wrapper of the {@code /forumlist} response.
 */
@Value
@Builder
@Jacksonized
@JacksonXmlRootElement(localName = "forums")
public class ForumListResponse {

    /** Terms of use URL provided by BGG. */
    @JacksonXmlProperty(isAttribute = true)
    String termsofuse;

    /** Id of the Thing/Family this forum list belongs to. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Type of the parent (e.g. {@code thing}). */
    @JacksonXmlProperty(isAttribute = true)
    String type;

    /** Available forums. */
    @Singular("forum")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "forum")
    List<ForumSummary> forums;
}

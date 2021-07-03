/**
 * This code was generated by
 * \ / _    _  _|   _  _
 *  | (_)\/(_)(_|\/| |(/_  v1.0.0
 *       /       /
 */

package com.twilio.rest.messaging.v1.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.base.Resource;
import com.twilio.converter.Converter;
import com.twilio.converter.DateConverter;
import com.twilio.exception.ApiConnectionException;
import com.twilio.exception.ApiException;
import com.twilio.exception.RestException;
import com.twilio.http.HttpMethod;
import com.twilio.http.Request;
import com.twilio.http.Response;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.Domains;
import lombok.ToString;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * PLEASE NOTE that this class contains beta products that are subject to
 * change. Use them with caution.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class UsAppToPerson extends Resource {
    private static final long serialVersionUID = 131982293842514L;

    /**
     * Create a UsAppToPersonCreator to execute create.
     *
     * @param pathMessagingServiceSid The SID of the Messaging Service to create
     *                                the resource from
     * @param brandRegistrationSid A2P Brand Registration SID
     * @param description A short description of what this SMS campaign does
     * @param messageSamples Message samples
     * @param usAppToPersonUsecase A2P Campaign Use Case.
     * @param hasEmbeddedLinks Indicates that this SMS campaign will send messages
     *                         that contain links
     * @param hasEmbeddedPhone Indicates that this SMS campaign will send messages
     *                         that contain phone numbers
     * @return UsAppToPersonCreator capable of executing the create
     */
    public static UsAppToPersonCreator creator(final String pathMessagingServiceSid,
                                               final String brandRegistrationSid,
                                               final String description,
                                               final List<String> messageSamples,
                                               final String usAppToPersonUsecase,
                                               final Boolean hasEmbeddedLinks,
                                               final Boolean hasEmbeddedPhone) {
        return new UsAppToPersonCreator(pathMessagingServiceSid, brandRegistrationSid, description, messageSamples, usAppToPersonUsecase, hasEmbeddedLinks, hasEmbeddedPhone);
    }

    /**
     * Create a UsAppToPersonDeleter to execute delete.
     *
     * @param pathMessagingServiceSid The SID of the Messaging Service to delete
     *                                the resource from
     * @param pathSid The SID that identifies the US A2P Compliance resource to
     *                delete
     * @return UsAppToPersonDeleter capable of executing the delete
     */
    public static UsAppToPersonDeleter deleter(final String pathMessagingServiceSid,
                                               final String pathSid) {
        return new UsAppToPersonDeleter(pathMessagingServiceSid, pathSid);
    }

    /**
     * Create a UsAppToPersonReader to execute read.
     *
     * @param pathMessagingServiceSid The SID of the Messaging Service to fetch the
     *                                resource from
     * @return UsAppToPersonReader capable of executing the read
     */
    public static UsAppToPersonReader reader(final String pathMessagingServiceSid) {
        return new UsAppToPersonReader(pathMessagingServiceSid);
    }

    /**
     * Create a UsAppToPersonFetcher to execute fetch.
     *
     * @param pathMessagingServiceSid The SID of the Messaging Service to fetch the
     *                                resource from
     * @param pathSid The SID that identifies the US A2P Compliance resource to
     *                fetch
     * @return UsAppToPersonFetcher capable of executing the fetch
     */
    public static UsAppToPersonFetcher fetcher(final String pathMessagingServiceSid,
                                               final String pathSid) {
        return new UsAppToPersonFetcher(pathMessagingServiceSid, pathSid);
    }

    /**
     * Converts a JSON String into a UsAppToPerson object using the provided
     * ObjectMapper.
     *
     * @param json Raw JSON String
     * @param objectMapper Jackson ObjectMapper
     * @return UsAppToPerson object represented by the provided JSON
     */
    public static UsAppToPerson fromJson(final String json, final ObjectMapper objectMapper) {
        // Convert all checked exceptions to Runtime
        try {
            return objectMapper.readValue(json, UsAppToPerson.class);
        } catch (final JsonMappingException | JsonParseException e) {
            throw new ApiException(e.getMessage(), e);
        } catch (final IOException e) {
            throw new ApiConnectionException(e.getMessage(), e);
        }
    }

    /**
     * Converts a JSON InputStream into a UsAppToPerson object using the provided
     * ObjectMapper.
     *
     * @param json Raw JSON InputStream
     * @param objectMapper Jackson ObjectMapper
     * @return UsAppToPerson object represented by the provided JSON
     */
    public static UsAppToPerson fromJson(final InputStream json, final ObjectMapper objectMapper) {
        // Convert all checked exceptions to Runtime
        try {
            return objectMapper.readValue(json, UsAppToPerson.class);
        } catch (final JsonMappingException | JsonParseException e) {
            throw new ApiException(e.getMessage(), e);
        } catch (final IOException e) {
            throw new ApiConnectionException(e.getMessage(), e);
        }
    }

    private final String sid;
    private final String accountSid;
    private final String brandRegistrationSid;
    private final String messagingServiceSid;
    private final String description;
    private final List<String> messageSamples;
    private final String usAppToPersonUsecase;
    private final Boolean hasEmbeddedLinks;
    private final Boolean hasEmbeddedPhone;
    private final String campaignStatus;
    private final String campaignId;
    private final Boolean isExternallyRegistered;
    private final Map<String, Object> rateLimits;
    private final ZonedDateTime dateCreated;
    private final ZonedDateTime dateUpdated;
    private final URI url;

    @JsonCreator
    private UsAppToPerson(@JsonProperty("sid")
                          final String sid,
                          @JsonProperty("account_sid")
                          final String accountSid,
                          @JsonProperty("brand_registration_sid")
                          final String brandRegistrationSid,
                          @JsonProperty("messaging_service_sid")
                          final String messagingServiceSid,
                          @JsonProperty("description")
                          final String description,
                          @JsonProperty("message_samples")
                          final List<String> messageSamples,
                          @JsonProperty("us_app_to_person_usecase")
                          final String usAppToPersonUsecase,
                          @JsonProperty("has_embedded_links")
                          final Boolean hasEmbeddedLinks,
                          @JsonProperty("has_embedded_phone")
                          final Boolean hasEmbeddedPhone,
                          @JsonProperty("campaign_status")
                          final String campaignStatus,
                          @JsonProperty("campaign_id")
                          final String campaignId,
                          @JsonProperty("is_externally_registered")
                          final Boolean isExternallyRegistered,
                          @JsonProperty("rate_limits")
                          final Map<String, Object> rateLimits,
                          @JsonProperty("date_created")
                          final String dateCreated,
                          @JsonProperty("date_updated")
                          final String dateUpdated,
                          @JsonProperty("url")
                          final URI url) {
        this.sid = sid;
        this.accountSid = accountSid;
        this.brandRegistrationSid = brandRegistrationSid;
        this.messagingServiceSid = messagingServiceSid;
        this.description = description;
        this.messageSamples = messageSamples;
        this.usAppToPersonUsecase = usAppToPersonUsecase;
        this.hasEmbeddedLinks = hasEmbeddedLinks;
        this.hasEmbeddedPhone = hasEmbeddedPhone;
        this.campaignStatus = campaignStatus;
        this.campaignId = campaignId;
        this.isExternallyRegistered = isExternallyRegistered;
        this.rateLimits = rateLimits;
        this.dateCreated = DateConverter.iso8601DateTimeFromString(dateCreated);
        this.dateUpdated = DateConverter.iso8601DateTimeFromString(dateUpdated);
        this.url = url;
    }

    /**
     * Returns The unique string that identifies a US A2P Compliance resource.
     *
     * @return The unique string that identifies a US A2P Compliance resource
     */
    public final String getSid() {
        return this.sid;
    }

    /**
     * Returns The SID of the Account that created the resource.
     *
     * @return The SID of the Account that created the resource
     */
    public final String getAccountSid() {
        return this.accountSid;
    }

    /**
     * Returns A2P Brand Registration SID.
     *
     * @return A2P Brand Registration SID
     */
    public final String getBrandRegistrationSid() {
        return this.brandRegistrationSid;
    }

    /**
     * Returns The SID of the Messaging Service the resource is associated with.
     *
     * @return The SID of the Messaging Service the resource is associated with
     */
    public final String getMessagingServiceSid() {
        return this.messagingServiceSid;
    }

    /**
     * Returns A short description of what this SMS campaign does.
     *
     * @return A short description of what this SMS campaign does
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * Returns Message samples.
     *
     * @return Message samples
     */
    public final List<String> getMessageSamples() {
        return this.messageSamples;
    }

    /**
     * Returns A2P Campaign Use Case..
     *
     * @return A2P Campaign Use Case.
     */
    public final String getUsAppToPersonUsecase() {
        return this.usAppToPersonUsecase;
    }

    /**
     * Returns Indicate that this SMS campaign will send messages that contain
     * links.
     *
     * @return Indicate that this SMS campaign will send messages that contain links
     */
    public final Boolean getHasEmbeddedLinks() {
        return this.hasEmbeddedLinks;
    }

    /**
     * Returns Indicates that this SMS campaign will send messages that contain
     * phone numbers.
     *
     * @return Indicates that this SMS campaign will send messages that contain
     *         phone numbers
     */
    public final Boolean getHasEmbeddedPhone() {
        return this.hasEmbeddedPhone;
    }

    /**
     * Returns Campaign status.
     *
     * @return Campaign status
     */
    public final String getCampaignStatus() {
        return this.campaignStatus;
    }

    /**
     * Returns The Campaign Registry (TCR) Campaign ID..
     *
     * @return The Campaign Registry (TCR) Campaign ID.
     */
    public final String getCampaignId() {
        return this.campaignId;
    }

    /**
     * Returns Indicates whether the campaign was registered externally or not.
     *
     * @return Indicates whether the campaign was registered externally or not
     */
    public final Boolean getIsExternallyRegistered() {
        return this.isExternallyRegistered;
    }

    /**
     * Returns Rate limit and/or classification set by each carrier.
     *
     * @return Rate limit and/or classification set by each carrier
     */
    public final Map<String, Object> getRateLimits() {
        return this.rateLimits;
    }

    /**
     * Returns The ISO 8601 date and time in GMT when the resource was created.
     *
     * @return The ISO 8601 date and time in GMT when the resource was created
     */
    public final ZonedDateTime getDateCreated() {
        return this.dateCreated;
    }

    /**
     * Returns The ISO 8601 date and time in GMT when the resource was last updated.
     *
     * @return The ISO 8601 date and time in GMT when the resource was last updated
     */
    public final ZonedDateTime getDateUpdated() {
        return this.dateUpdated;
    }

    /**
     * Returns The absolute URL of the US App to Person resource.
     *
     * @return The absolute URL of the US App to Person resource
     */
    public final URI getUrl() {
        return this.url;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UsAppToPerson other = (UsAppToPerson) o;

        return Objects.equals(sid, other.sid) &&
               Objects.equals(accountSid, other.accountSid) &&
               Objects.equals(brandRegistrationSid, other.brandRegistrationSid) &&
               Objects.equals(messagingServiceSid, other.messagingServiceSid) &&
               Objects.equals(description, other.description) &&
               Objects.equals(messageSamples, other.messageSamples) &&
               Objects.equals(usAppToPersonUsecase, other.usAppToPersonUsecase) &&
               Objects.equals(hasEmbeddedLinks, other.hasEmbeddedLinks) &&
               Objects.equals(hasEmbeddedPhone, other.hasEmbeddedPhone) &&
               Objects.equals(campaignStatus, other.campaignStatus) &&
               Objects.equals(campaignId, other.campaignId) &&
               Objects.equals(isExternallyRegistered, other.isExternallyRegistered) &&
               Objects.equals(rateLimits, other.rateLimits) &&
               Objects.equals(dateCreated, other.dateCreated) &&
               Objects.equals(dateUpdated, other.dateUpdated) &&
               Objects.equals(url, other.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sid,
                            accountSid,
                            brandRegistrationSid,
                            messagingServiceSid,
                            description,
                            messageSamples,
                            usAppToPersonUsecase,
                            hasEmbeddedLinks,
                            hasEmbeddedPhone,
                            campaignStatus,
                            campaignId,
                            isExternallyRegistered,
                            rateLimits,
                            dateCreated,
                            dateUpdated,
                            url);
    }
}
package org.benetech.servicenet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A Referral.
 */
@Entity
@Table(name = "referral")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Referral extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String FULFILLED = "fulfilled";
    public static final String SENT = "sent";
    public static final String WAITING = "Waiting for arrival";
    public static final String ARRIVED = "Arrived";

    @Column(name = "shortcode")
    private String shortcode;

    @Column(name = "sent_at")
    private ZonedDateTime sentAt;

    @Column(name = "fulfilled_at")
    private ZonedDateTime fulfilledAt;

    @ManyToOne
    private UserProfile fromUser;

    @ManyToOne
    @JsonIgnoreProperties("referrals")
    private Organization from;

    @ManyToOne
    private Location fromLocation;

    @ManyToOne
    @JsonIgnoreProperties("referrals")
    private Organization to;

    @ManyToOne
    private Location toLocation;

    @ManyToOne
    @JsonIgnoreProperties("referrals")
    private Beneficiary beneficiary;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    public String getShortcode() {
        return shortcode;
    }

    public Referral shortcode(String shortcode) {
        this.shortcode = shortcode;
        return this;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public ZonedDateTime getSentAt() {
        return sentAt;
    }

    public Referral sentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
        return this;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public ZonedDateTime getFulfilledAt() {
        return fulfilledAt;
    }

    public Referral fulfilledAt(ZonedDateTime fulfilledAt) {
        this.fulfilledAt = fulfilledAt;
        return this;
    }

    public void setFulfilledAt(ZonedDateTime fulfilledAt) {
        this.fulfilledAt = fulfilledAt;
    }

    public Organization getFrom() {
        return from;
    }

    public Referral from(Organization organization) {
        this.from = organization;
        return this;
    }

    public void setFrom(Organization organization) {
        this.from = organization;
    }

    public Organization getTo() {
        return to;
    }

    @SuppressWarnings("PMD.ShortMethodName")
    public Referral to(Organization organization) {
        this.to = organization;
        return this;
    }

    public void setTo(Organization organization) {
        this.to = organization;
    }

    public Beneficiary getBeneficiary() {
        return beneficiary;
    }

    public Referral beneficiary(Beneficiary beneficiary) {
        this.beneficiary = beneficiary;
        return this;
    }

    public void setBeneficiary(Beneficiary beneficiary) {
        this.beneficiary = beneficiary;
    }


    public Location getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(Location fromLocation) {
        this.fromLocation = fromLocation;
    }

    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Referral)) {
            return false;
        }
        return getId() != null && getId().equals(((Referral) o).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Referral{" +
            "id=" + getId() +
            ", shortcode='" + getShortcode() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", fulfilledAt='" + getFulfilledAt() + "'" +
            "}";
    }

    public UserProfile getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserProfile fromUser) {
        this.fromUser = fromUser;
    }
}

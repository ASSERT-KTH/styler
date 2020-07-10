package org.benetech.servicenet.repository;

import org.benetech.servicenet.domain.Organization;
import org.benetech.servicenet.domain.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Spring Data  repository for the Organization entity.
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    @Query("SELECT org FROM Organization org WHERE org.account.id = :ownerId")
    List<Organization> findAllWithOwnerId(@Param("ownerId") UUID ownerId);

    @Query("SELECT org FROM Organization org WHERE :userProfile MEMBER OF org.userProfiles")
    List<Organization> findAllWithUserProfile(@Param("userProfile") UserProfile userProfile);

    @Query("SELECT org FROM Organization org WHERE :userProfile NOT MEMBER OF org.userProfiles")
    Page<Organization> findAllWithoutUserProfile(@Param("userProfile") UserProfile userProfile, Pageable pageable);

    @Query("SELECT org FROM Organization org WHERE org.account.id = :ownerId")
    Page<Organization> findAllWithOwnerId(@Param("ownerId") UUID ownerId, Pageable pageable);

    @Query("SELECT org FROM Organization org "
        + "LEFT JOIN FETCH org.account "
        + "LEFT JOIN FETCH org.locations")
    List<Organization> findAllWithEagerAssociations();

    @Query("SELECT org FROM Organization org "
        + "LEFT JOIN FETCH org.account "
        + "LEFT JOIN FETCH org.locations "
        + "WHERE org.id = :id")
    Organization findOneWithEagerAssociations(@Param("id") UUID id);

    @Query("SELECT org FROM Organization org " +
        "LEFT JOIN FETCH org.contacts " +
        "WHERE org.externalDbId = :externalDbId AND org.account.name = :providerName")
    Optional<Organization> findOneWithEagerAssociationsByExternalDbIdAndProviderName(@Param("externalDbId")
                                                                                         String externalDbId,
                                                                                     @Param("providerName")
                                                                                         String providerName);

    List<Organization> findAllByIdOrExternalDbId(UUID id, String externalDbId);

    @Query("SELECT org FROM Organization org WHERE org.account.name != :providerName AND org.active = True")
    List<Organization> findAllByProviderNameNot(@Param("providerName") String providerName);

    @Query("SELECT org FROM Organization org WHERE org.id NOT IN :ids "
        + "AND org.account.name != :providerName AND org.active = True")
    List<Organization> findAllByProviderNameNotAnAndIdNotIn(@Param("providerName") String providerName,
        @Param("ids") List<UUID> ids);

    Page<Organization> findAll(Pageable pageable);

    @Query("SELECT org FROM Organization org "
        + "LEFT JOIN FETCH org.account "
        + "LEFT JOIN FETCH org.locations locs "
        + "LEFT JOIN FETCH org.services srvs "
        + "LEFT JOIN FETCH org.contacts "
        + "LEFT JOIN FETCH org.phones "
        + "LEFT JOIN FETCH org.programs "
        + "LEFT JOIN FETCH locs.regularSchedule lRS "
        + "LEFT JOIN FETCH lRS.openingHours "
        + "LEFT JOIN FETCH locs.holidaySchedules "
        + "LEFT JOIN FETCH locs.langs "
        + "LEFT JOIN FETCH locs.accessibilities "
        + "LEFT JOIN FETCH srvs.regularSchedule sRS "
        + "LEFT JOIN FETCH sRS.openingHours "
        + "LEFT JOIN FETCH srvs.holidaySchedules "
        + "LEFT JOIN FETCH srvs.funding "
        + "LEFT JOIN FETCH srvs.eligibility "
        + "LEFT JOIN FETCH srvs.docs "
        + "LEFT JOIN FETCH srvs.paymentsAccepteds "
        + "LEFT JOIN FETCH srvs.langs "
        + "LEFT JOIN FETCH srvs.taxonomies "
        + "LEFT JOIN FETCH srvs.phones "
        + "LEFT JOIN FETCH srvs.contacts "
        + "WHERE org.id = :id OR "
        + "org.externalDbId = :externalDbId OR "
        + "locs.id = :id OR "
        + "locs.externalDbId = :externalDbId OR "
        + "srvs.id = :id OR "
        + "srvs.externalDbId = :externalDbId")
    Organization findOneWithAllEagerAssociationsByIdOrExternalDbId(@Param("id") UUID id, @Param("externalDbId") String externalDbId);
}

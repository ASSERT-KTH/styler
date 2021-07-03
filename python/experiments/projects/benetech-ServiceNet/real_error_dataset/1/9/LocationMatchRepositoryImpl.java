package org.benetech.servicenet.repository;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.benetech.servicenet.domain.LocationMatch;
import org.benetech.servicenet.domain.LocationMatch_;
import org.benetech.servicenet.domain.Location_;
import org.benetech.servicenet.service.dto.LocationMatchDto;


public class LocationMatchRepositoryImpl implements LocationMatchRepositoryCustom {
    public static final Integer QUERY_PREDICATE_CHUNK_SIZE = 256;

    private final EntityManager em;
    private final CriteriaBuilder cb;

    LocationMatchRepositoryImpl(EntityManager em) {
        this.em = em;
        this.cb = em.getCriteriaBuilder();
    }

    @Override
    public void deleteInBatchByLocationAndMatchingLocationIds(
        List<LocationMatchDto> dtos) {
        CriteriaBuilder criteriaBuilder  = em.getCriteriaBuilder();
        Lists.partition(dtos, QUERY_PREDICATE_CHUNK_SIZE).forEach(locationMatchDtos -> {
            CriteriaDelete<LocationMatch> query = criteriaBuilder
                .createCriteriaDelete(LocationMatch.class);
            Root<LocationMatch> root = query.from(LocationMatch.class);
            List<Predicate> predicates = new ArrayList<>();

            locationMatchDtos.forEach(dto -> {
                predicates.add(cb.and(
                    cb.equal(root.get(LocationMatch_.LOCATION).get(Location_.ID),
                        dto.getLocation()),
                    cb.equal(root.get(LocationMatch_.LOCATION).get(Location_.ID),
                        dto.getMatchingLocation())
                ));
                predicates.add(cb.and(
                    cb.equal(root.get(LocationMatch_.LOCATION).get(Location_.ID),
                        dto.getMatchingLocation()),
                    cb.equal(root.get(LocationMatch_.LOCATION).get(Location_.ID), dto.getLocation())
                ));
            });
            query.where(predicates.toArray(Predicate[]::new));

            em.createQuery(query).executeUpdate();
        });
    }
}

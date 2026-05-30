package com.example.tounip.tounip.space.infrastructure.persistence.repository;

import com.example.tounip.tounip.space.application.dto.SpaceSearchCommand;
import com.example.tounip.tounip.space.infrastructure.persistence.entity.SpaceEntity;
import com.example.tounip.tounip.user.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SpaceCriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<SpaceEntity> searchSpaces(SpaceSearchCommand command) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SpaceEntity> query = cb.createQuery(SpaceEntity.class);

        Root<SpaceEntity> space = query.from(SpaceEntity.class);
        space.fetch("owner", JoinType.INNER);

        Join<SpaceEntity, UserEntity> owner = space.join("owner", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();

        if (Boolean.TRUE.equals(command.getOnlyPublic())) {
            predicates.add(cb.isTrue(space.get("isPublic")));
        }

        String searchText = normalizeSearchText(command.getQuery());

        if (searchText != null) {
            String pattern = "%" + searchText.toLowerCase() + "%";

            Predicate nameMatches = cb.like(
                    cb.lower(space.get("name")),
                    pattern
            );

            Predicate descriptionMatches = cb.like(
                    cb.lower(space.get("description")),
                    pattern
            );

            Predicate ownerUsernameMatches = cb.like(
                    cb.lower(owner.get("username")),
                    pattern
            );

            predicates.add(cb.or(
                    nameMatches,
                    descriptionMatches,
                    ownerUsernameMatches
            ));
        }

        query.select(space)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.desc(space.get("createdAt")));

        return entityManager.createQuery(query).getResultList();
    }

    private String normalizeSearchText(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        return text.trim();
    }
}
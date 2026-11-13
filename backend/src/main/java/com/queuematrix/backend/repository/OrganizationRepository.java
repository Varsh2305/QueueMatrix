package com.queuematrix.backend.repository;

import com.queuematrix.backend.entity.Organization;
import com.queuematrix.backend.entity.OrganizationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    List<Organization> findByType(OrganizationType type);
}

package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.RegistryRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RegistryRequestRepository extends JpaRepository<RegistryRequest, UUID> {


}

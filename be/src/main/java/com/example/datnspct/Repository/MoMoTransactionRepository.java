package com.example.datnspct.Repository;

import com.example.datnspct.Model.MoMoTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoMoTransactionRepository extends JpaRepository<MoMoTransaction, Integer> {

    Optional<MoMoTransaction> findByOrderId(String orderId);

    Optional<MoMoTransaction> findByRequestId(String requestId);

    Optional<MoMoTransaction> findByTransId(String transId);

    @Query("SELECT mt FROM MoMoTransaction mt WHERE mt.idHD = :idHD")
    Optional<MoMoTransaction> findByIdHD(@Param("idHD") Integer idHD);
}
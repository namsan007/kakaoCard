package com.kakaopay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakaopay.entity.CancelInfoEnty;

/**
 * PaymentInfo repository
 *
 * @author kjy
 * @since Create : 2021. 4. 15
 * @version 1.0
 */
public interface CancelInfoRep extends JpaRepository<CancelInfoEnty, Long> {

	List<CancelInfoEnty> findAllByPayMgnNo(String payMgnNo);
	CancelInfoEnty findByCancelMgnNo(String mgnNo);
	List<CancelInfoEnty> findByCardInfo(String cardInfo);
}


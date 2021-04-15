package com.kakaopay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakaopay.entity.PayInfoEnty;

/**
 * PaymentInfo repository
 *
 * @author kjy
 * @since Create : 2021. 4. 15
 * @version 1.0
 */
public interface PayInfoRep extends JpaRepository<PayInfoEnty, Long> {

	PayInfoEnty findByPayMgnNo(String mgnNo);
	
	List<PayInfoEnty> findByCardInfo(String cardInfo);
}


package com.kakaopay.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakaopay.entity.FullTextEnty;

/**
 * FullTextEnty repository
 *
 * @author kjy
 * @since Create : 2020. 4. 17.
 * @version 1.0
 */
public interface FullTextRep extends JpaRepository<FullTextEnty, Long> {

}


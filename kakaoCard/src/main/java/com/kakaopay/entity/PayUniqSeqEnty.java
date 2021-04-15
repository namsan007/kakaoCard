package com.kakaopay.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 유일 시퀀스 번호 테이블
 *
 * @author kjy
 * @since Create : 2020. 4. 17.
 * @version 1.0
 */
@Entity
@Data
@EqualsAndHashCode(of = "payUniqSeq")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayUniqSeqEnty {

	  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private long payUniqSeq;
	  private Date inputDt;
	  
}

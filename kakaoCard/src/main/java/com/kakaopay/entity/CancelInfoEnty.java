package com.kakaopay.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 취소 가능 금액 entity
 *
 * @author kjy
 * @since Create : 2021. 4. 15
 * @version 1.0
 */
@Entity
@Data
@EqualsAndHashCode(of = "cancelMgnNo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelInfoEnty {

	@Id 
	private String cancelMgnNo;		//관리번호

	private String payMgnNo;		//관리번호
	
	private String cardInfo;		//카드정보
	
	private int payAmt;				//결제_취소금액(100원 이상, 10억원 이하, 숫자)
	  
	private int vatAmt;				//부가가치세(계산해서 생성)
	
	private String payMsg;			//관리번호
	
	private String paySts;			//진행상태 진행중, 완료
	
	private Date inputDt;			//입력일시
	  
}

package com.kakaopay.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.kakaopay.cryp.SHA256Util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 취소 가능 금액 entity
 *
 * @author kjy
 * @since Create : 2020. 4. 17.
 * @version 1.0
 */
@Entity
@Data
@EqualsAndHashCode(of = "payMgnNo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayInfoEnty {

	
	@Id
	private String payMgnNo; // 관리번호

	private String cardInfo; // 카드정보

	private int payAmt; // 결제_취소금액(100원 이상, 10억원 이하, 숫자)

	private int vatAmt; // 부가가치세(계산해서 생성)

	private String payMsg; // 결제/취소 구분 메시지

	private String insMth; // 할부개월수

	private String paySts; // 진행상태 진행중, 완료

	private Date inputDt; // 입력일시

	/**
	 * 카드 정보 (카드번호|유효기간|cvc)
	 * 
	 * @return
	 */
	public String getDeCardNo() {

		String tempArr[] = SHA256Util.decrypt(cardInfo).split("\\|"); // 카드번호 + 유효기간 +cvc 합쳐서 암호화 예) 카드정보|유효기간|cvc
		String cardNo = tempArr[0];

		return cardNo;
	}
	

	/**
	 * 카드 정보 (카드번호|유효기간|cvc)
	 * 
	 * @return
	 */
	public String getCardInfo() {

		return SHA256Util.decrypt(cardInfo);
	}
	
	/**
	 * 암호된 카드 정보 (카드번호|유효기간|cvc)
	 * 
	 * @return
	 */
	public String getEnCardInfo() {

		return cardInfo;
	}		
}

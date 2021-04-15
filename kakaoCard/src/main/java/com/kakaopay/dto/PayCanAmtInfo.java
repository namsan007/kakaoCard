package com.kakaopay.dto;

import lombok.Data;

/**
 * 결제 요청
 *
 * @author kjy
 * @since Create : 2021. 4. 15
 * @version 1.0
 */
@Data
public class PayCanAmtInfo {
		
	private int payAmt;
	
	private int vatAmt;

	public PayCanAmtInfo(int payAmt, int vatAmt){
		this.payAmt = payAmt;
		this.vatAmt = vatAmt;
		
	}
}


package com.kakaopay.dto;

import lombok.Data;

/**
 * 결제 응답
 *
 * @author kjy
 * @since Create : 2020. 4. 17.
 * @version 1.0
 */
@Data
public class CardInfo {

	private String cardNo;
	private String extMmYy;
	private String cvcNo;
	
	
	public CardInfo(String cardInfoStr) {
		
		String tempStr 	= (cardInfoStr);
		String tempCdNo	= tempStr.split("\\|")[0];
		this.extMmYy	= tempStr.split("\\|")[1];
		this.cvcNo		= tempStr.split("\\|")[2];
		
		this.cardNo = getCardNoMaskStr(tempCdNo);
	}
	  
	
	public String getCardNoMaskStr(String tempCdNo) {
		String rtnStr 			= "";
		int cardLen 			= tempCdNo.length();
		int tempLen 			= 0;
		int cardFrontLen 		= 6;		//앞6자리
		int cardMidLen 			= 0;		//가운데 자리수
		int cardLastLen 		= 3;		//뒤3자리
		tempLen 	= cardLen - cardFrontLen;
		cardMidLen 	= tempLen - cardLastLen;
		
		String frontStr	= tempCdNo.substring(0,cardFrontLen);
//		String midStr	= tempCdNo.substring(cardFrontLen, cardFrontLen+cardMidLen); 
		String lastStr	= tempCdNo.substring(cardFrontLen+cardMidLen, cardLen);
		
		String midStr = "";
		for(int i=0; i<cardMidLen; i++) {
			midStr += "*";
		}
		rtnStr = frontStr + midStr + lastStr;
		return rtnStr;
		
	}	
	  
}

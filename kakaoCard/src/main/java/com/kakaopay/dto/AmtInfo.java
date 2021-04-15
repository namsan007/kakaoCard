package com.kakaopay.dto;

import java.util.List;

import com.kakaopay.entity.CancelInfoEnty;
import com.kakaopay.entity.PayInfoEnty;

import lombok.Data;

/**
 * 결제 요청
 *
 * @author kjy
 * @since Create : 2020. 4. 17.
 * @version 1.0
 */
@Data
public class AmtInfo {
		
	private int totalAmt;
	
	private int payAmt;
	
	private int vatAmt;
	
	public AmtInfo(){
		super();
	}
	//DB결제금액 정보
	public AmtInfo(PayInfoEnty payInfoEnty){
		this.payAmt = payInfoEnty.getPayAmt();
		this.vatAmt = payInfoEnty.getVatAmt();
		this.totalAmt = payAmt +vatAmt;
		
	}
	
	//DB결제금액 - DB취소금액 정보
	public AmtInfo(PayInfoEnty payInfoEnty, List<CancelInfoEnty> cancelInfoEntyList){
		
		AmtInfo tepAmtInfo1 = new AmtInfo();
		AmtInfo tepAmtInfo2 = new AmtInfo();
		
		tepAmtInfo1 		= getAmtInfo(payInfoEnty);
		tepAmtInfo2 		= getAmtInfo(cancelInfoEntyList);
		
		this.payAmt 		= tepAmtInfo1.getPayAmt() - tepAmtInfo2.getPayAmt();
		this.vatAmt 		= tepAmtInfo1.getVatAmt() - tepAmtInfo2.getVatAmt();
		this.totalAmt 		= tepAmtInfo1.getTotalAmt() - tepAmtInfo2.getTotalAmt();

	}
	//DB결제금액 - DB취소금액 , 요청취소금액
	public AmtInfo(PayInfoEnty payInfoEnty, List<CancelInfoEnty> cancelInfoEntyList, CancelReqDto cancelReqDto) {	
		
		AmtInfo tepAmtInfo1 = new AmtInfo();
		AmtInfo tepAmtInfo2 = new AmtInfo();
		
		
		tepAmtInfo1 		= getAmtInfo(payInfoEnty);
		tepAmtInfo2 		= getAmtInfo(cancelInfoEntyList,cancelReqDto);
		
		this.payAmt 		= tepAmtInfo1.getPayAmt() - tepAmtInfo2.getPayAmt();
		this.vatAmt 		= tepAmtInfo1.getVatAmt() - tepAmtInfo2.getVatAmt();
		this.totalAmt 		= tepAmtInfo1.getTotalAmt() - tepAmtInfo2.getTotalAmt();
		
		if(payAmt<0) { payAmt =0 ;}
		if(vatAmt<0) { vatAmt =0 ;}
		if(totalAmt<0) { totalAmt =0 ;}
		
	}		

	//DB취소금액 + 요청취소금액
	public AmtInfo(List<CancelInfoEnty> cancelInfoEntyList, CancelReqDto cancelReqDto) {	
		AmtInfo tepAmtInfo1 = new AmtInfo();
		tepAmtInfo1 		= getAmtInfo(cancelInfoEntyList,cancelReqDto);
		this.payAmt 		= tepAmtInfo1.getPayAmt();
		this.vatAmt 		= tepAmtInfo1.getVatAmt();
		
		if(payAmt<0) { payAmt =0 ;}
		if(vatAmt<0) { vatAmt =0 ;}
		if(totalAmt<0) { totalAmt =0 ;}		
	}	
	
	
	public AmtInfo getAmtInfo(PayInfoEnty payInfoEnty) {
		
		AmtInfo rtnAmtInfo = new AmtInfo();
		int payAmt 		= 0;								
		int vatAmt 		= 0;
		
		if(payInfoEnty!=null) {
			payAmt 		= payInfoEnty.getPayAmt();								
			vatAmt 		= payInfoEnty.getVatAmt();
		}
		
		rtnAmtInfo.setTotalAmt(payAmt +vatAmt);
		rtnAmtInfo.setPayAmt(payAmt);
		rtnAmtInfo.setVatAmt(vatAmt);
		
		return rtnAmtInfo;		
	}
	
	//총 취소금액 정보 메소드
	public AmtInfo getAmtInfo(List<CancelInfoEnty> cancelInfoEntyList) {
		
		AmtInfo rtnAmtInfo = new AmtInfo();
		CancelInfoEnty cancelInfoEnty = null;
		int payAmt = 0;
		int vatAmt = 0;
		
		if(cancelInfoEntyList!=null) {
			for(int i=0; i < cancelInfoEntyList.size(); i++) {
				
				cancelInfoEnty = null;
				cancelInfoEnty = new CancelInfoEnty();
				cancelInfoEnty = cancelInfoEntyList.get(i);
				
				payAmt += cancelInfoEnty.getPayAmt();
				vatAmt += cancelInfoEnty.getVatAmt();
				
			}
		}
		rtnAmtInfo.setTotalAmt(payAmt +vatAmt);
		rtnAmtInfo.setPayAmt(payAmt);
		rtnAmtInfo.setVatAmt(vatAmt);
		
		return rtnAmtInfo;
	}	
	
	
	public AmtInfo getAmtInfo(CancelReqDto cancelReqDto) {
		
		AmtInfo rtnAmtInfo = new AmtInfo();
		int payAmt 		= cancelReqDto.getPayAmt();								
		int vatAmt 		= cancelReqDto.getVatAmt();
		
		rtnAmtInfo.setTotalAmt(payAmt +vatAmt);
		rtnAmtInfo.setPayAmt(payAmt);
		rtnAmtInfo.setVatAmt(vatAmt);
		
		return rtnAmtInfo;		
	}
	
	
	//총 취소금액 정보 메소드
	public AmtInfo getAmtInfo(List<CancelInfoEnty> cancelInfoEntyList, CancelReqDto cancelReqDto) {
		
		AmtInfo rtnAmtInfo 	= new AmtInfo();
		AmtInfo tepAmtInfo1 = new AmtInfo();
		AmtInfo tepAmtInfo2 = new AmtInfo();
		
		tepAmtInfo1 		= getAmtInfo(cancelInfoEntyList);
		tepAmtInfo2 		= getAmtInfo(cancelReqDto);
		
		rtnAmtInfo.setPayAmt(tepAmtInfo1.getPayAmt() 		+ tepAmtInfo2.getPayAmt());	
		rtnAmtInfo.setVatAmt(tepAmtInfo1.getVatAmt() 		+ tepAmtInfo2.getVatAmt());	
		rtnAmtInfo.setTotalAmt(tepAmtInfo1.getTotalAmt()  	+ tepAmtInfo2.getTotalAmt());

		return rtnAmtInfo;
		
	}

}


package com.kakaopay.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.kakaopay.cryp.SHA256Util;
import com.kakaopay.dto.AmtInfo;
import com.kakaopay.dto.CancelReqDto;
import com.kakaopay.dto.CancelResDto;
import com.kakaopay.dto.CardInfo;
import com.kakaopay.dto.PayCanAmtInfo;
import com.kakaopay.dto.PayReqDto;
import com.kakaopay.dto.PayResDto;
import com.kakaopay.dto.SearchReqDto;
import com.kakaopay.dto.SearchResDto;
import com.kakaopay.entity.CancelInfoEnty;
import com.kakaopay.entity.FullTextEnty;
import com.kakaopay.entity.PayInfoEnty;
import com.kakaopay.entity.PayUniqSeqEnty;
import com.kakaopay.exception.CancelFailExcn;
import com.kakaopay.exception.CstsPayExcn;
import com.kakaopay.exception.NoHavResultExcn;
import com.kakaopay.repository.CancelInfoRep;
import com.kakaopay.repository.FullTextRep;
import com.kakaopay.repository.PayInfoRep;
import com.kakaopay.repository.PayUniqSeqEntyRep;

/**
 * 결제 처리 service
 *
 * @author kjy
 * @since Create : 2020. 4. 17.
 * @version 1.0
 */
@Service
public class KakaoCardServiceImpl implements KakaoCardService {
	
	private final PayInfoRep payInfoRep;
	private final CancelInfoRep cancelInfoRep;
	private final FullTextRep fullTextRep;
	private final PayUniqSeqEntyRep payUniqSeqEntyRep;
	
	public KakaoCardServiceImpl(PayInfoRep payInfoRep
						 ,CancelInfoRep cancelInfoRep
						 ,PayUniqSeqEntyRep payUniqSeqEntyRep
						 ,FullTextRep fullTextRep
						 ) 
	{
		this.payInfoRep 		= payInfoRep;
		this.cancelInfoRep 		= cancelInfoRep;
		this.payUniqSeqEntyRep 	= payUniqSeqEntyRep;
		this.fullTextRep 		= fullTextRep;
	}

	
	
	@Transactional
	@Override
	public PayResDto payment(PayReqDto payReqDto) {	//결제 API

		PayResDto payResDto 	= new PayResDto();

		Date dt = new Date();
		//1-2 validate체크 처리
		payReqDto.vatAmtValdate();

		List<PayInfoEnty> payInfoEntyList	= payInfoRep.findByCardInfo(payReqDto.getCardInfoAddStr());	//취소 관리번호로 원결제 관리번호 조회
		PayInfoEnty payInfoEnty = null;

		if(payInfoEntyList!=null) {
			for(int i=0; i < payInfoEntyList.size(); i++) {
				
				payInfoEnty = null;
				payInfoEnty = new PayInfoEnty();
				payInfoEnty = payInfoEntyList.get(i);
				
				if(payInfoEnty!=null) {
					if(payInfoEnty.getPaySts().equals("진행중")) {
						throw new CstsPayExcn("하나의 카드번호로 동시에 결제를 할 수 없습니다.");	
					}
				}
				
			}
		}

		
		//2 테이블에 결제데이타 입력 요청 (2-1,2-2,2-3,2-3-1)
		String mgnNo 		= makeMgnNo(dt);						//관리번호 최초 생성
		String cardInfoStr 	= payReqDto.getCardInfoAddStr();		//암호화된 데이터
		String fullText 	= makeFullText("PAYMENT",mgnNo
											,payReqDto.getCardNo()					,payReqDto.getInsMth()					,payReqDto.getExtMmYy()	,payReqDto.getCvcNo()
											,String.valueOf(payReqDto.getPayAmt())	,String.valueOf(payReqDto.getVatAmt())	,""						,cardInfoStr
											);
		
		//---------------------------------------INSERT START---------------------------
		//3. 테이블 INSERT -> 납입 테이블 , 전문 테이블
		insertPayInfoEnty(payReqDto, cardInfoStr, dt , mgnNo , payReqDto.getPayMsg(),"진행중");					
		insertFullTextEnty(fullText, dt, mgnNo);
		//---------------------------------------INSERT END---------------------------

		//4. 리턴될값 셋팅
		payResDto = getPayResDto(payResDto,payReqDto,mgnNo);		//공통적으로 리턴하는 항목에 대해세 method처리 그외 하단에서 처리
		insertPayInfoEnty(payReqDto, cardInfoStr, dt , mgnNo , payReqDto.getPayMsg(),"완료");
		return payResDto;
	}
	
	@Transactional
	@Override
	public CancelResDto cancel(CancelReqDto cancelReqDto) {	//취소 API
		CancelResDto cancelResDto = new CancelResDto();
		Date dt = new Date();
		//1-2 validate체크 처리
		cancelReqDto.vatAmtValdate();
		
		//2 관리번호로 결제테이블 , 취소테이블 조회
		//---------------------------------------취소 관리번호로 결제 테이블 조회 START---------------------------
		PayInfoEnty payInfoEnty		= payInfoRep.findByPayMgnNo(cancelReqDto.getCancelMgnNo());	//취소 관리번호로 원결제 관리번호 조회
		//---------------------------------------취소 관리번호로 결제 테이블 조회 END---------------------------
		
		//2. 관리번호로 결제테이블 , 취소테이블  조회 결과 없는경우 리턴
		if(payInfoEnty==null) {
			throw new CancelFailExcn("관리번호 : [" +cancelReqDto.getCancelMgnNo() +"] 으로 조회된 결제 정보가 없습니다.");
		}
		
		List<CancelInfoEnty> cancelInfoEntyList	= cancelInfoRep.findByCardInfo(payInfoEnty.getEnCardInfo());		//취소 관리번호로 원결제 관리번호 조회
		CancelInfoEnty cancelInfoEnty 			= null;
		if(cancelInfoEntyList!=null) {
			for(int i=0; i < cancelInfoEntyList.size(); i++) {
				
				cancelInfoEnty = null;
				cancelInfoEnty = new CancelInfoEnty();
				cancelInfoEnty = cancelInfoEntyList.get(i);
				
				if(cancelInfoEnty!=null) {
					if(cancelInfoEnty.getPaySts().equals("진행중")) {
						throw new CstsPayExcn("결제 한 건에 대한 전체,부분 취소를 동시에 할 수 없습니다.");	
					}
				}
				
			}
		}
		
		String mgnNo 		= makeMgnNo(dt);													//관리번호 최초 생성
		//3	취소테이블의 SUM(취소금액), SUM(부가가치세) 합계를 구한다		
		//4. 결제금액, 부가가치세금액 validata 체크하여 에러리턴
		cancelResDto = getCancelResDto(cancelResDto,cancelReqDto,payInfoEnty,mgnNo);			//공통적으로 리턴하는 항목에 대해세 method처리 그외 하단에서 처리
		
		//---------------------------------------INSERT START---------------------------
		//5. 테이블에 취소, 전문 데이타 입력 요청
		//5-1 입력 형태로 데이터 변환    카드번호 + 유효기간 +cvc 합쳐서 암호화    예) 카드정보|유효기간|cvc
		String cardInfoStr 	= payInfoEnty.getCardInfo();	//복호화 처리
		// 구분자 "|"으로 합쳐져 있는 데이타를 구분한다
		String tempArr[] = cardInfoStr.split("\\|");							//카드번호 + 유효기간 +cvc 합쳐서 암호화 예) 카드정보|유효기간|cvc
		
		String cardNo		= tempArr[0];
		String extMmYy		= tempArr[1];
		String cvcNo		= tempArr[2];
		
		//5-2,5-2-1,5-2-2 Head Body
		String fullText 	= makeFullText("CANCEL"									,mgnNo
											,cardNo									,payInfoEnty.getInsMth()				,extMmYy					,cvcNo
											,String.valueOf(payInfoEnty.getPayAmt()),String.valueOf(payInfoEnty.getVatAmt()),payInfoEnty.getPayMgnNo()	,cardInfoStr);
				
		insertCancelInfoEnty(cancelReqDto, payInfoEnty.getEnCardInfo(), dt , mgnNo,cancelReqDto.getPayMsg(),"진행중");			//취소테이블					
		insertFullTextEnty(fullText, dt, mgnNo);						//전문테이블
		//---------------------------------------INSERT END---------------------------
		insertCancelInfoEnty(cancelReqDto, payInfoEnty.getEnCardInfo(), dt , mgnNo,cancelReqDto.getPayMsg(),"완료");			//취소테이블
		return cancelResDto;
	}	
	
	@Override
	public SearchResDto search(SearchReqDto searchReqDto) {

		SearchResDto resDto				 		= new SearchResDto();
		PayInfoEnty payInfoEnty					= payInfoRep.findByPayMgnNo(searchReqDto.getMgnNo());		//관리번호로 원결제 관리번호 조회
		CancelInfoEnty cancelInfoEnty			= cancelInfoRep.findByCancelMgnNo(searchReqDto.getMgnNo());	//관리번호로 취소 관리번호 조회
		PayCanAmtInfo payCanAmtInfo				= null;
		AmtInfo payAmtInfo 	  					= null;
		String wonMgnNo							= "";														//원장 관리번호
		String payMsg							= "";

		if(payInfoEnty==null && cancelInfoEnty==null) {
			throw new NoHavResultExcn("관리번호 : [" +searchReqDto.getMgnNo() +"] 으로 조회된 결제 정보가 없습니다.");
		}else {
			
			resDto.setMgnNo(searchReqDto.getMgnNo());
			
			//원관리번호로 결제, 취소된 내역이 있는지 조회
			if(payInfoEnty!=null && cancelInfoEnty==null) {											//결제테이블에 결과가 있는경우
				wonMgnNo 	  = searchReqDto.getMgnNo();
				payMsg		  = payInfoEnty.getPayMsg();
				payCanAmtInfo = new PayCanAmtInfo(payInfoEnty.getPayAmt(), payInfoEnty.getVatAmt());
				
			}else {																					//취소테이블에 결과가 있는경우		
				wonMgnNo 	  = cancelInfoEnty.getPayMgnNo();										//취소테이블의 원결제 관리번호
				payMsg		  = cancelInfoEnty.getPayMsg();	
				payCanAmtInfo = new PayCanAmtInfo(cancelInfoEnty.getPayAmt(), cancelInfoEnty.getVatAmt());
				payInfoEnty	  = payInfoRep.findByPayMgnNo(wonMgnNo);								//원결제 관리번호로 원결제 정보조회
			}
			
			List<CancelInfoEnty> cancelInfoEntyList = cancelInfoRep.findAllByPayMgnNo(wonMgnNo);	//취소 관리번호로 그동안 취소되었던 내역조회
			payAmtInfo	  = new AmtInfo(payInfoEnty,cancelInfoEntyList);							//원결제테이블금액과 취소금액의 합계
			resDto.setCardInfo(new CardInfo(payInfoEnty.getCardInfo()));
			resDto.setPayMsg(payMsg);
			resDto.setPayCanAmtInfo(payCanAmtInfo);
			resDto.setAmtInfo(payAmtInfo);
		}
	
		return resDto;
	}
	
	/**
	 * @return
	 */
	private void insertPayInfoEnty(PayReqDto payReqDto, String cardInfoStr,Date dt, String mgnNo, String payMsg, String paySts) {		//납입테이블 INSERT
		

		payInfoRep.save(PayInfoEnty.builder()		 				//결제 API INSERT
								   .payMgnNo(mgnNo)					//관리번호
								   .cardInfo(cardInfoStr)			//카드정보
								   .payAmt(payReqDto.getPayAmt())	//결제_취소금액(100원 이상, 10억원 이하, 숫자)	
								   .vatAmt(payReqDto.getVatAmt())	//부가가치세(계산해서 생성)
								   .payMsg(payMsg)					//"결제", "취소"
								   .insMth(payReqDto.getInsMth())	//할부개월수
								   .paySts(paySts)					//진행상태 진행중, 완료
								   .inputDt(dt)
								   .build()
						);
		
	}
	
	/**
	 * @return
	 */
	private void insertCancelInfoEnty(CancelReqDto cancelReqDto,String cardInfoStr,Date dt, String mgnNo,String payMsg, String paySts) {		//납입테이블 INSERT
		
		cancelInfoRep.save(CancelInfoEnty.builder()									//결제 API INSERT
								   		 .cancelMgnNo(mgnNo)						//새로 생성된 관리번호
								   		 .payMgnNo(cancelReqDto.getCancelMgnNo())	//취소관리번호
									     .cardInfo(cardInfoStr)			//카드정보
								   		 .payAmt(cancelReqDto.getPayAmt())			//결제_취소금액(100원 이상, 10억원 이하, 숫자)	
								   		 .vatAmt(cancelReqDto.getVatAmt())			//부가가치세(계산해서 생성)
								   		 .payMsg(payMsg)							//"결제", "취소"
								   		 .paySts(paySts)							//진행상태 진행중, 완료
								   		 .inputDt(dt)
								   		 .build()	
						);
		
	}		
	
	/**
	 * yyMMddHHmmss 12자리
	 * 4자리 난수
	 * 4자리 순번
	 * @return
	 */
	private void insertFullTextEnty(String fullText,Date dt, String mgnNo) {		//납입테이블 INSERT
		
		fullTextRep.save(FullTextEnty.builder()						//전문데이타 Insert
								   .mgnNo(mgnNo)					//관리번호
								   .fullText(fullText)				//결제String			
								   .inputDt(dt)
								   .build()
						);
		
	}	
	
	private String makeMgnNo(Date dt) {	//관리번호 생성
		//-----------------------------관리번호 위한 순번 생성 START---------------------------------
		PayUniqSeqEnty payUniqSeqEnty = new PayUniqSeqEnty();
		payUniqSeqEnty.setInputDt(dt);
		payUniqSeqEntyRep.save(payUniqSeqEnty);
		long seq 			= payUniqSeqEnty.getPayUniqSeq();
		//-----------------------------관리번호 위한 순번 생성 END---------------------------------		
		
		
		//-----------------------------관리번호 최초 생성 START---------------------------------
		StringBuilder mgnNo = new StringBuilder();
		Random rand 		= new Random();
		
		String tempStr1   	= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));	//12자리
		String tempStr2   	= StringUtils.leftPad(String.valueOf(rand.nextInt(100)),2,"0");				//2자리
		String tempStr3   	= StringUtils.leftPad(String.valueOf(seq), 6, "0");							//6자리
		mgnNo.append(tempStr1).append(tempStr2).append(tempStr3);										//20자리(size)
		
		//-----------------------------관리번호 최초 생성 END---------------------------------
		return mgnNo.toString();
	}
	private String makeFullText(String payMsg	,String newMgnNo	
			 				   ,String cardNo   ,String insMth	,String extMmYy		,String cvcNo
							   ,String payAmt	,String vatAmt	,String payMgnNo    ,String cardInfoStr) {	//전문 전체 내용 생성 450
		
		String enCardInfoStr = SHA256Util.encrypt(cardInfoStr);
		String hdData0 	= lPad("450",4,"");				//0. 데이터 길이			4   Left 	"" 
		String hdData1 	= rPad(payMsg,10,"");			//1. 데이터 구분			10 	right 	""	
		String hdData2 	= rPad(newMgnNo,20,"");			//2. 관리번호				20	right 	""	
		
		String bdData0 	= rPad(cardNo,20,"");			//0. 카드번호 				20	right 	""
		String bdData1 	= lPad(insMth,2,"0");			//1. 할부개월수			2	Left 	"0"
		String bdData2 	= rPad(extMmYy,4,"");			//2. 카드유효기간			4	right 	""
		String bdData3 	= rPad(cvcNo,3,"");				//3. cvc				3	right 	""
		String bdData4 	= lPad(payAmt,10,"");			//4. 거래금액				10	Left 	""
		String bdData5 	= lPad(vatAmt,10,"0");			//5. 부가가치세			10	Left 	"0"
		String bdData6 	= rPad(payMgnNo,20,"");			//6. 원거래관리번호			20	right 	""
		String bdData7 	= rPad(enCardInfoStr,300,"");	//7. 암호화된카드정보		300	right 	""
		String bdData8 	= rPad("",47,"");				//8. 예비번호				47	right 	""
		
		StringBuilder fullTextSb = new StringBuilder();	
		fullTextSb.append(hdData0).append(hdData1).append(hdData2)
				  .append(bdData0).append(bdData1).append(bdData2)
				  .append(bdData3).append(bdData4).append(bdData5)
				  .append(bdData6).append(bdData7).append(bdData8);
		
		return fullTextSb.toString();
	}	
	
	private PayResDto getPayResDto(PayResDto resDto, PayReqDto payReqDto, String mgnNo) {	
  
		//4. ----------------------리턴될값 셋팅 START----------------------
		StringBuilder msgSb = new StringBuilder();	//결과 자세한 안내 메시지
		//관리번호 : [12345678901234567890] 20,000(2,000)원 결제 성공 
		//관리번호의 총 결제금액 - (총 결제금액 + 총 부가세금액)  22,000 = (20,000 + 2,000)원
		msgSb.append("")
			 .append("관리번호 : [")	.append(mgnNo)	.append("] ")
			 .append(payReqDto.getPayAmtStr())		.append("(")	.append(payReqDto.getVatAmtStr())		.append(")원 ")
			 .append("결제 성공");
		
		resDto.setMgnNo(mgnNo);
		resDto.setRsltStsMsg("성공");
		resDto.setMsg(msgSb.toString());		
		
		return resDto;
		//   ----------------------리턴될값 셋팅 END----------------------
		
	}
	
	private CancelResDto getCancelResDto(CancelResDto resDto, CancelReqDto cancelReqDto,PayInfoEnty payInfoEnty, String mgnNo) {	
		
		//---------------------------------------취소 관리번호로 취소 테이블 조회 START---------------------------
		List<CancelInfoEnty> cancelInfoEntyList = cancelInfoRep.findAllByPayMgnNo(cancelReqDto.getCancelMgnNo());	//취소 관리번호로 그동안 취소되었던 내역조회
		//---------------------------------------취소 관리번호로 취소 테이블 조회 END---------------------------
		
		AmtInfo payAmtInfo 	  = new AmtInfo(payInfoEnty);									//결제 테이블의 금액
		AmtInfo cancelAmtInfo = new AmtInfo(cancelInfoEntyList,cancelReqDto);				//취소 테이블의 금액 + 취소요청 금액
		AmtInfo resultAmtInfo = new AmtInfo(payInfoEnty,cancelInfoEntyList,cancelReqDto);	//결제금액-취소금액
		
		amtErrCheck( payAmtInfo,cancelAmtInfo , resultAmtInfo);									//4. 결제금액, 부가가치세금액 validata 체크하여 에러리턴
		
		StringBuilder msgSb = new StringBuilder();	//결과 자세한 안내 메시지
		//관리번호 : [12345678901234567890] 20,000(2,000)원 결제 성공 
		//관리번호의 총 결제금액 - (총 결제금액 + 총 부가세금액)  22,000 = (20,000 + 2,000)원
		msgSb.append("")
			 .append("관리번호 : [")	.append(mgnNo)	.append("] ")
			 .append(cancelReqDto.getPayAmtStr())		.append("(")	.append(cancelReqDto.getVatAmtStr())		.append(")원 ")
			 .append("취소 성공")
			 .append("  관리번호의 취소후 남은 총 결제금액 = (총 결제금액 + 총 부가세금액) ")
			 .append(resultAmtInfo.getTotalAmt())	.append(" = (")	.append(resultAmtInfo.getPayAmt())	.append(" + ")	.append(resultAmtInfo.getVatAmt())	.append(")원");
		
		
		resDto.setMgnNo(mgnNo);
		resDto.setResPayAmtTot(resultAmtInfo.getTotalAmt());
		resDto.setResPayAmt(resultAmtInfo.getPayAmt());
		resDto.setResVatAmt(resultAmtInfo.getVatAmt());
		resDto.setRsltStsMsg("성공");
		resDto.setMsg(msgSb.toString());		
		
		return resDto;
		//   ----------------------리턴될값 셋팅 END----------------------
		
	}
	
	
	private String lPad(String temp1, int size , String temp2) {
		return StringUtils.leftPad(temp1, size, temp2);	
	}
	private String rPad(String temp1, int size , String temp2) {
		return StringUtils.rightPad(temp1, size, temp2);	
	}	
	
	
	//payAmtInfo 	결제 테이블의 금액
	//cancelAmtInfo 취소 테이블의 금액 + 취소요청 금액
	//resultAmtInfo 결제금액-취소금액
	private void amtErrCheck(AmtInfo payAmtInfo , AmtInfo cancelAmtInfo, AmtInfo resultAmtInfo) {
		
		if(payAmtInfo.getTotalAmt()<cancelAmtInfo.getTotalAmt()) {
			throw new CancelFailExcn("취소 총금액이 남은 총 금액 보다 큰경우.");
		}else if(payAmtInfo.getPayAmt()<cancelAmtInfo.getPayAmt()) {
			throw new CancelFailExcn("취소 금액이 남은 결제 금액 보다 큰경우.");
		}else if(payAmtInfo.getVatAmt()<cancelAmtInfo.getVatAmt()) {
			if(payAmtInfo.getVatAmt()>1000) {
				throw new CancelFailExcn("취소부가세 금액이 남은 결제 부가세금액 보다 큰경우.");
			}
			
		}
	}

	
}

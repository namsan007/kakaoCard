package com.kakaopay.dto;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.kakaopay.cryp.SHA256Util;
import com.kakaopay.exception.PayReqValidateExcn;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 결제 요청
 *
 * @author kjy
 * @since Create : 2020. 4. 17.
 * @version 1.0
 */
@Data
@EqualsAndHashCode(of = "cardNo")
public class PayReqDto {
	/**
	 * 카드번호 카드번호(10 ~ 16자리 숫자) 길이 : 16
	 * 
	 * @return
	 */
	@NotNull
	@Pattern(regexp = "^[0-9]{10,16}$", message = "10~16자리의 숫자만 입력 가능")
	private String cardNo;

	/**
	 * 유효기간 유효기간(4자리 숫자, mmyy) 길이 : 4
	 * 
	 * @return
	 */
	@NotNull
	@Pattern(regexp = "^(0[1-9]|1[0-2])([0-9]{2})$", message = "4자리의 숫자만 입력가능")
	private String extMmYy;

	/**
	 * cvc cvc(3자리 숫자) 길이 : 3
	 * 
	 * @return
	 */
	@NotNull
	@Pattern(regexp = "^[0-9]{3}$", message = "3자리의 숫자만 입력가능")
	private String cvcNo;

	/**
	 * insMth insMth 할부개월수 : 0(일시불), 1 ~ 12 문자로 입력 받은 후 0~12사이인지 한번더 체크 길이 : 2
	 * 
	 * @return
	 */
	@Min(0)
	@Max(12)
	private Integer insMth;

	@Min(100)
	@Max(1_000_000_000)
	private Integer payAmt;

	@Min(0)
	@Max(99_000_000)
	private Integer vatAmt;

	DecimalFormat fm = new DecimalFormat("###,###");

	
	/**
	 * 부가세가 빈값인 경우 계산 round(결제금액 / 11)
	 * 
	 * @return
	 */
	public int getVatAmt() {
		if (vatAmt == null) {
			vatAmt = 0;
		}
		if (vatAmt == 0) {
			if(payAmt==1000) {
				vatAmt = 91;
			}else {
				vatAmt = (int) Math.round(Double.valueOf(payAmt) / 11);
			}
		}
		
		return vatAmt;
	}

	/**
	 * 부가세 검증 ROUNDUP(결제금액 / 11)< 부가가치세금액"
	 * 
	 * @return
	 */
	public void vatAmtValdate() {

		
		int tempInt = 0; // 정상적인 부가세 금액

		if (vatAmt != null && payAmt != null) {
			tempInt = getVatAmt();

			if (tempInt != vatAmt) { // 계산된 부가가치세 금액과 입력된 부가세 금액이 같지 않은경우 오류
				if (payAmt == 1000 && vatAmt == 0) { // 결제금액이 1000원이고 부가세가 0원인경우 허용
				} else {
					throw new PayReqValidateExcn("부가가치세 금액은 (결제금액의 /11)의 소수점 반올림한 금액 입니다");

				}
			}

		} else if (vatAmt == 0) {
			this.vatAmt = getVatAmt();
		}
		
		extMmYyCheck();//유효기간체크
	}

	/**
	 * 할부개월 한자리인경우 2자리로 변환 "1" -> "01",숫자->문자
	 * 
	 * @return
	 */
	public String getInsMth() {
		return StringUtils.leftPad(String.valueOf(insMth), 2, "0");
	}

	/**
	 * 카드 정보 (카드번호|유효기간|cvc)
	 * 
	 * @return
	 */
	public String getCardInfoAddStr() {
		return SHA256Util.encrypt(String.join("|", cardNo, extMmYy, cvcNo));
	}
	/**
	 * 카드 정보 (카드번호|유효기간|cvc)
	 * 
	 * @return
	 */
	public String getEnCardNo() {
		return SHA256Util.encrypt(cardNo);
	}

	/**
	 * 결제 금액 000,000 포멧 리턴
	 * 
	 * @return
	 */
	public String getPayAmtStr() {
		return fm.format(payAmt);
	}

	/**
	 * 취소 금액 000,000 포멧 리턴
	 * 
	 * @return
	 */
	public String getVatAmtStr() {
		return fm.format(vatAmt);
	}

	/**
	 * 상태
	 * 
	 * @return
	 */
	public String getPayMsg() {
		return "결제";
	}
	
	public void extMmYyCheck() {
		LocalDateTime localDateTime = LocalDateTime.now();
		String mm = extMmYy.substring(0,2);
		String yy = extMmYy.substring(2,4);
		
		int yymmCard = Integer.parseInt(yy+mm);
		int yymmNow = Integer.parseInt(localDateTime.format(DateTimeFormatter.ofPattern("YYMM")));
		if(yymmNow>yymmCard) {
			throw new PayReqValidateExcn("유효기간이 지난 카드 입니다.");
		}
	}
	
}

package com.kakaopay.dto;

import java.text.DecimalFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.kakaopay.exception.PayReqValidateExcn;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 결제 요청
 *
 * @author kjy
 * @since Create : 2021. 4. 15
 * @version 1.0
 */
@Data
@EqualsAndHashCode(of = "cancelMgnNo")
public class CancelReqDto {

  
  /**
   * 관리번호
   * 관리번호(unique id, 20자리)
   * 길이 : 20
   * @return
   */	
  @NotNull
  @Pattern(regexp = "^[0-9]{20}$", message = "관리번호는 20자리의 숫자만 입력이 가능합니다")
  private String cancelMgnNo;
  

  @Min(100)
  @Max(1_000_000_000)
  private Integer payAmt;
  
  
  @Min(0)
  @Max(99_000_000)
  private Integer vatAmt;


  DecimalFormat fm 		= new DecimalFormat("###,###");
  
  /**
   * 부가세가 빈값인 경우 계산
   * round(결제금액 / 11) 
   * @return
   */
  public int getVatAmt() {
	if(vatAmt == null) {
		vatAmt = 0;
	}
    if (vatAmt == 0) {
    	vatAmt = (int) Math.round(Double.valueOf(payAmt) / 11);
    }
    return vatAmt;
  }

	/**
	 * 부가세 검증
	 * ROUNDUP(결제금액 / 11)< 부가가치세금액" 
	 * @return
	 */
	public void vatAmtValdate() {
		
		int tempInt = 0;	//정상적인 부가세 금액
		
		if (vatAmt != null && payAmt != null) {
			tempInt = getVatAmt();
	
			if (tempInt != vatAmt) {	//계산된 부가가치세 금액과 입력된 부가세 금액이 같지 않은경우 오류
				if(payAmt==1000 && vatAmt==0) {		//결제금액이 1000원이고 부가세가 0원인경우 허용
				}else {
					throw new PayReqValidateExcn("부가가치세 금액은 (결제금액의 /11)의 소수점 반올림한 금액 입니다");	
					
				}
			}
	
		}else if (vatAmt == 0) {
			this.vatAmt = getVatAmt();
		}
		
	}
  
  /**
   * 결제 금액 000,000 포멧 리턴
   * @return
   */
  public String getPayAmtStr() {
	  return fm.format(payAmt);
  }
  
  
  /**
   * 취소 금액 000,000 포멧 리턴
   * @return
   */
  public String getVatAmtStr() {
	  return fm.format(vatAmt);
  }
  
  /**
  * 상태
  * @return
  */
  public String getPayMsg() {
	  return "취소";
  }
  
}


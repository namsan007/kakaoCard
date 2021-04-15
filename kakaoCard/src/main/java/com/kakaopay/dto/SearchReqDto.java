package com.kakaopay.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
@EqualsAndHashCode(of = "mgnNo")
public class SearchReqDto {

  /**
   * 관리번호
   * 관리번호(unique id, 20자리)
   * 길이 : 20
   * @return
   */	
  @NotNull
  @Pattern(regexp = "^[0-9]{20}$", message = "관리번호는 20자리의 숫자만 입력이 가능합니다")
  private String mgnNo;
  

}


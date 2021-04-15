package com.kakaopay.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 결제 응답
 *
 * @author kjy
 * @since Create : 2021. 4. 15
 * @version 1.0
 */
@Getter
@Setter
@EqualsAndHashCode(of = "mgnNo")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PayResDto {

	private String mgnNo;
	private String rsltStsMsg;
	private String msg;

}

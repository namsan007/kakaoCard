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
 * 결제_취소 전문
 * 
 * @author kjy
 * @since Create : 2021. 4. 15
 * @version 1.0
 */
@Entity
@Data
@EqualsAndHashCode(of = "mgnNo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FullTextEnty {

	@Id 
	private String mgnNo;		//관리번호
	
	private String fullText;	//결제전문,취소전문정보의 관리번호 20자리 문자
	  
	private Date inputDt;		//입력일시
	  
}

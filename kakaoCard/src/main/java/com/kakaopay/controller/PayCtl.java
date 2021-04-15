package com.kakaopay.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kakaopay.dto.CancelReqDto;
import com.kakaopay.dto.CancelResDto;
import com.kakaopay.dto.PayReqDto;
import com.kakaopay.dto.PayResDto;
import com.kakaopay.dto.SearchReqDto;
import com.kakaopay.dto.SearchResDto;
import com.kakaopay.exception.NoHavResultExcn;
import com.kakaopay.exception.PayReqValidateExcn;
import com.kakaopay.service.KakaoCardService;

/**
 * 결제 처리 api
 *
 * @author kjy
 * @since Create : 2020. 4. 17.
 * @version 1.0
 */
@RestController
public class PayCtl {

  private final KakaoCardService service;

  public PayCtl(KakaoCardService service) {
    this.service = service;
  }


  /**
   * 결제 요청 처리
   * 
   * @param PayReqDto
   * @param errors
   * @return
   */
  @PostMapping("/pay")
  public ResponseEntity<PayResDto> payment(@RequestBody @Valid PayReqDto payReq, Errors errors) {
	
	if (errors.hasErrors()) {
		FieldError fieldError = errors.getFieldError();
		throw new PayReqValidateExcn(fieldError.getField() + " : -> " +fieldError.getDefaultMessage());
	}
	PayResDto payResDto = service.payment(payReq);

	return ResponseEntity.ok(payResDto);
  }
  /**
   * 취소 요청 처리
   * 
   * @param CancelResDto
   * @param errors
   * @return
   */
  @PostMapping("/cancel")
  public ResponseEntity<CancelResDto> cancel(@RequestBody @Valid CancelReqDto cancelReq, Errors errors) {
	
	if (errors.hasErrors()) {
		FieldError fieldError = errors.getFieldError();
		throw new PayReqValidateExcn(fieldError.getField() + " : -> " +fieldError.getDefaultMessage());
	}
	CancelResDto cancelResDto = service.cancel(cancelReq);
	return ResponseEntity.ok(cancelResDto);
  }

  
  /**
   * 조회 요청 처리
   * 
   * @param CancelResDto
   * @param errors
   * @return
   */
  @PostMapping("/search")
  public ResponseEntity<SearchResDto> seach(@RequestBody @Valid SearchReqDto searchReq, Errors errors) {
	
	if (errors.hasErrors()) {
		FieldError fieldError = errors.getFieldError();
		throw new NoHavResultExcn(fieldError.getField() + " : -> " +fieldError.getDefaultMessage());
	}
	SearchResDto searchResDto = service.search(searchReq);
	return ResponseEntity.ok(searchResDto);
  }  

}


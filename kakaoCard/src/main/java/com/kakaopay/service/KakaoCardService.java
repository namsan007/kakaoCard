package com.kakaopay.service;

import com.kakaopay.dto.CancelReqDto;
import com.kakaopay.dto.CancelResDto;
import com.kakaopay.dto.PayReqDto;
import com.kakaopay.dto.PayResDto;
import com.kakaopay.dto.SearchReqDto;
import com.kakaopay.dto.SearchResDto;

/**
 * 결제 처리 interface
 *
 * @author kjy
 * @since Create : 2021. 4. 15
 * @version 1.0
 */
public interface KakaoCardService {
  
  /**
   * 결제 처리
   * 
   * @param paymentRequest
   * @return
   */
  PayResDto payment(PayReqDto payReqDto);
  
  /**
   * 결제 처리
   * 
   * @param paymentRequest
   * @return
   */
  CancelResDto cancel(CancelReqDto cancelReqDto);
  
  
  /**
   * 결제 처리
   * 
   * @param paymentRequest
   * @return
   */
  SearchResDto search(SearchReqDto searchReqDto);

  
  
  
}


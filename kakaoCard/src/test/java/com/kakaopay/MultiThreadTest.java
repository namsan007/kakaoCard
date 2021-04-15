package com.kakaopay;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.dto.CancelReqDto;
import com.kakaopay.dto.PayReqDto;
import com.kakaopay.dto.PayResDto;
import com.kakaopay.entity.CancelInfoEnty;
import com.kakaopay.entity.PayInfoEnty;
import com.kakaopay.repository.CancelInfoRep;
import com.kakaopay.repository.PayInfoRep;

/**
 * 멀티스레드 테스트
 *
 * @author kjy
 * @since Create : 2020. 4. 17.
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MultiThreadTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	private final String PAY_MGN_NO = "00000000000000000001"; // 결제시 사용
	private final String CAN_MGN_NO = "00000000000000000009"; // 취소시 사용

	private final String CARD_NO1 = "1234567890123456";
	private final String EXT_MMYY1 = "0420";
	private final String CVC_NO1 = "666";
	private final Integer INS_MTH1 = 0;
	private final Integer PAY_AMT1 = 100000;
	private final Integer VAT_AMT1 = 10000;

	private final String CARD_NO2 = "9234567890123456";
	private final String EXT_MMYY2 = "0421";
	private final String CVC_NO2 = "777";
	private final Integer INS_MTH2 = 1;
	private final Integer PAY_AMT2 = 200000;
	private final Integer VAT_AMT2 = 20000;

	private final Integer CANCEL_PAY_AMT1 = 10000;
	private final Integer CANCEL_VAT_AMT1 = 1000;

	Date dt = new Date();

	@Autowired
	private PayInfoRep payInfoRep;
	@Autowired
	private CancelInfoRep cancelInfoRep;

	@Before
	public void setup() {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	// 기본 payReq셋팅
	public PayReqDto getTestPayReqDto1() {
		PayReqDto payReqDto = new PayReqDto();

		payReqDto.setCardNo(CARD_NO1);
		payReqDto.setExtMmYy(EXT_MMYY1);
		payReqDto.setCvcNo(CVC_NO1);
		payReqDto.setInsMth(INS_MTH1);
		payReqDto.setPayAmt(PAY_AMT1);
		payReqDto.setVatAmt(VAT_AMT1);
		return payReqDto;
	}

	// 기본 payReq셋팅
	public PayReqDto getTestPayReqDto2() {
		PayReqDto payReqDto = new PayReqDto();

		payReqDto.setCardNo(CARD_NO2);
		payReqDto.setExtMmYy(EXT_MMYY2);
		payReqDto.setCvcNo(CVC_NO2);
		payReqDto.setInsMth(INS_MTH2);
		payReqDto.setPayAmt(PAY_AMT2);
		payReqDto.setVatAmt(VAT_AMT2);
		return payReqDto;
	}

	// 기본 cancelReq셋팅
	public CancelReqDto getTestCancelReqDto1(String cancelMgnNo) { // 취소할 관리번호
		CancelReqDto cancelReqDto = new CancelReqDto();

		cancelReqDto.setCancelMgnNo(cancelMgnNo);
		cancelReqDto.setPayAmt(CANCEL_PAY_AMT1);
		cancelReqDto.setVatAmt(CANCEL_VAT_AMT1);
		return cancelReqDto;
	}

	// 1. 결제_진행중_처리
	// 2. 멀티쓰레드_결제
	// 결제 진행중인 상태에서는 해당 카드번호로는 결제가 되어서는 안된다.
	
	public void 하나의_카드번호로_동시에_결제불가() throws Exception {
		PayReqDto payReqDto = getTestPayReqDto1();
		결제_진행중_처리(payReqDto, PAY_MGN_NO); // 결재 하려는 관리번호로 취소 진행중 강제 처리
		멀티쓰레드_결제(payReqDto); // 결제 요청 -> 예상 결과 진행중인 취소건이 있어서 실패
	}
	// 1. 멀티쓰레드_결제
	// 2. 취소_진행중_처리
	// 3. 멀티쓰레드_취소
	// 전체취소,부분취소 : 결제 한 건에 대한 전체취소를 동시에 할 수 없습니다.
	@Test
	public void 멀티쓰레드_취소_진행중() throws Exception {
		PayReqDto payReqDto = getTestPayReqDto1();
		PayResDto pay = 멀티쓰레드_결제(payReqDto); // 결제 이후 자동 생성된 관리번호 조회
		CancelReqDto cancelReqDto = getTestCancelReqDto1(pay.getMgnNo()); // 취소할 관리번호로 셋팅

		취소_진행중_처리(cancelReqDto, payReqDto, pay.getMgnNo()); // 취소 하려는 관리번호로 취소 진행중 강제 처리
		멀티쓰레드_취소(cancelReqDto); // 취소 요청 -> 예상 결과 진행중인 취소건이 있어서 실패
	}
	
	public PayResDto 멀티쓰레드_결제(PayReqDto payReqDto) throws Exception {
		RequestBuilder payBuiler = payBuilder(payReqDto);
		MockHttpServletResponse payResponse = mockMvc.perform(payBuiler).andReturn().getResponse();
		PayResDto pay = convertJsonBytesToObject(payResponse.getContentAsString(), PayResDto.class);
		return pay;
	}	

	public void 멀티쓰레드_취소(CancelReqDto cancelReqDto) throws Exception {

		RequestBuilder cancelBuiler = cancelBuilder(cancelReqDto);
		MockHttpServletResponse cancelResponse = mockMvc.perform(cancelBuiler).andReturn().getResponse();

		assertEquals(cancelResponse.getStatus(), 400);
		
	}

	private void 결제_진행중_처리(PayReqDto payReqDto, String mgnNo) {

		String cardInfo = payReqDto.getCardInfoAddStr();
		PayInfoEnty payInfoEnty = PayInfoEnty.builder().payMgnNo(mgnNo).cardInfo(cardInfo).payAmt(payReqDto.getPayAmt()) // 결제_취소금액(100원
				.vatAmt(payReqDto.getVatAmt()) // 부가가치세(계산해서 생성)
				.payMsg("결제") // "결제", "취소"
				.insMth(payReqDto.getInsMth()) // 할부개월수
				.paySts("진행중") // 진행상태 진행중, 완료
				.inputDt(dt).build();
		payInfoRep.save(payInfoEnty);

	}

	private void 취소_진행중_처리(CancelReqDto cancelReqDto, PayReqDto payReqDto, String cancelMgnNo) {

		String cardInfo = payReqDto.getCardInfoAddStr();
		cancelInfoRep.save(CancelInfoEnty.builder() // 결제 API INSERT
				.cancelMgnNo(CAN_MGN_NO) // 새로 생성된 관리번호
				.payMgnNo(cancelMgnNo) // 취소관리번호
				.cardInfo(cardInfo) // 카드정보
				.payAmt(cancelReqDto.getPayAmt()) // 결제_취소금액(100원 이상, 10억원 이하, 숫자)
				.vatAmt(cancelReqDto.getVatAmt()) // 부가가치세(계산해서 생성)
				.payMsg("취소") // "결제", "취소"
				.paySts("진행중") // 진행상태 진행중, 완료
				.inputDt(new Date()).build());
	}
	
	/**
	 * payment request builder
	 * 
	 * @param paymentRequest
	 * @return
	 */
	public static RequestBuilder payBuilder(PayReqDto payReqDto) {
		return MockMvcRequestBuilders.post("/pay").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(convertObjectToJsonBytes(payReqDto));

	}

	/**
	 * cancel request builder
	 * 
	 * @param cancelRequest
	 * @return
	 */
	public static RequestBuilder cancelBuilder(CancelReqDto cancelReqDto) {
		return MockMvcRequestBuilders.post("/cancel").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(convertObjectToJsonBytes(cancelReqDto));
	}

	public static byte[] convertObjectToJsonBytes(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		try {
			return mapper.writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T convertJsonBytesToObject(String json, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(json, clazz);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}	

}

package com.hcommerce.heecommerce.payment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentViewController {

    @GetMapping("/payments")
    public String handlePaymentFormView() {
        System.out.println("열려라");

        return "tosspayment";
    }

    @GetMapping("/success")
    public String handlePaymentSuccessView() {
        return "toss-payment-success";
    }

    @GetMapping("/fail")
    public String handlePaymentFailView() {
        return "toss-payment-fail";
    }
}

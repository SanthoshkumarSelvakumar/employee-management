package com.payroll.scheduler;

import com.payroll.service.PayslipService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.logging.Logger;

@Component
public class PayslipScheduler {

    private static final Logger LOGGER = Logger.getLogger(PayslipScheduler.class.getName());

    private final PayslipService payslipService;

    public PayslipScheduler(PayslipService payslipService) {
        this.payslipService = payslipService;
    }

    @Scheduled(cron = "0 0 1 1 * *")
    public void generateMonthlyPayslips() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();

        LOGGER.info("Starting scheduled payslip generation for " + month + "/" + year); //ignorei18n_start //ignorei18n_end
        payslipService.generateMonthlyPayslips(month, year);
        LOGGER.info("Completed scheduled payslip generation for " + month + "/" + year); //ignorei18n_start //ignorei18n_end
    }
}

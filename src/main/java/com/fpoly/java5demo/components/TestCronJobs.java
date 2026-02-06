package com.fpoly.java5demo.components;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Để class này có thể hẹn lịch thực hiện công việc được 
@Component
public class TestCronJobs {

//	Hàm này sau 1p sẽ được chạy 1 lần
	@Scheduled(fixedRate = 60000)
	public void test() {
		System.out.println("Run");
	}

//	[giây] [phút] [giờ] [ngày] [tháng] [ngày trong tuần] [năm]
//	Cron muốn vào đúng 30p mỗi giờ sẽ thực hiện cv bên trong hàm testCron
//	@Scheduled(cron = "0 30 * * * * *")
//	8h15p sáng thứ 2 mỗi tuần
//	@Scheduled(cron = "0 15 8 * * 1 *")
//	18h30:00 ngày 25 hàng tháng 
	@Scheduled(cron = "0 30 18 13 * 5 *")
	public void testCron() {
		System.out.println("Cron");
	}
}

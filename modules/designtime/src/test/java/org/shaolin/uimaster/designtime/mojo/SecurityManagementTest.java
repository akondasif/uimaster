package org.shaolin.uimaster.designtime.mojo;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class SecurityManagementTest {
	
	@Test
	public void test() {
//		org.shaolin.bmdp.test.dao.SecurityManagement manager = new org.shaolin.bmdp.test.dao.SecurityManagement();
//		
//		FileImpl file = new FileImpl();
//		file.setName("Shaolin");
//		manager.searchFile(file, 0, 100);
//		manager.searchFileCount(file);
//		manager.searchFileWithOrderby(file, 0, 100);
//		manager.searchFileWithOrderbyCount(file);
//		manager.searchFileJoin(file, 0, 100);
//		manager.searchFileJoinCount(file);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
		System.out.println(sdf.format(new Date()));
	}
	
}

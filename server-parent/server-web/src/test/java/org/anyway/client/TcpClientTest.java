package org.anyway.client;

import org.anyway.common.uGlobalVar;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class TcpClientTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		ByteBuf ibuffer = ByteBufAllocator.DEFAULT.buffer(100);
		TcpClient client = new TcpClient("182.168.0.2", 8082);
		client.send(ibuffer, uGlobalVar.RETRY);
	}

}

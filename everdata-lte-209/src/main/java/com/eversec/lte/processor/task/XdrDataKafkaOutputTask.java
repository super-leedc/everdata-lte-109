package com.eversec.lte.processor.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.eversec.lte.config.SdtpConfig;
import com.eversec.lte.kafka.producer.KafkaByteProducer;
import com.eversec.lte.sdtp.model.NotifyXDRDataReq;

/**
 * 
 * @author bieremayi
 * 
 */
public class XdrDataKafkaOutputTask extends
		AbstractOutputTask<NotifyXDRDataReq> {

	public static final String XDR_TOPIC = "xdr";

	public XdrDataKafkaOutputTask(ArrayBlockingQueue<NotifyXDRDataReq> queue) {
		super(queue);
	}

	@Override
	public void run() {
		while (true) {
			KafkaByteProducer producer = new KafkaByteProducer(SdtpConfig.getKafkaXdrBrokerList());
			Collection<NotifyXDRDataReq> coll = new ArrayList<>();
			try {
				while (true) {
					int count = queue.drainTo(coll, drainMaxElements);
					if (count > 0) {
						for (NotifyXDRDataReq data : coll) {
							producer.sendBytes(XDR_TOPIC, data.toByteArray());
						}
					} else {
						try {
							TimeUnit.MILLISECONDS.sleep(drainTaskSleepMills);//不释放资源锁
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					coll.clear();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				producer.close();
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
				}
			}
		}

	}
}

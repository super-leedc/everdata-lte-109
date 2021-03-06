package com.eversec.lte.sdtp.file;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eversec.lte.constant.SdtpConstants;
import com.eversec.lte.main.LteMain;
import com.eversec.lte.model.single.XdrSingleSource;
import com.eversec.lte.model.single.XdrSingleSourceS1U;

/**
 * Output 综合工具
 * 
 * @author lirongzhi
 * 
 */
public abstract class FileOutputTools {

	protected Logger logger = LoggerFactory.getLogger(FileOutputTools.class);

	protected final String S6A = "s6a";
	protected final String SGS = "sgs";
	protected final String S1MME = "s1mme";
	protected final String S11 = "s11";
	
	protected final String S1U = "s1u";

	protected final String UU = "uu";
	protected final String X2 = "x2";
	protected final String UEMR = "uemr";
	protected final String CELLMR = "cellmr";
	
	protected final String SIP = "sip";
	protected final String SV = "sv";
	protected final String DIA = "dia";
	protected final String RX = "rx";
	protected final String HARASS = "harass";

	protected Map<String, FileBufferCache> buffer_cache_map = new HashMap<>();

	protected AtomicLong count = new AtomicLong(0);

	protected FileBufferOutput file_output;

	public void outputXdr(XdrSingleSource xdr) {

		long time = xdr.getProduceEndTime();
		short Interface = -1;
		int apptype = -1;
		if (xdr instanceof XdrSingleSourceS1U) {
			XdrSingleSourceS1U s1u = (XdrSingleSourceS1U) xdr;
			Interface = s1u.getS1uCommon().getInterface();
			apptype = s1u.getBusinessCommon().getAppTypeCode();
		} else {
			Interface = xdr.getCommon().getInterface();
		}

		StringBuilder sb = new StringBuilder();
		String[] strs = xdr.toStringArr();
		for (String str : strs) {
			sb.append(str);
			sb.append("|");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}

		SdtpFileOutputItem data = new SdtpFileOutputItem(sb.toString(), time,
				Interface, apptype);
		try {
			switch (Interface) {
			case SdtpConstants.XDRInterface.UU:
				buffer_cache_map.get(UU).putData(data);
				break;
			case SdtpConstants.XDRInterface.X2:
				buffer_cache_map.get(X2).putData(data);
				break;
			case SdtpConstants.XDRInterface.UE_MR:
				buffer_cache_map.get(UEMR).putData(data);
				break;
			case SdtpConstants.XDRInterface.CELL_MR:
				buffer_cache_map.get(CELLMR).putData(data);
				break;
			case SdtpConstants.XDRInterface.S1MME:
				buffer_cache_map.get(S1MME).putData(data);
				break;
			case SdtpConstants.XDRInterface.S6A:
				buffer_cache_map.get(S6A).putData(data);
				break;
			case SdtpConstants.XDRInterface.S11:
				buffer_cache_map.get(S11).putData(data);
				break;
			case SdtpConstants.XDRInterface.SGS:
				buffer_cache_map.get(SGS).putData(data);
				break;
			case SdtpConstants.XDRInterface.S1U:
				buffer_cache_map.get("app_" + apptype).putData(data);
				break;
			case SdtpConstants.XDRInterface.SIP:
				buffer_cache_map.get("SIP").putData(data);
			case SdtpConstants.XDRInterface.SV:
				buffer_cache_map.get("SV").putData(data);
			case SdtpConstants.XDRInterface.RX:
				buffer_cache_map.get("RX").putData(data);
			case SdtpConstants.XDRInterface.DIA:
				buffer_cache_map.get("SIP").putData(data);
			case SdtpConstants.XDRInterface.HARASS:
				buffer_cache_map.get("HARASS").putData(data);
			default:
				break;
			}

			count.incrementAndGet();
		} catch (Exception e) {
			System.err.println(buffer_cache_map);
			System.err.println("Interface:" + Interface + ",apptype" + apptype);

			e.printStackTrace();
		}

	}

	protected abstract void init();
}

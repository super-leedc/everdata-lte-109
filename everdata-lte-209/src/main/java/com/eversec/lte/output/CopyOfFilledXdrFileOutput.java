//package com.eversec.lte.output;
//
//import static com.eversec.lte.constant.SdtpConstants.XDRInterface.CELL_MR;
//import static com.eversec.lte.constant.SdtpConstants.XDRInterface.S11;
//import static com.eversec.lte.constant.SdtpConstants.XDRInterface.S1MME;
//import static com.eversec.lte.constant.SdtpConstants.XDRInterface.S1U;
//import static com.eversec.lte.constant.SdtpConstants.XDRInterface.S6A;
//import static com.eversec.lte.constant.SdtpConstants.XDRInterface.SGS;
//import static com.eversec.lte.constant.SdtpConstants.XDRInterface.UE_MR;
//import static com.eversec.lte.constant.SdtpConstants.XDRInterface.UU;
//import static com.eversec.lte.constant.SdtpConstants.XDRInterface.X2;
//import static com.eversec.lte.main.LteMain.EXEC;
//import static com.eversec.lte.main.LteMain.FILE_OUTPUT_TASK;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.TimeUnit;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.eversec.common.output.FileOutput;
//import com.eversec.common.output.IOutput;
//import com.eversec.lte.config.SdtpConfig;
//import com.eversec.lte.constant.SdtpConstants;
//import com.eversec.lte.model.single.XdrSingleSource;
//import com.eversec.lte.model.single.XdrSingleSourceS1U;
//import com.eversec.lte.vo.DataQueueCache;
//
//@SuppressWarnings("rawtypes")
//public class CopyOfFilledXdrFileOutput {
//
//	private static Logger logger = LoggerFactory
//			.getLogger(CopyOfFilledXdrFileOutput.class);
//
//	protected static final String CACHE_TYPE = "filled_file";
//
//	private static final ThreadLocal<DataQueueCache<IOutput>> S6A_DATA_CACHE = createDataCache();
//	private static final ThreadLocal<DataQueueCache<IOutput>> SGS_DATA_CACHE = createDataCache();
//	private static final ThreadLocal<DataQueueCache<IOutput>> S1MME_DATA_CACHE = createDataCache();
//	private static final ThreadLocal<DataQueueCache<IOutput>> S11_DATA_CACHE = createDataCache();
//	private static final ThreadLocal<DataQueueCache<IOutput>> S1U_DATA_CACHE = createDataCache();
//	private static final ThreadLocal<DataQueueCache<IOutput>> UEMR_DATA_CACHE = createDataCache();
//	private static final ThreadLocal<DataQueueCache<IOutput>> X2_DATA_CACHE = createDataCache();
//	private static final ThreadLocal<DataQueueCache<IOutput>> CELLMR_DATA_CACHE = createDataCache();
//	private static final ThreadLocal<DataQueueCache<IOutput>> UU_DATA_CACHE = createDataCache();
//
//	// 存放回填后等待输出的单接口XDR队列
//	public static ArrayBlockingQueue<IOutput[]> S6A_FILLED_QUEUE = new ArrayBlockingQueue<>(
//			SdtpConfig.getDataQueueCapacity() / DataQueueCache.SIZE);
//	public static ArrayBlockingQueue<IOutput[]> SGS_FILLED_QUEUE = new ArrayBlockingQueue<>(
//			SdtpConfig.getDataQueueCapacity() / DataQueueCache.SIZE);
//	public static ArrayBlockingQueue<IOutput[]> S1MME_FILLED_QUEUE = new ArrayBlockingQueue<>(
//			SdtpConfig.getDataQueueCapacity() / DataQueueCache.SIZE);
//	public static ArrayBlockingQueue<IOutput[]> S11_FILLED_QUEUE = new ArrayBlockingQueue<>(
//			SdtpConfig.getDataQueueCapacity() / DataQueueCache.SIZE);
//
//	public static ArrayBlockingQueue<IOutput[]> S1U_FILLED_QUEUE = new ArrayBlockingQueue<>(
//			SdtpConfig.getDataQueueCapacity() / DataQueueCache.SIZE);
//
//	public static ArrayBlockingQueue<IOutput[]> UEMR_FILLED_QUEUE = new ArrayBlockingQueue<>(
//			SdtpConfig.getDataQueueCapacity() / DataQueueCache.SIZE);
//	public static ArrayBlockingQueue<IOutput[]> X2_FILLED_QUEUE = new ArrayBlockingQueue<>(
//			SdtpConfig.getDataQueueCapacity() / DataQueueCache.SIZE);
//	public static ArrayBlockingQueue<IOutput[]> CELLMR_FILLED_QUEUE = new ArrayBlockingQueue<>(
//			SdtpConfig.getDataQueueCapacity() / DataQueueCache.SIZE);
//	public static ArrayBlockingQueue<IOutput[]> UU_FILLED_QUEUE = new ArrayBlockingQueue<>(
//			SdtpConfig.getDataQueueCapacity() / DataQueueCache.SIZE);
//
//	// 文件输出工具
//	public static FileOutput S6A_FILLED_OUTPUT;
//	public static FileOutput SGS_FILLED_OUTPUT;
//	public static List<FileOutput> S1MME_FILLED_OUTPUTS;
//	public static List<FileOutput> S11_FILLED_OUTPUTS;
//	// public static List<FileOutput> S1U_FILLED_OUTPUTS;
//
//	public static Map<String, List<FileOutput>> S1U_FILLED_OUTPUTS_MAP;
//
//	public static FileOutput X2_FILLED_OUTPUT;
//	public static FileOutput UU_FILLED_OUTPUT;
//	public static FileOutput UEMR_FILLED_OUTPUT;
//	public static FileOutput CELLMR_FILLED_OUTPUT;
//
//	public static void report() {
//		logger.info(
//				"s1mme_filled : {} , sgs_filled : {} , s6a_filled : {} , s11_filled : {} ",
//				new Object[] { S1MME_FILLED_QUEUE.size(),
//						SGS_FILLED_QUEUE.size(), S6A_FILLED_QUEUE.size(),
//						S11_FILLED_QUEUE.size() });
//		logger.info(
//				"  uu_filled : {} , x2_filled : {} , uemr_filled : {} , cellmr_filled : {} ",
//				new Object[] { UU_FILLED_QUEUE.size(), X2_FILLED_QUEUE.size(),
//						UEMR_FILLED_QUEUE.size(), CELLMR_FILLED_QUEUE.size() });
//
//		logger.info("s1u_filled : {} ",
//				new Object[] { S1U_FILLED_QUEUE.size() });
//	}
//
//	private static ThreadLocal<DataQueueCache<IOutput>> createDataCache() {
//		return new ThreadLocal<DataQueueCache<IOutput>>() {
//			protected DataQueueCache<IOutput> initialValue() {
//				return new DataQueueCache<IOutput>(CACHE_TYPE) {
//					public IOutput[] createCache(int size) {
//						return new IOutput[SIZE];
//					}
//				};
//			};
//		};
//	}
//
//	/**
//	 * 回填后的单接口XDR数据写入
//	 * 
//	 * @param Interface
//	 * @param data
//	 */
//	public static void flushFilled(String Interface, IOutput data) {
//		flushFilled(Integer.parseInt(Interface), data);
//	}
//
//	/**
//	 * 回填后的单接口XDR数据写入
//	 * 
//	 * @param data
//	 */
//	public static void output(XdrSingleSource data) {
//		flushFilled(data.getCommon().getInterface(), data);
//	}
//
//	/**
//	 * 回填后的单接口XDR数据写入
//	 * 
//	 * @param Interface
//	 * @param data
//	 */
//	public static void flushFilled(int Interface, IOutput data) {
//		if (SdtpConfig.IS_OUTPUT_FILLED_XDR_2_FILE) {
//			try {
//				switch (Interface) {
//				case UU:
//					put(UU_DATA_CACHE, UU_FILLED_QUEUE, data);
//					// UU_FILLED_QUEUE.put(data);
//					break;
//				case X2:
//					put(X2_DATA_CACHE, X2_FILLED_QUEUE, data);
//					// X2_FILLED_QUEUE.put(data);
//					break;
//				case UE_MR:
//					put(UEMR_DATA_CACHE, UEMR_FILLED_QUEUE, data);
//					// UEMR_FILLED_QUEUE.put(data);
//					break;
//				case CELL_MR:
//					put(CELLMR_DATA_CACHE, CELLMR_FILLED_QUEUE, data);
//					// CELLMR_FILLED_QUEUE.put(data);
//					break;
//				case S1MME:
//					put(S1MME_DATA_CACHE, S1MME_FILLED_QUEUE, data);
//					// S1MME_FILLED_QUEUE.put(data);
//					break;
//				case S6A:
//					put(S6A_DATA_CACHE, S6A_FILLED_QUEUE, data);
//					// S6A_FILLED_QUEUE.put(data);
//					break;
//				case S11:
//					put(S11_DATA_CACHE, S11_FILLED_QUEUE, data);
//					// S11_FILLED_QUEUE.put(data);
//					break;
//				case SGS:
//					put(SGS_DATA_CACHE, SGS_FILLED_QUEUE, data);
//					// SGS_FILLED_QUEUE.put(data);
//					break;
//				case S1U:
//					put(S1U_DATA_CACHE, S1U_FILLED_QUEUE, data);
//					// S1U_FILLED_QUEUE.put(data);
//					break;
//				default:
//					break;
//				}
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private static void put(ThreadLocal<DataQueueCache<IOutput>> local,
//			ArrayBlockingQueue<IOutput[]> output, IOutput data) throws InterruptedException {
//
//		IOutput[] cache = local.get().addAndGet(data);
//		if (cache != null) {
//			output.put(cache);
//		}
//
//	}
//
//	@SuppressWarnings("unchecked")
//	public static void initOutputTask() {
//		String max_memory_size = SdtpConfig.getOutputFileSize();
//		String expire_time = SdtpConfig.getOutputFilePeriod();
//		// String filledDir = SdtpConfig.getFilledDir();
//		String filledScaDir = SdtpConfig.getFilledScaDir();
//		String filledSigDir = SdtpConfig.getFilledSigDir();
//		String filledS1uDir = SdtpConfig.getFilledS1uDir();
//		String S6A = "s6a";
//		String SGS = "sgs";
//		String S1MME = "s1mme";
//		String S11 = "s11";
//		String S1U = "s1u";
//
//		String UU = "uu";
//		String X2 = "x2";
//		String UEMR = "uemr";
//		String CELLMR = "cellmr";
//		// 初始化output工具类
//		// s6a
//		S6A_FILLED_OUTPUT = new LteFileOutput(max_memory_size, expire_time,
//				filledSigDir + File.separator + S6A, S6A);
//		FILE_OUTPUT_TASK.addOutput(S6A_FILLED_OUTPUT);
//		EXEC.execute(new Runnable() {
//			@Override
//			public void run() {
//				while (true) {
//					try {
//						IOutput[] datas = S6A_FILLED_QUEUE.take();
//						for (IOutput data : datas) {
//							S6A_FILLED_OUTPUT.flush(data);
//						}
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//		// sgs
//		SGS_FILLED_OUTPUT = new LteFileOutput(max_memory_size, expire_time,
//				filledSigDir + File.separator + SGS, SGS);
//		FILE_OUTPUT_TASK.addOutput(SGS_FILLED_OUTPUT);
//		EXEC.execute(new Runnable() {
//			@Override
//			public void run() {
//				while (true) {
//					try {
//						IOutput[] datas = SGS_FILLED_QUEUE.take();
//						for (IOutput data : datas) {
//							SGS_FILLED_OUTPUT.flush(data);
//						}
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//		// s1mme
//		S1MME_FILLED_OUTPUTS = new ArrayList<FileOutput>(
//				SdtpConfig.getS1mmeOutputThread());
//		for (int i = 0; i < SdtpConfig.getS1mmeOutputThread(); i++) {
//			LteFileOutput output = new LteFileOutput(max_memory_size,
//					expire_time, filledSigDir + File.separator + S1MME
//							+ File.separator + i, S1MME);
//			S1MME_FILLED_OUTPUTS.add(output);
//			FILE_OUTPUT_TASK.addOutput(output);
//		}
//		for (int i = 0; i < SdtpConfig.getS1mmeOutputThread(); i++) {
//			final int index = i;
//			EXEC.execute(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						IOutput[] datas = S1MME_FILLED_QUEUE.take();
//						for (IOutput data : datas) {
//							S1MME_FILLED_OUTPUTS.get(index).flush(data);
//						}
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			});
//		}
//		// s11
//		S11_FILLED_OUTPUTS = new ArrayList<FileOutput>(
//				SdtpConfig.getS11OutputThread());
//		for (int i = 0; i < SdtpConfig.getS11OutputThread(); i++) {
//			LteFileOutput output = new LteFileOutput(max_memory_size,
//					expire_time, filledSigDir + File.separator + S11
//							+ File.separator + i, S11);
//			S11_FILLED_OUTPUTS.add(output);
//			FILE_OUTPUT_TASK.addOutput(output);
//		}
//		for (int i = 0; i < SdtpConfig.getS11OutputThread(); i++) {
//			final int index = i;
//			EXEC.execute(new Runnable() {
//				@Override
//				public void run() {
//					while (true) {
//						try {
//							IOutput[] datas = S11_FILLED_QUEUE.take();
//							for (IOutput data : datas) {
//								S11_FILLED_OUTPUTS.get(index).flush(data);
//							}
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			});
//		}
//		// s1u
//		S1U_FILLED_OUTPUTS_MAP = initS1uOutputMap();
//		for (String appTypeCode : S1U_FILLED_OUTPUTS_MAP.keySet()) {
//			for (int i = 0; i < SdtpConfig.getS1UOutputThread(); i++) {
//				LteFileOutput output = new LteFileOutput(max_memory_size,
//						expire_time, filledS1uDir + File.separator + S1U + "_"
//								+ appTypeCode + File.separator + i, S1U);
//				S1U_FILLED_OUTPUTS_MAP.get(appTypeCode).add(output);
//				FILE_OUTPUT_TASK.addOutput(output);
//			}
//		}
//		for (int i = 0; i < SdtpConfig.getS1UOutputThread(); i++) {
//			final int index = i;
//			EXEC.execute(new Runnable() {
//				@Override
//				public void run() {
//					while (true) {
//						XdrSingleSourceS1U data = null;
//						try {
//							IOutput[] datas = S1U_FILLED_QUEUE.take();
//							for (IOutput dataSource : datas) {
//								data = (XdrSingleSourceS1U) dataSource;
//								short code = data.getBusinessCommon()
//										.getAppTypeCode();
//								String appTypeCode = SdtpConstants.S1U_APP_TYPE_CODE_MAP
//										.get(code);
//								S1U_FILLED_OUTPUTS_MAP.get(appTypeCode)
//										.get(index).flush(data);
//							}
//
//						} catch (Exception e) {
//							System.err.println(S1U_FILLED_OUTPUTS_MAP);
//							System.err.println(data);
//							e.printStackTrace();
//						}
//					}
//				}
//			});
//		}
//
//		// uu
//		UU_FILLED_OUTPUT = new LteFileOutput(max_memory_size, expire_time,
//				filledScaDir + File.separator + UU, UU);
//		FILE_OUTPUT_TASK.addOutput(UU_FILLED_OUTPUT);
//		EXEC.execute(new Runnable() {
//			@Override
//			public void run() {
//				while (true) {
//					try {
//						IOutput[] datas = UU_FILLED_QUEUE.take();
//						for (IOutput data : datas) {
//							UU_FILLED_OUTPUT.flush(data);
//						}
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//
//		// x2
//		X2_FILLED_OUTPUT = new LteFileOutput(max_memory_size, expire_time,
//				filledScaDir + File.separator + X2, X2);
//		FILE_OUTPUT_TASK.addOutput(X2_FILLED_OUTPUT);
//		EXEC.execute(new Runnable() {
//			@Override
//			public void run() {
//				while (true) {
//					try {
//						IOutput[] datas = X2_FILLED_QUEUE.take();
//						for (IOutput data : datas) {
//							X2_FILLED_OUTPUT.flush(data);
//						}
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//
//		// uemr
//		UEMR_FILLED_OUTPUT = new LteFileOutput(max_memory_size, expire_time,
//				filledScaDir + File.separator + UEMR, UEMR);
//		FILE_OUTPUT_TASK.addOutput(UEMR_FILLED_OUTPUT);
//		EXEC.execute(new Runnable() {
//			@Override
//			public void run() {
//				while (true) {
//					try {
//						IOutput[] datas = UEMR_FILLED_QUEUE.take();
//						for (IOutput data : datas) {
//							UEMR_FILLED_OUTPUT.flush(data);
//						}
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//
//		// cellmr
//		CELLMR_FILLED_OUTPUT = new LteFileOutput(max_memory_size, expire_time,
//				filledScaDir + File.separator + CELLMR, CELLMR);
//		FILE_OUTPUT_TASK.addOutput(CELLMR_FILLED_OUTPUT);
//		EXEC.execute(new Runnable() {
//			@Override
//			public void run() {
//				while (true) {
//					try {
//						IOutput[] datas = CELLMR_FILLED_QUEUE.take();
//						for (IOutput data : datas) {
//							CELLMR_FILLED_OUTPUT.flush(data);
//						}
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//	}
//
//	private static Map<String, List<FileOutput>> initS1uOutputMap() {
//		Map<String, List<FileOutput>> result = new HashMap<>();
//		for (String value : SdtpConstants.S1U_APP_TYPE_CODE_MAP.values()) {
//			result.put(value,
//					new ArrayList<FileOutput>(SdtpConfig.getS1UOutputThread()));
//		}
//		return result;
//	}
//
//}

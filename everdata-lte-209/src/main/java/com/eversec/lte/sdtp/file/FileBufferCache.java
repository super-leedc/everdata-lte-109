package com.eversec.lte.sdtp.file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eversec.common.util.ConvertUtils;
import com.eversec.lte.config.SdtpConfig;

public abstract class FileBufferCache implements Runnable {
	protected static Logger logger = LoggerFactory
			.getLogger(FileBufferCache.class);

	protected ArrayBlockingQueue<FileOutputItem> queue;

	public static final long BUFFER_SIZE = SdtpConfig.getFileBufferCacheSize();

	protected IoBuffer buffer;

	protected long totalSize = 0;

	protected long lasttime;

	protected final String path;

	protected final long maxlimit;

	protected final long expire;

	protected final ArrayBlockingQueue<FileBuffer> fileBufferQueue;

	public FileBufferCache(String path, String maxlimit, String expire,
			ArrayBlockingQueue<FileBuffer> fileBufferQueue) {
		super();
		this.path = path;
		this.maxlimit = ConvertUtils.parseBytesSizeValue(maxlimit);
		this.expire = ConvertUtils.parseTimeValue(expire);
		this.fileBufferQueue = fileBufferQueue;
		this.queue = new ArrayBlockingQueue<FileOutputItem>(10000);
	}
	
	public FileBufferCache(String path, String maxlimit, String expire,
			ArrayBlockingQueue<FileBuffer> fileBufferQueue, int queueSize) {
		super();
		this.path = path;
		this.maxlimit = ConvertUtils.parseBytesSizeValue(maxlimit);
		this.expire = ConvertUtils.parseTimeValue(expire);
		this.fileBufferQueue = fileBufferQueue;
		this.queue = new ArrayBlockingQueue<FileOutputItem>(queueSize);
	}

	public FileBufferCache(String path, long maxlimit, long expire,
			ArrayBlockingQueue<FileBuffer> fileBufferQueue) {
		super();
		this.path = path;
		this.maxlimit = maxlimit;
		this.expire = expire;
		this.fileBufferQueue = fileBufferQueue;
		this.queue = new ArrayBlockingQueue<FileOutputItem>(10000);
	}

	public FileBufferCache(String path, long maxlimit, long expire,
			ArrayBlockingQueue<FileBuffer> fileBufferQueue, int queueSize) {
		super();
		this.path = path;
		this.maxlimit = maxlimit;
		this.expire = expire;
		this.fileBufferQueue = fileBufferQueue;
		this.queue = new ArrayBlockingQueue<FileOutputItem>(queueSize);
	}

	public void createBuffer() {
		// buffer = IoBuffer.allocate(1024, true).setAutoExpand(true);
		buffer = IoBuffer.allocate((int) BUFFER_SIZE, false)
				.setAutoExpand(true);
	}

	public void putBuffer(String line) {
		byte[] bytes = (line + "\n").getBytes();
		buffer.put(bytes);
		totalSize += bytes.length;
	}

	@Deprecated
	public void run2() {
		createBuffer();
		while (true) {
			try {
				FileOutputItem item = queue.take();
				lasttime = System.currentTimeMillis();

				String line = item.line;
				if (line != null) {
					putBuffer(line);
				}
				if (buffer.limit() > BUFFER_SIZE || totalSize > maxlimit
						|| line == null) {
					if (totalSize > maxlimit) {
						fileBufferQueue.put(new FileBuffer(path, buffer,
								getParameters(item), false));

						totalSize = 0;
					} else {
						fileBufferQueue.put(new FileBuffer(path, buffer,
								getParameters(item), true));
					}

					createBuffer();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		createBuffer();
		while (true) {
			try {
				Collection<FileOutputItem> list = new ArrayList<>();
				int count = queue.drainTo(list/* , 10000 */);
				if (count > 0) {
					for (FileOutputItem item : list) {
						String line = item.line;
						lasttime = System.currentTimeMillis();

					if (line != null) {
							putBuffer(line);
						}
						if (buffer.limit() > BUFFER_SIZE
								|| totalSize > maxlimit || line == null) {
							if (totalSize > maxlimit) {
								fileBufferQueue.put(new FileBuffer(path,
										buffer, getParameters(item), false));

								totalSize = 0;
							} else {
								fileBufferQueue.put(new FileBuffer(path,
										buffer, getParameters(item), true));
							}

							createBuffer();
						}

					}
				} else {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
					}
				}
				list.clear();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public abstract Object[] getParameters(FileOutputItem item);

	public void putData(FileOutputItem e) throws Exception {
		queue.put(e);
	}

	public void reflush() {
		try {
			if ((System.currentTimeMillis() - lasttime) >= expire
					&& buffer != null && buffer.position() > 0) {
				logger.info("expire to flush! limit size :" + buffer.limit());
				queue.put(new SdtpFileOutputItem(null, System
						.currentTimeMillis(), (short) 0, 0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public String toString() {
		return "queue:"+queue.size();
	}

}

package com.huawei.checkpoint.utils;

/**
 * 环形数组缓冲区 缓冲区所能存放的数据为缓冲区大小的个数 减去1 为实际能存储的大小
 * 
 * @author Xialf
 *
 */
public class RingBuffer {
	public byte[] buf = null;
	public int start;
	public int end;
	public int dataLen;

	public RingBuffer(int s) {// 构造函数定义缓冲区的大小
		buf = new byte[s];
		this.start = this.end = 0;
		dataLen = 0;
	}

	public static void main(String[] args) {// 测试
		RingBuffer buf = new RingBuffer(11);
		byte[] ar = "aabbccddee".getBytes();
		if (buf.put(ar) == false) {
			System.out.println("空间不够");
			return;
		}

		byte[] ar1 = new byte[4];
		ar1 = buf.get(ar1.length);
		if (ar1 != null) {
			System.out.println(new String(ar1));
		} else {
			System.out.println("内容不够，无法提取！");
		}

		byte[] ara = "ff".getBytes();
		if (buf.put(ara) == false) {
			System.out.println("空间不够");
			return;
		}

		byte[] ar3 = new byte[8];
		ar3 = buf.get(ar3.length);
		if (ar3 != null) {
			System.out.println(new String(ar3));
		} else {
			System.out.println("内容不够，无法提取！");
		}

		byte[] ar2 = "eerr".getBytes();
		if (buf.put(ar2) == false) {
			System.out.println("空间不够");
		}

		byte[] ar3a = new byte[4];
		ar3a = buf.get(ar3a.length);
		if (ar3a != null) {
			System.out.println(new String(ar3a));
		} else {
			System.out.println("内容不够，无法提取！");
		}

		byte[] araa = "aabbccddee".getBytes();
		if (buf.put(araa) == false) {
			System.out.println("空间不够");
			return;
		}
		byte[] ar3aa = new byte[10];
		ar3aa = buf.get(ar3aa.length);
		if (ar3aa != null) {
			System.out.println(new String(ar3aa));
		} else {
			System.out.println("内容不够，无法提取！");
		}

	}
	public boolean put(byte[] ar) {
		if (end + 1 == start || (start == end && start == 0 && ar.length >= buf.length)) {
			// System.out.println("空间不够");
			return false;// 已满||空间不够
		} else if (start < end && ar.length >= buf.length - end + start) {
			// System.out.println("空间不够");
			return false;// 空间不够
		} else if (start > end && ar.length >= start - end) {
			// System.out.println("空间不够");
			return false;// 空间不够
		} else if (start < end && ar.length < buf.length - end + start) {
			if (ar.length < buf.length - end) {
				System.arraycopy(ar, 0, buf, end, ar.length);
				end = end + ar.length;
				
			} else {
				System.arraycopy(ar, 0, buf, end, buf.length - end);
				System.arraycopy(ar, (buf.length - end), buf, 0, ar.length - (buf.length - end));
				end = ar.length - (buf.length - end);
			}
		} else if (start > end && ar.length < start - end) {
			System.arraycopy(ar, 0, buf, end, ar.length);
			end = end + ar.length;
		} else if (start == end && start == 0 && ar.length < buf.length) {
			start = 0;
			System.arraycopy(ar, 0, buf, start, ar.length);
			end = ar.length;
		}
		
		dataLen += ar.length;
		return true;
	}

	public  byte[] get(int len) {
		byte[] arr = new byte[len];
		if (start < end) {
			if (len <= end - start) {
				System.arraycopy(buf, start, arr, 0, len);
				start = start + len;
				if (start == end) {
					start = end = 0;
				}
				dataLen -= len;
				return arr;
			} else {
				// System.out.println("内容不够，无法提取！");
				return null;
			}
		} else if (start > end) {
			if (len <= buf.length - start) {
				System.arraycopy(buf, start, arr, 0, len);
				start = start + len;
				if (start == end) {
					start = end = 0;
				}
				dataLen -= len;
				return arr;
			} else if (len <= buf.length - start + end) {
				System.arraycopy(buf, start, arr, 0, buf.length - start);
				System.arraycopy(buf, 0, arr, buf.length - start, len - (buf.length - start));
				start = len - (buf.length - start);
				if (start == end) {
					start = end = 0;
				}
				dataLen -= len;
				return arr;
			} else {
				// System.out.println("内容不够，无法提取！");
				return null;
			}
		}
		// System.out.println("内容不够，无法提取！");
		return null;
	} 
	
	public  byte[] tryGet(int len) {
		byte[] arr = new byte[len];
		if (start < end) {
			if (len <= end - start) {
				System.arraycopy(buf, start, arr, 0, len);
				//start = start + len;
				//if (start == end) {
				//	start = end = 0;
				//}
				//dataLen -= len;
				return arr;
			} else {
				// System.out.println("内容不够，无法提取！");
				return null;
			}
		} else if (start > end) {
			if (len <= buf.length - start) {
				System.arraycopy(buf, start, arr, 0, len);
				//start = start + len;
				//if (start == end) {
				//	start = end = 0;
				//}
				//dataLen -= len;
				return arr;
			} else if (len <= buf.length - start + end) {
				System.arraycopy(buf, start, arr, 0, buf.length - start);
				System.arraycopy(buf, 0, arr, buf.length - start, len - (buf.length - start));
				//start = len - (buf.length - start);
				//if (start == end) {
				//	start = end = 0;
				//}
				//dataLen -= len;
				return arr;
			} else {
				// System.out.println("内容不够，无法提取！");
				return null;
			}
		}
		// System.out.println("内容不够，无法提取！");
		return null;
	}
	public  int shift(int len) {
		//byte[] arr = new byte[len];
		if (start < end) {
			if (len <= end - start) {
				//System.arraycopy(buf, start, arr, 0, len);
				start = start + len;
				if (start == end) {
					start = end = 0;
				}
				dataLen -= len;
				return len;
			} else {
				// System.out.println("内容不够，无法提取！");
				return -1;
			}
		} else if (start > end) {
			if (len <= buf.length - start) {
				//System.arraycopy(buf, start, arr, 0, len);
				start = start + len;
				if (start == end) {
					start = end = 0;
				}
				dataLen -= len;
				return len;
			} else if (len <= buf.length - start + end) {
				//System.arraycopy(buf, start, arr, 0, buf.length - start);
				//System.arraycopy(buf, 0, arr, buf.length - start, len - (buf.length - start));
				start = len - (buf.length - start);
				if (start == end) {
					start = end = 0;
				}
				dataLen -= len;
				return len;
			} else {
				// System.out.println("内容不够，无法提取！");
				return -1;
			}
		}
		// System.out.println("内容不够，无法提取！");
		return -1;
	}
}
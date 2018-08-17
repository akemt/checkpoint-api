package com.huawei.checkpoint.data;

public class SubscribeStatus {
	private final static Object syncLock = new Object();

	private boolean tollgateUpdateStatus = true;
	private boolean subscribedMotorVehicle = false;
	private boolean subscribedTollgate = false;

	private boolean laneUpdateStatus = true;
	private boolean subscribedLaneInfo = false;

	private static volatile SubscribeStatus instance;

	public static SubscribeStatus getIns() {
		if (instance == null)
			synchronized (SubscribeStatus.class) {
				if (instance == null)
					instance = new SubscribeStatus();
			}
		return instance;
	}

	public boolean isLaneUpdateStatus() {
		return laneUpdateStatus;
	}

	public void setLaneUpdateStatus(boolean laneUpdateStatus) {
		synchronized (syncLock) {
			this.laneUpdateStatus = laneUpdateStatus;
		}
	}

	public boolean isSubscribedLaneInfo() {
		return subscribedLaneInfo;
	}

	public void setSubscribedLaneInfo(boolean subscribedLaneInfo) {
		synchronized (syncLock) {
			this.subscribedLaneInfo = subscribedLaneInfo;
		}
	}

	public boolean isTollgateUpdateStatus() {
		return tollgateUpdateStatus;
	}

	public void setTollgateUpdateStatus(boolean tollgateUpdateStatus) {
		synchronized (syncLock) {
			this.tollgateUpdateStatus = tollgateUpdateStatus;
		}
	}

	public boolean isSubscribedMotorVehicle() {
		return subscribedMotorVehicle;
	}

	public void setSubscribedMotorVehicle(boolean subscribedMotorVehicle) {
		synchronized (syncLock) {
			this.subscribedMotorVehicle = subscribedMotorVehicle;
		}
	}

	public boolean isSubscribedTollgate() {
		return subscribedTollgate;
	}

	public void setSubscribedTollgate(boolean subscribedTollgate) {
		synchronized (syncLock) {
			this.subscribedTollgate = subscribedTollgate;
		}
	}
}

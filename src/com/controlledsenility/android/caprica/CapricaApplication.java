package com.controlledsenility.android.caprica;

import android.app.Application;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;

public class CapricaApplication extends Application {

	/**
	 * The drone is kept in the application context so that all activities use
	 * the same drone instance
	 */
	private IARDrone drone;

	public void onCreate() {
		drone = new ARDrone("192.168.1.1");
	}

	public IARDrone getARDrone() {
		return drone;
	}

}
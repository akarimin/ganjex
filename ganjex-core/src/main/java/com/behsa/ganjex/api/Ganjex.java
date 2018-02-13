/*
 * Copyright 2018 Behsa Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.behsa.ganjex.api;

import com.behsa.ganjex.container.GanjexApplication;
import com.behsa.ganjex.container.HookLoader;
import com.behsa.ganjex.watch.JarWatcher;
import com.behsa.ganjex.watch.ServiceFileChangeListener;
import com.behsa.ganjex.watch.LibraryFileChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;

/**
 * Ganjex container class. each instance of this class represent a ganjex container.
 * <p>
 * clients can use static method <code>run({@link GanjexConfiguration})</code> to start a new
 * container. running a new container means Ganjex watch the library and service directory for
 * the changes and start or shutdown service when a jar files under those directory changes.
 * </p>
 *
 * @author hekmatof
 * @since 1.0
 */
public final class Ganjex {
	private static final Logger log = LoggerFactory.getLogger(Ganjex.class);
	private static volatile boolean bootstrapped = false;
	private JarWatcher serviceWatcher = null;
	private JarWatcher libWatcher = null;
	private GanjexApplication app;

	public Ganjex(GanjexConfiguration config) {
		app = new GanjexApplication(config);
	}

	/**
	 * start a new ganjex container and return an object of this class representing the container
	 * same as calling <code>new Ganjex(config).run()</code>
	 *
	 * @param config ganjex configuration object, this object should be created by the
	 *               {@link GanjexConfiguration.Builder} which is a builder for
	 *               {@link GanjexConfiguration}
	 * @return a running ganjex container object
	 */
	public static Ganjex run(GanjexConfiguration config) {
		return new Ganjex(config).run();
	}

	/**
	 * indicate the bootstrap process of the container has done or not
	 *
	 * @return return true if and only if container bootstrapped and not destroyed
	 */
	public static boolean bootstrapped() {
		return bootstrapped;
	}

	/**
	 * @return the top level classloader
	 */
	public ClassLoader mainClassLoader() {
		return app.mainClassLoader();
	}

	private void watchServicesDirectory(String servicePath) {
		serviceWatcher = new JarWatcher(new File(servicePath),
						new ServiceFileChangeListener(app), app.config().getWatcherDelay());
	}

	private void watchLibraryDirectory(String libPath) {
		libWatcher = new JarWatcher(new File(libPath),
						new LibraryFileChangeListener(app), app.config().getWatcherDelay());
	}

	/**
	 * useful for testing, destroy the container and clean all the states, also interrupt all
	 * watcher threads
	 */
	public void destroy() throws InterruptedException {
		app.destroy();
		bootstrapped = false;
		if (Objects.nonNull(serviceWatcher))
			serviceWatcher.destroy();
		if (Objects.nonNull(libWatcher))
			libWatcher.destroy();
		System.gc();
		log.info("ganjex shutdown correctly");
	}

	/**
	 * run the container. start watchers on library and service directory and detect any changes
	 * there. this method has been used in the complicated scenarios, usually clients use
	 * <code>Ganjex.run({@link GanjexConfiguration})</code> static method.
	 *
	 * @return ganjex container object
	 */
	public Ganjex run() {
		watchLibraryDirectory(app.config().getLibPath());
		new HookLoader(app).loadHooks(app.config().getBasePackage());
		app.lifecycleManagement().doneRegistering();
		watchServicesDirectory(app.config().getServicePath());
		bootstrapped = true;
		return this;
	}

}

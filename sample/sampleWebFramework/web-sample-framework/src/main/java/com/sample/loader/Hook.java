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

package com.sample.loader;

import com.behsa.ganjex.GanjexHook;
import com.behsa.ganjex.api.ServiceContext;
import com.behsa.ganjex.api.ShutdownHook;
import com.behsa.ganjex.api.StartupHook;
import com.sample.service.ServiceContainer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hekmatof
 */
@GanjexHook
public class Hook {
	@Autowired
	private ServiceContainer container;

	@StartupHook
	public void startService(ServiceContext context) {
		container.add(context);
	}

	@ShutdownHook
	public void shutdownService(ServiceContext context) {
		container.remove(context);
	}
}
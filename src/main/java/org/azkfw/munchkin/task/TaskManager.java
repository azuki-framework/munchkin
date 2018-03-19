/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.azkfw.munchkin.task;

import java.util.ArrayDeque;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * このクラスは、タスク管理を行うマネージャークラスです。
 * 
 * @author kawakicchi
 */
public class TaskManager {

	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskManager.class);

	private final Queue<Task> tasks;

	private Thread thread;

	private boolean reqStop;

	public TaskManager() {
		reqStop = false;
		tasks = new ArrayDeque<Task>();
	}

	public void queue(final Task task) {
		synchronized (tasks) {
			tasks.add(task);
		}
	}

	public void start() {
		synchronized (this) {
			if (null != thread && thread.isAlive()) {
				return;
			}
			reqStop = false;
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					doRun();
				}
			});
			thread.start();
		}
	}

	public void stop() {
		reqStop = true;
	}

	public void stopForWait() {
		reqStop = true;
		while (null != thread && thread.isAlive()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {
				LOGGER.warn("", ex);
			}
		}
	}

	private void doRun() {
		try {
			while (!reqStop) {
				Task task = null;
				synchronized (tasks) {
					task = tasks.poll();
				}

				if (null == task) {
					Thread.sleep(500);
				} else {

					task.execute();

				}
			}
		} catch (InterruptedException ex) {
			LOGGER.warn("", ex);
		}
	}
}

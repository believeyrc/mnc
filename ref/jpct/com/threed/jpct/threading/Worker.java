// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.threed.jpct.threading;

import java.util.Hashtable;
import java.util.Vector;

import com.threed.jpct.Logger;

// Referenced classes of package com.threed.jpct.threading:
//            WorkLoad

public class Worker {
	private class WorkerThread implements Runnable {

		public void run() {
			do {
				if (stop)
					break;
				if (queues[num].size() != 0) {
					WorkLoad workload = null;
					synchronized (queues) {
						workload = (WorkLoad) queues[num].elementAt(0);
						queues[num].removeElementAt(0);
					}
					try {
						workload.doWork();
					} catch (Exception exception1) {
						workload.error(exception1);
					}
					enqueued.removeElement(workload);
					subType(workload.getClass());
					workload.done();
				} else {
					try {
						boolean flag = false;
						synchronized (queues) {
							flag = !Thread.interrupted() && queues[num].size() == 0;
						}
						if (flag)
							Thread.sleep(20L);
					} catch (Exception exception) {
					}
				}
			} while (true);
			Logger.log("Worker thread stopped!", 2);
		}

		private int num;

		public WorkerThread(int i) {
			num = -1;
			num = i;
			Thread.currentThread().setPriority(9);
		}
	}

	public Worker(int i) {
		types = new Hashtable();
		waitingFor = new Vector();
		stop = false;
		if (i < 0)
			i = 1;
		threads = new Thread[i];
		queues = new Vector[i];
		enqueued = new Vector();
		if (i > 1) {
			for (int j = 0; j < i; j++) {
				Thread thread = new Thread(new WorkerThread(j));
				thread.setDaemon(true);
				threads[j] = thread;
				queues[j] = new Vector();
				thread.start();
			}

		}
		Logger.log("Worker created using " + i + " queue" + (i <= 1 ? "" : "s") + "!", 2);
	}

	public boolean isDone(WorkLoad workload) {
		if (scpu())
			return true;
		else
			return !enqueued.contains(workload);
	}

	public int getQueueCount() {
		return queues.length;
	}

	public void waitForAll() {
		if (scpu())
			return;
		waitingFor.addElement(Thread.currentThread());
		for (int i = types.size(); i > 0 && !stop;)
			try {
				if (!Thread.interrupted())
					Thread.sleep(20L);
				i = types.size();
			} catch (Exception exception) {
				i = types.size();
			}

		waitingFor.removeElement(Thread.currentThread());
	}

	public void waitFor(Class class1) {
		if (scpu())
			return;
		waitingFor.addElement(Thread.currentThread());
		for (boolean flag = types.containsKey(class1); flag && !stop;)
			try {
				if (!Thread.interrupted())
					Thread.sleep(20L);
				flag = types.containsKey(class1);
			} catch (Exception exception) {
				flag = types.containsKey(class1);
			}

		waitingFor.removeElement(Thread.currentThread());
	}

	public void add(WorkLoad workload, int i) {
		int j = i % queues.length;
		if (scpu() || threads[j] == Thread.currentThread()) {
			try {
				workload.doWork();
			} catch (Exception exception) {
				workload.error(exception);
			}
			workload.done();
			return;
		}
		synchronized (queues) {
			enqueued.addElement(workload);
			addType(workload.getClass());
			queues[j].addElement(workload);
			try {
				threads[j].interrupt();
			} catch (Exception exception1) {
				Logger.log("Unable to interrupt thread!", 1);
			}
		}
	}

	public void add(WorkLoad paramWorkLoad) {
		if (scpu()) {
			try {
				paramWorkLoad.doWork();
			} catch (Exception localException1) {
				paramWorkLoad.error(localException1);
			}
			paramWorkLoad.done();
			return;
		}
		int i = 0;
		synchronized (this.queues) {
			this.enqueued.addElement(paramWorkLoad);
			addType(paramWorkLoad.getClass());
			int j = 99999999;
			for (int k = 0; k < this.queues.length; ++k) {
				Vector localVector = this.queues[k];
				if (localVector.size() >= j)
					continue;
				j = localVector.size();
				i = k;
				if (j == 0)
					break;
			}
			if (this.threads[i] == Thread.currentThread()) {
				this.enqueued.removeElement(paramWorkLoad);
				try {
					paramWorkLoad.doWork();
				} catch (Exception localException2) {
					paramWorkLoad.error(localException2);
				}
				paramWorkLoad.done();
				return;
			}
			this.queues[i].addElement(paramWorkLoad);
			try {
				this.threads[i].interrupt();
			} catch (Exception localException3) {
				Logger.log("Unable to interrupt thread!", 1);
			}
		}
	}

	private void addType(Class class1) {
		synchronized (types) {
			Integer integer = (Integer) types.get(class1);
			if (integer == null) {
				types.put(class1, new Integer(1));
			} else {
				integer = new Integer(integer.intValue() + 1);
				types.put(class1, integer);
			}
		}
	}

	private void subType(Class class1) {
		synchronized (types) {
			Integer integer = (Integer) types.get(class1);
			if (integer == null)
				throw new RuntimeException("No entry found for class " + class1.getName());
			integer = new Integer(integer.intValue() - 1);
			if (integer.intValue() == 0)
				synchronized (waitingFor) {
					types.remove(class1);
					for (int i = 0; i < waitingFor.size(); i++)
						try {
							((Thread) waitingFor.elementAt(i)).interrupt();
						} catch (Exception exception) {
							Logger.log("Unable to interrupt thread!", 1);
						}

				}
			else
				types.put(class1, integer);
		}
	}

	private boolean scpu() {
		return queues.length == 1;
	}

	public void dispose() {
		if (!stop) {
			stop = true;
			for (int i = 0; i < threads.length; i++) {
				if (threads[i] == null)
					continue;
				try {
					threads[i].interrupt();
				} catch (Exception exception) {
					Logger.log("Unable to interrupt thread!", 1);
				}
			}

			boolean flag = true;
			int j = 0;
			do {
				flag = true;
				for (int k = 0; k < threads.length; k++)
					if (threads[k] != null)
						flag &= !threads[k].isAlive();

				if (!flag)
					try {
						Thread.sleep(5L);
					} catch (Exception exception1) {
					}
				j++;
			} while (!flag && j <= 250);
		}
	}

	public void finalize() {
		dispose();
	}

	private static int maxProcessorsUsed = -1;
	private Thread threads[];
	private Vector queues[];
	private Vector enqueued;
	private Hashtable types;
	private Vector waitingFor;
	private boolean stop;

}

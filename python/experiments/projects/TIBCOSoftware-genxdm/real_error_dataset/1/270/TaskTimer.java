/**
 * Copyright (c) 2010 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.samples.performance;

import java.util.ArrayList;

public class TaskTimer {
	private ArrayList<TaskTimer> m_subtasks;
	final private String m_name;
	private long m_start;
	private long m_elapsed;
	final private boolean m_checkMemory;
	private ArrayList<String> m_notes;
	private boolean m_printTimeUnits;
	
	public TaskTimer(String name)
	{
		this(name, false);
	}
	public TaskTimer(String name, boolean checkMemory)
	{
		m_name = name;
		m_elapsed = 0;
		m_start = 0;
		m_checkMemory = checkMemory;
	}
	private static final long MEGABYTE = 1024L * 1024L;

	public TaskTimer newChild(String name)
	{
		TaskTimer child = new TaskTimer(name, m_checkMemory);
		addTask(child);
		return child;
	}
	public void setPrintTimeUnits(boolean printTimeUnits)
	{
		m_printTimeUnits = printTimeUnits;
		if(m_subtasks != null)
		{
			for(TaskTimer child : m_subtasks)
			{
				child.setPrintTimeUnits(printTimeUnits);
			}
		}
	}
	public void addNote(String note)
	{
		if(m_notes == null)
		{
			m_notes = new ArrayList<String>();
		}
		m_notes.add(note);
	}
	public void startTimer()
	{
		m_start = System.nanoTime();
		m_elapsed = 0;
	}
	public void stopTimer()
	{
		if(m_start > 0)
		{
			m_elapsed += System.nanoTime() - m_start;
			if(m_checkMemory)
			{
				addNote("Before gc: " + checkMemory(false));
				addNote("After  gc: " + checkMemory(true));
			}
		}
	}
	public void pauseTimer()
	{
		m_elapsed += System.nanoTime() - m_start;
	}
	public void continueTimer()
	{
		m_start = System.nanoTime();
	}
	public long getElapsedTimeNanos()
	{
		return m_elapsed;
	}
	public double getElapsedTimeSeconds()
	{
		return m_elapsed / 1000d / 1000000d;
	}
	public long getGroupElapsedTimeNanos()
	{
		long elapsed = m_elapsed;
		for(TaskTimer subtask : getSubtasks())
		{
			elapsed += subtask.getGroupElapsedTimeNanos();
		}
		return elapsed;
	}
	public double getGroupElapsedTimeMillis()
	{
		return getGroupElapsedTimeNanos() / 1000000d;
	}
	public double getGroupElapsedTimeSeconds()
	{
		return getGroupElapsedTimeNanos() / 1000d / 1000000d;
	}
	public void addTask(TaskTimer taskTimer)
	{
		getSubtasks().add(taskTimer);
	}
	public ArrayList<TaskTimer> getSubtasks()
	{
		if(m_subtasks == null)
		{
			m_subtasks = new ArrayList<TaskTimer>();
		}
		return m_subtasks;
	}
	public String toString()
	{
		return toPrettyStringMillis("", 0);
	}
	public String toPrettyStringNanos(String indent, int includeSubtimes)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(indent).append(m_name).append(": ").append(getGroupElapsedTimeNanos());
		if(m_printTimeUnits)
		{
			sb.append(" ns");
		}
		sb.append('\n');
		if(m_notes != null)
		{
			for(String note : m_notes)
			{
				sb.append(indent).append('\t').append(note).append('\n');
			}
		}
		if(includeSubtimes > 0)
		{
			for(TaskTimer subtask : getSubtasks())
			{
				sb.append(subtask.toPrettyStringNanos(indent.concat("\t"), includeSubtimes-1));
			}
		}
		return sb.toString();
	}
	public String toPrettyStringMillis(String indent, int includeSubtimes)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(indent).append(m_name).append(": ").append(getGroupElapsedTimeMillis());
		if(m_printTimeUnits)
		{
			sb.append(" ms");
		}
		sb.append('\n');
		
		if(m_notes != null)
		{
			for(String note : m_notes)
			{
				sb.append(indent).append('\t').append(note).append('\n');
			}
		}
		if(includeSubtimes > 0)
		{
			for(TaskTimer subtask : getSubtasks())
			{
				sb.append(subtask.toPrettyStringMillis(indent.concat("\t"), includeSubtimes-1));
			}
		}
		return sb.toString();
	}
	public String toPrettyStringSeconds(String indent, int includeSubtimes)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(indent).append(m_name).append(": ").append(getGroupElapsedTimeSeconds());
		if(m_printTimeUnits)
		{
			sb.append(" sec");
		}
		sb.append('\n');
		
		if(m_notes != null)
		{
			for(String note : m_notes)
			{
				sb.append(indent).append('\t').append(note).append('\n');
			}
		}
		if(includeSubtimes > 0)
		{
			for(TaskTimer subtask : getSubtasks())
			{
				sb.append(subtask.toPrettyStringSeconds(indent.concat("\t"), includeSubtimes-1));
			}
		}
		return sb.toString();
	}
	public String toCsvMillis(int includeSubtimes)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(toCsvMillis("", includeSubtimes));
		return sb.toString();
	}
	private String toCsvMillis(String prepend, int includeSubtimes)
	{
		StringBuffer sb = new StringBuffer();
		if(includeSubtimes > 0)
		{
			for(TaskTimer subtask : getSubtasks())
			{
				sb.append(subtask.toCsvMillis(prepend + m_name + ", ", includeSubtimes-1));
			}
		}
		else
		{
			sb.append(prepend).append(m_name).append(",").append(getGroupElapsedTimeMillis());
			if(m_printTimeUnits)
			{
				sb.append(" ms");
			}
			sb.append('\n');
		}
		return sb.toString();
	}
	public static long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}
	public String checkMemory(boolean doGc)
	{
		// Get the Java runtime
		Runtime runtime = Runtime.getRuntime();
		if(doGc)
		{
			// Run the garbage collector
			runtime.gc();
		}
		// Calculate the used memory
		long memory = runtime.totalMemory() - runtime.freeMemory();
		String retVal = "Used memory: " + bytesToMegabytes(memory) + "Mb";
		return retVal;
	}
}

/**
 *
 *     Copyright 2013 KU Leuven Research and Development - iMinds - Distrinet
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     Administrative Contact: dnet-project-office@cs.kuleuven.be
 *     Technical Contact: bart.vanbrabant@cs.kuleuven.be
 */
package drm.taskworker.monitoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import drm.taskworker.Job;

public class Monitor implements IMonitor {

	private static Logger logger = Logger.getLogger(Monitor.class
			.getCanonicalName());

	@Override
	public Map<String, Set<Statistic>> getStats() {
		List<Job> in =Job.getAll();
		
		return getStats(in);
	}

	protected Map<String, Set<Statistic>> getStats(List<Job> in) {
		Map<String, Set<Statistic>> out = new HashMap<String, Set<Statistic>>();

		for(Job wf: in){
			if(wf.getStats()==null)
				wf.calcStats();
			if(wf.getStats() != null)
				out.put(wf.getJobId().toString(),new HashSet<>(wf.getStats()));
		}
		
		return out;
	}
	
	@Override
	public Map<String, Set<Statistic>> getStats(int lastN) {
		logger.fine("Retrieving last " + lastN + " stats");
		//FIXME: inefficient as cassandra can not 'order by'  
		List<Job> all = new ArrayList<>(Job.getAll());
		Collections.sort(all, new Comparator<Job>() {

			@Override
			public int compare(Job o1, Job o2) {
				return o1.getFinishedAt().compareTo(o2.getFinishedAt());
			}
		});
		
		int idx = Math.max(0, all.size()-11);
		all=all.subList(idx, all.size());
		
		Map<String, Set<Statistic>> out = new HashMap<String, Set<Statistic>>();

		for(Job wf: all){
			if(wf.getStats()==null)
				wf.calcStats();
			if(wf.getStats() != null)
				out.put(wf.getJobId().toString(),new HashSet<>(wf.getStats()));
		}
		
		return out;
	}

}

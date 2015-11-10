package com.apifest.oauth20.security;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 *
 * @author Edouard De Oliveira
 */
public class SubnetRange {
	private static Pattern pattern = Pattern.compile("[\\p{Blank},]");
	private List<SubnetUtils> ranges = new ArrayList<SubnetUtils>();
	
	private SubnetRange() {
	}
	
	/**
	 * Returns true if no allowedIPs set
	 */
	public boolean inRange(String addr) {
		if (ranges.isEmpty()) {
			return true;
		}

		for (SubnetUtils s : ranges) {
			if (s.getInfo().isInRange(addr))
				return true;
		}

		return false;
	}
	
	public static SubnetRange parse(String text, boolean inclusiveHostCount) throws IllegalArgumentException {
		String[] cidrs = pattern.split(text);
		SubnetRange sr = new SubnetRange();
		
		for (String cidr : cidrs) {
			if (cidr.indexOf('/') < 0) {
				cidr = cidr + "/32";
			}
			SubnetUtils net = new SubnetUtils(cidr);
			net.setInclusiveHostCount(inclusiveHostCount);

			if (net == null) {
				throw new IllegalArgumentException("Invalid subnet : " + cidr);
			}

			sr.ranges.add(net);
		}

		return sr;
	}
}

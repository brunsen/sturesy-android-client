/*
 * StuReSy - Student Response System
 * Copyright (C) 2012-2014  StuReSy-Team
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package sturesy.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import sturesy.items.Vote;

/**
 * 
 * @author b.brunsen
 *
 */
public class VoteFilter {

	public static void filterVotes(Collection<Vote> c, ValidVotePredicate predicate)
	{
		Iterator<Vote> iterator = c.iterator();
    	Collection<Vote> exclude = new ArrayList<Vote>();
    	while (iterator.hasNext())
    	{
    		Vote v = iterator.next();
    		if(!predicate.evaluate(v))
    		{
    			// Adds invalid votes into a list for later exclusion.
    			exclude.add(v);
    		}
    	}
    	// Only retain valid votes
    	c.removeAll(exclude);
	}
}

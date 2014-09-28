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
package sturesy.services.deserialization;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import sturesy.items.Vote;
import sturesy.items.vote.MultipleVote;
import sturesy.items.vote.SingleVote;
import sturesy.items.vote.TextVote;

/**
 * Class which handles deserialization of Votes.
 * @author b.brunsen
 *
 */
public class VoteImportservice {
	/**
	 * Receives a File object and returns a Hashmap of lists, which contain votes.
	 * @param file The file object, where the votes are stored.
	 * @throws XmlPullParserException
	 * @throws IOException 
	 */
	public Map<Integer, Set<Vote>> readFromFile(File file) throws XmlPullParserException, IOException
	{
		Map<Integer,Set<Vote>> votes = new HashMap<Integer, Set<Vote>>();
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		FileReader reader = new FileReader(file);
		parser.setInput(reader);
		processXML(parser, votes);
		reader.close();
		return votes;
	}
	/**
	 * Helper method of readFromFile. This method takes care of the actual deserialization.
	 * @param xpp XmlPullParser object.
	 * @param voteMap Hashmap, where votes are stored.
	 * @throws XmlPullParserException
	 * @throws IOException 
	 */
	private void processXML(XmlPullParser xpp, Map<Integer, Set<Vote>> voteMap) throws XmlPullParserException, IOException{
		int eventType = xpp.getEventType();
		Set<Vote> votes = new HashSet<Vote>();
		int number= 0;
		Vote vote;
		String guid ="";
		long tdiff = 0;
		ArrayList<Short> answers = new ArrayList<Short>();
		String answer = "";
		boolean reset = false;

        String lastStartTag = "";
        do{
        	if(reset)
        	{
        		vote = null;
        		guid = "";
        		tdiff = 0;
        		answers = new ArrayList<Short>();
        		answer = "";
        		reset = false;
        	}
        	if(eventType == XmlPullParser.START_TAG)
        	{
        		lastStartTag = xpp.getName();
        		if(lastStartTag.equals("number")){
        			number = Integer.decode(xpp.nextText());
        		}
        		else if(lastStartTag.equals("guid")){
        			guid = xpp.nextText();
        		}
        		else if(lastStartTag.equals("tdiff")){
        			tdiff = Long.decode(xpp.nextText());
        		}
        		else if(lastStartTag.equals("voting")||lastStartTag.equals("votes")){
        			answers.add(Short.decode(xpp.nextText()));
        		}
        		else if(lastStartTag.equals("answer")){
        			answer = xpp.nextText();
        		}
        	}
        	else if(eventType == XmlPullParser.END_TAG)
        	{
        		String lastEndTag = xpp.getName();
        		if(lastEndTag.equals("q"))
        		{
        			voteMap.put(number, votes);
        			votes = new HashSet<Vote>();
        		}
        		else if(lastEndTag.equals("vote")){
        			vote = new SingleVote(guid, tdiff, (int)answers.get(0));
        			votes.add(vote);
        			reset = true;
        		}
        		else if(lastEndTag.equals("multiplevote")){
        			vote = new MultipleVote(guid, tdiff, answers);
        			votes.add(vote);
        			reset = true;
        		}
        		else if(lastEndTag.equals("textvote")){
        			vote = new TextVote(guid, tdiff, answer);
        			votes.add(vote);
        			reset = true;
        		}
        	}
        	eventType = xpp.next();
        }while(eventType != XmlPullParser.END_DOCUMENT);
	}
}

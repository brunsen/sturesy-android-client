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
package sturesy.services.serialization;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import sturesy.core.backend.services.FileIOService;
import sturesy.items.Vote;
import sturesy.items.VotingSet;
import sturesy.items.vote.MultipleVote;
import sturesy.items.vote.SingleVote;
import sturesy.items.vote.TextVote;
/**
 * 
 * @author b.brunsen
 *
 */
public class VoteExportService {
	FileIOService _fileService;

	public VoteExportService() {
		_fileService = new FileIOService();
	}

	/**
	 * Saves this whole VotingSet in XML-Format to a file
	 * 
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public void createAndUpdateVoting(VotingSet votingSet, String filename)
			throws IOException {
		filename = filename.replace(".xml", "_voting.xml");
		String xml = votingSetToXml(votingSet);
		File f = new File(filename);
		_fileService.writeToFileCreateIfNotExist(f, xml);
	}

	/**
	 * Helper method for writeToFile. Builds xml structure and parses
	 * information to string.
	 * 
	 * @param questionSet
	 * @return Xml String
	 */
	private String votingSetToXml(VotingSet votes) {
		StringBuilder sb = new StringBuilder();
		sb.append("<results>" + "\n");
		for (int i = 0; i < votes.getQuestionSize(); i++)
		{
			Set<Vote> votesFor = votes.getVotesFor(i);
			if (votesFor != null && !votesFor.isEmpty())
			{
				sb.append("<q>" + "\n");
				sb.append("<number>" + i + "</number>" + "\n");
				Iterator<Vote> iterator = votesFor.iterator();
				while (iterator.hasNext())
				{
					Vote v = iterator.next();
					if (v instanceof SingleVote)
					{
						// Save a singlechoice vote
						SingleVote single = (SingleVote) v;
						sb.append("<vote>" + "\n");
						saveGeneralInfo(sb, single);
						sb.append("<voting>" + single.getVote() + "</voting>" + "\n");
						sb.append("</vote>" + "\n");

					} else if (v instanceof MultipleVote)
					{
						// Save a multiplechoice vote
						MultipleVote multi = (MultipleVote) v;
						sb.append("<multiplevote>" + "\n");
						saveGeneralInfo(sb, multi);
						short[] multiVotes = multi.getVotes();
						for (Short s : multiVotes)
						{
							sb.append("<votes>" + s + "</votes>" + "\n");
						}
						sb.append("</multiplevote>" + "\n");
					} else if (v instanceof TextVote)
					{
						// Save a text vote
						TextVote textVote = (TextVote) v;
						sb.append("<textvote>" + "\n");
						saveGeneralInfo(sb, textVote);
						sb.append("<answer>" + textVote.getAnswer() +"</answer>" + "\n");
						sb.append("</textvote>" + "\n");
					}
				}
				sb.append("</q>" + "\n");
			}
		}
		sb.append("</results>" + "\n");
		return sb.toString();
	}
	
	/**
	 * Writes general information about a vote into a string builder.
	 * @param sb
	 * @param v
	 */
	public void saveGeneralInfo(StringBuilder sb, Vote v)
	{
		sb.append("<guid>" + v.getGuid() + "</guid>" + "\n");
		sb.append("<tdiff>" + v.getTimeDiff() + "</tdiff>" + "\n");
	}
	
	/**
	 * Creates a csv file with the result of a voting
	 * 
	 * @param votes
	 *            the votes for which the result file should be build
	 * @param file
	 *            the file where it has to be stored
	 * @throws IOException
	 */
	public void saveVotingResult(Set<Vote> votes, File file) throws IOException {
		if (!votes.isEmpty())
		{
			String content = createCSVContent(votes);
			if (file != null)
			{
				_fileService.createCSVFileIfNotExist(file);
				_fileService.writeToFile(file, content);
			}
		}
	}

	/**
	 * places the votes in a comma separated string
	 * 
	 * @param votes
	 *            the votes to be parse
	 * @return comma separated string
	 */
	private String createCSVContent(Set<Vote> votes) {
		StringBuffer content = new StringBuffer();
		for (Vote v : votes)
		{
			String guid = v.getGuid();
			char voteAsUpperCase = (char) ('A' + v.getVote());
			long timediff = v.getTimeDiff();
			content.append(guid + "," + voteAsUpperCase + "," + timediff + "\n");
		}
		return content.toString();
	}

}
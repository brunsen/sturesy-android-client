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
package sturesy.services;

import java.util.Arrays;
import java.util.Set;

import sturesy.items.MultipleChoiceQuestion;
import sturesy.items.QuestionModel;
import sturesy.items.SingleChoiceQuestion;
import sturesy.items.TextQuestion;
import sturesy.items.Vote;
import sturesy.items.vote.MultipleVote;
import sturesy.items.vote.TextVote;

/**
 * Class which takes care of managing votes and converting them into a format,
 * which other classes use. This class is introduced to reduce shared code.
 * 
 * @author b.brunsen
 *
 */
public class VotingResultCalculator {

	/**
	 * Converts a set of votes into an array representing those votes
	 * 
	 * @param qm
	 *            Question model (Singlechoice, MultipleChoice or TextChoice)
	 * @param votes
	 *            a Set of votes to be converted.
	 * @return
	 */
	public float[] calculateVotes(QuestionModel qm, Set<Vote> votes) {
		float[] votesarr = new float[] {};
		if (qm instanceof SingleChoiceQuestion)
		{
			votesarr = new float[qm.getAnswers().size()];
			Arrays.fill(votesarr, 0);
			for (Vote vote : votes)
			{
				if (vote.getVote() < votesarr.length && vote.getVote() >= 0)
				{
					votesarr[vote.getVote()]++;
				}
			}
		} else if (qm instanceof MultipleChoiceQuestion)
		{
			votesarr = new float[qm.getAnswers().size()];
			Arrays.fill(votesarr, 0);
			for (Vote vote : votes)
			{
				MultipleVote mv = (MultipleVote) vote;
				for (short shortVote : mv.getVotes())
                {
                    if (shortVote < votesarr.length && shortVote >= 0)
                    {
                        votesarr[shortVote]++;
                    }
                }
			}
		} else if (qm instanceof TextQuestion)
		{
			TextQuestion tq = (TextQuestion) qm;
			votesarr = new float[2];
			Arrays.fill(votesarr, 0);
			for (Vote vote : votes)
			{
				TextVote tv = (TextVote) vote;

				if (tq.matchesPercentage(tv.getAnswer()))
				{
					votesarr[0]++;
				} else
				{
					votesarr[1]++;
				}
			}
		}
		return votesarr;
	}
}

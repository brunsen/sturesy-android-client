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
package sturesy.android.controllers.voting;

import java.util.Set;

import org.achartengine.GraphicalView;

import sturesy.android.controllers.votinganalysis.BarChartFactory;
import sturesy.items.QuestionModel;
import sturesy.items.Vote;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import de.uhh.sturesy_android.R;
/**
 * 
 * @author b.brunsen
 *
 */
public class VotingResulstFragment extends Fragment {
	private QuestionModel _questionModel;
	private Set<Vote> _votesToDisplay;
	private boolean _showCorrect;
	private LinearLayout _graphArea;

	public VotingResulstFragment(QuestionModel qm, Set<Vote> votes) {
		_questionModel = qm;
		_votesToDisplay = votes;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.voting_results_fragment,
				container, false);
		_graphArea = (LinearLayout) view.findViewById(R.id.voting_results_bar_area);
		setContent(_questionModel,_votesToDisplay ,view);
		return view;
	}

	/**
	 * Sets the data for the result view.
	 * 
	 * @param qm
	 * @param votes
	 */
	public void setContent(QuestionModel qm,  Set<Vote> votes, View v) {
		_showCorrect = false;
		BarChartFactory factory = new BarChartFactory(getActivity());
		GraphicalView barChartView = factory.generateBar(votes, qm, false);
		_graphArea.addView(barChartView);
	}

	/**
	 * Clears bar chart and replaces old bar chart with new one.
	 * Switches display of correct answer.
	 * @param v
	 */
	
	public void swapAnswerBars(View v) {
		_graphArea.removeAllViews();
		GraphicalView barChartView;
		BarChartFactory factory = new BarChartFactory(getActivity());
		if (_showCorrect) {
			barChartView = factory.generateBar(_votesToDisplay, _questionModel, false);
		}

		else {
			barChartView = factory.generateBar(_votesToDisplay, _questionModel, true);
		}
		_graphArea.addView(barChartView);
		_showCorrect = !_showCorrect;
	}
}

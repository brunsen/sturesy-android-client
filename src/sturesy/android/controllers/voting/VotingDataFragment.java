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

import java.util.List;

import sturesy.items.QuestionModel;
import sturesy.items.TextQuestion;
import sturesy.util.HTMLLabel;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.uhh.sturesy_android.R;
/**
 * 
 * @author b.brunsen
 *
 */
public class VotingDataFragment extends Fragment {
	private TextView _questionPanel;
	private LinearLayout _answerPanel;
	private QuestionModel _questionModel;

	public VotingDataFragment(QuestionModel qm) {
		_questionModel = qm;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.voting_data_fragment, container,
				false);
		_questionPanel = (TextView) view.findViewById(R.id.questionText);
		_answerPanel = (LinearLayout) view.findViewById(R.id.Answer_Panels);
		String question = _questionModel.getQuestion();
		question = HTMLLabel.generateHTLMString(question);
		_questionPanel.setText(Html.fromHtml(question));
		_questionPanel.setTextSize((float) 20.0);
//		if (!getArguments().containsKey("picture"))
//		{
			ImageView img = (ImageView) view.findViewById(R.id.voting_Image);
			img.setVisibility(View.GONE);
//		}
		if(!(_questionModel instanceof TextQuestion))
		{
			List<String> answers = _questionModel.getAnswers();
			for (int i = 0; i < answers.size(); i++)
			{
				String answerText = "";
				TextView questionTextView = new TextView(getActivity());
				answerText += "<html><body> <center>" + (char) ('A' + i) + ": " + answers.get(i)
						+ "</center></body></html>";
				questionTextView.setText(Html.fromHtml(answerText));
				questionTextView.setTextSize((float)18.0);
				questionTextView.setGravity(Gravity.CENTER_HORIZONTAL);
				_answerPanel.addView(questionTextView);
			}
		}
		return view;
	}

}

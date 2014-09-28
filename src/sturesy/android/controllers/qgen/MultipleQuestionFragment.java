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
package sturesy.android.controllers.qgen;

import sturesy.android.controllers.HtmlEditorActivity;
import sturesy.items.MultipleChoiceQuestion;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.swipedismiss.SwipeDismissListViewTouchListener;

import de.uhh.sturesy_android.R;

public class MultipleQuestionFragment extends Fragment implements
		OnClickListener, OnItemClickListener {

	private MultipleChoiceQuestion _questionModel;
	private Button _questionButton;
	private Button _deselectButton;
	private Button _addAnswerButton;
	private MultipleAnswerListAdapter _answerAdapter;
	private ListView _answerListView;
	private QuestionListAdapter _questionAdapter;

	public MultipleQuestionFragment(MultipleChoiceQuestion qm,
			QuestionListAdapter adapter) {
		_questionModel = qm;
		_questionAdapter = adapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_non_text_choice,
				container, false);
		initComponents(view);
		setListener();
		setData();
		return view;
	}

	private void initComponents(View view) {
		_questionButton = (Button) view.findViewById(R.id.questionButton);
		_answerListView = (ListView) view.findViewById(R.id.editorAnswers);
		_deselectButton = (Button) view.findViewById(R.id.deselect);
		_addAnswerButton = (Button) view.findViewById(R.id.addAnswer);
	}

	private void setListener() {
		_answerListView.setOnItemClickListener(this);
		_questionButton.setOnClickListener(this);
		_deselectButton.setOnClickListener(this);
		_addAnswerButton.setOnClickListener(this);

	}

	private void setData() {
		_answerAdapter = new MultipleAnswerListAdapter(getActivity(),
				R.layout.multiple_answer_list_item, _questionModel);
		_answerListView.setAdapter(_answerAdapter);
		setTouchListener(_answerListView, _answerAdapter);
		_questionButton.setText(_questionModel.getQuestion());
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.questionButton)
		{
			Intent editorIntent = new Intent(v.getContext(),
					HtmlEditorActivity.class);
			editorIntent.putExtra("Position", -1);
			editorIntent
					.putExtra("Question", ((Button) v).getText().toString());
			startActivityForResult(editorIntent, 1);
		}
		if (v.getId() == R.id.addAnswer)
		{
			if (_questionModel.getAnswerSize() < 10)
			{
				String newAnswer = getString(R.string.new_answer);
				_questionModel.getAnswers().add(newAnswer);
				_answerAdapter.notifyDataSetChanged();
			}
		}
		if (v.getId() == R.id.deselect)
		{
			_answerAdapter.resetCorrectAnswer();
			_answerAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent editorIntent = new Intent(view.getContext(),
				HtmlEditorActivity.class);
		editorIntent.putExtra("Position", position);
		editorIntent.putExtra("Answer",
				_questionModel.getAnswers().get(position));
		startActivityForResult(editorIntent, 1);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1)
		{

			if (resultCode == android.app.Activity.RESULT_OK
					&& data.getExtras().getInt("Position") == -1)
			{
				String text = data.getExtras().getString("Question");
				_questionButton.setText(text);
				_questionModel.setQuestion(text);
				_questionAdapter.notifyDataSetChanged();
			}
			if (resultCode == android.app.Activity.RESULT_OK
					&& data.getExtras().getInt("Position") != -1)
			{
				int position = data.getExtras().getInt("Position");
				String text = data.getExtras().getString("Answer");
				_questionModel.getAnswers().set(position, text);
				_answerAdapter.notifyDataSetChanged();
			}

		}

	}

	public <T> void setTouchListener(ListView listView,
			final ArrayAdapter<T> adapter) {
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				listView,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return true;
					}

					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions)
						{
							adapter.remove(adapter.getItem(position));
						}
						if (adapter instanceof MultipleAnswerListAdapter)
						{
							((MultipleAnswerListAdapter) adapter)
									.resetCorrectAnswer();
						}
						adapter.notifyDataSetChanged();
					}
				});
		listView.setOnTouchListener(touchListener);
	}
}

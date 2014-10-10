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

import sturesy.android.controllers.TextInputDialog;
import sturesy.items.SingleChoiceQuestion;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;

import de.uhh.sturesy_android.R;

/**
 * 
 * @author b.brunsen
 *
 */
public class SingleQuestionFragment extends Fragment implements
		OnClickListener, OnItemClickListener, OnTouchListener, TextWatcher {
	private SingleChoiceQuestion _questionModel;
	private EditText _questionEditText;
	private Button _deselectButton;
	private Button _addAnswerButton;
	private EditText _timepicker;
	private int _focusedTextID;
	private SingleAnswerListAdapter _answerAdapter;
	private DynamicListView _answerListView;
	private QuestionListAdapter _questionAdapter;

	public SingleQuestionFragment(SingleChoiceQuestion qm,
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
		_questionEditText = (EditText) view.findViewById(R.id.questionEditText);
		_answerListView = (DynamicListView) view
				.findViewById(R.id.editorAnswers);
		_deselectButton = (Button) view.findViewById(R.id.deselect);
		_addAnswerButton = (Button) view.findViewById(R.id.addAnswer);
		_timepicker = (EditText) view.findViewById(R.id.timePicker);
	}

	private void setListener() {
		_answerListView.setOnItemClickListener(this);
		_questionEditText.setOnClickListener(this);
		_deselectButton.setOnClickListener(this);
		_addAnswerButton.setOnClickListener(this);
		_timepicker.setOnTouchListener(this);
		_timepicker.addTextChangedListener(this);
		_questionEditText.setOnTouchListener(this);
		_questionEditText.addTextChangedListener(this);

	}

	private void setData() {
		_answerAdapter = new SingleAnswerListAdapter(getActivity(),
				R.layout.single_answer_list_item, _questionModel);
		_answerListView.setAdapter(_answerAdapter);
		setTouchListener(_answerListView, _answerAdapter);
		_questionEditText.setText(_questionModel.getQuestion());
		if (_questionModel.getDuration() != -1)
		{
			_timepicker.setText("" + _questionModel.getDuration());
		}
		_focusedTextID = -1;
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.addAnswer)
		{
			if (_questionModel.getAnswerSize() < 10)
			{
				String newAnswer = getString(R.string.new_answer);
				_questionModel.addAnswer(newAnswer);
				_answerAdapter.notifyDataSetChanged();
			}
		}
		if (v.getId() == R.id.deselect)
		{
			_answerAdapter.setSelectedIndex(-1);
			_answerAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final int selectedPosition = position;
		final TextInputDialog input = new TextInputDialog(getActivity(),
				getString(R.string.enter_answer));
		input.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				String updatedText = input.getText();
				if (!updatedText.equals(""))
				{
					_questionModel.getAnswers().set(selectedPosition,
							updatedText);
					_answerAdapter.notifyDataSetChanged();
				}
			}
		});
		input.show();
	}

	public <T> void setTouchListener(DynamicListView listView,
			final ArrayAdapter<T> adapter) {
		SimpleSwipeUndoAdapter swipeUndoAdapter = new SimpleSwipeUndoAdapter(
				adapter, getActivity(), new OnDismissCallback() {
					@Override
					public void onDismiss(final ViewGroup listView,
							final int[] reverseSortedPositions) {

						for (int position : reverseSortedPositions)
						{
							adapter.remove(adapter.getItem(position));
						}
						if (adapter instanceof SingleAnswerListAdapter)
						{
							((SingleAnswerListAdapter) adapter)
									.setSelectedIndex(-1);
							_questionModel.setCorrectAnswer(-1);
						}
						adapter.notifyDataSetChanged();

					}
				});
		swipeUndoAdapter.setAbsListView(listView);
		listView.setAdapter(swipeUndoAdapter);
		listView.enableSimpleSwipeUndo();
	}

	// Textwatcher methods
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String updatedValue = "" + s;
		switch (_focusedTextID) {
		case R.id.questionEditText:
			if (!updatedValue.equals(""))
			{
				_questionModel.setQuestion(updatedValue);
				_questionAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.timePicker:
			// Handle events to time picker
			if (updatedValue.equals(""))
			{
				_questionModel.setDuration(-1);
			} else
			{
				_questionModel.setDuration(Integer.parseInt(updatedValue));
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	// Touchevent listener methods
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		_focusedTextID = v.getId();
		return false;
	}
}
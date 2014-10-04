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

import sturesy.items.TextQuestion;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import de.uhh.sturesy_android.R;

/**
 * 
 * @author b.brunsen
 *
 */
public class TextQuestionFragment extends Fragment implements
		OnCheckedChangeListener, OnTouchListener, TextWatcher {
	private CheckBox _ignoreCaseCheckbox;
	private CheckBox _ignoreWhiteSpaceCheckbox;
	private TextQuestion _questionModel;
	private EditText _questionEdit;
	private EditText _timepicker;
	private EditText _answerTextField;
	private EditText _toleranceTextField;
	private int _focusedTextID;
	private QuestionListAdapter _questionAdapter;

	public TextQuestionFragment(TextQuestion textQuestion,
			QuestionListAdapter adapter) {
		_questionModel = textQuestion;
		_questionAdapter = adapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_text_question,
				container, false);
		initComponents(view);
		setData();
		setListeners();

		return view;
	}

	private void setData() {
		_answerTextField.setText(_questionModel.getAnswer());
		String tolerance = "" + _questionModel.getTolerance();
		_questionEdit.setText(_questionModel.getQuestion());
		_toleranceTextField.setText(tolerance);
		_ignoreCaseCheckbox.setChecked(_questionModel.isIgnoreCase());
		_ignoreWhiteSpaceCheckbox.setChecked(_questionModel.isIgnoreSpaces());
		_focusedTextID = -1;
		if (_questionModel.getDuration() != -1)
		{
			_timepicker.setText("" + _questionModel.getDuration());
		}
	}

	private void setListeners() {
		_ignoreCaseCheckbox.setOnCheckedChangeListener(this);
		_ignoreWhiteSpaceCheckbox.setOnCheckedChangeListener(this);
		_questionEdit.setOnTouchListener(this);
		_questionEdit.addTextChangedListener(this);
		_answerTextField.setOnTouchListener(this);
		_answerTextField.addTextChangedListener(this);
		_toleranceTextField.setOnTouchListener(this);
		_toleranceTextField.addTextChangedListener(this);
		_timepicker.setOnTouchListener(this);
		_timepicker.addTextChangedListener(this);
	}

	private void initComponents(View view) {
		_ignoreCaseCheckbox = (CheckBox) view
				.findViewById(R.id.ignore_case_checkbox);
		_ignoreWhiteSpaceCheckbox = (CheckBox) view
				.findViewById(R.id.ignore_whitespace_checkbox);
		_questionEdit = (EditText) view.findViewById(R.id.questionEditText);
		_answerTextField = (EditText) view.findViewById(R.id.answer_edit);
		_toleranceTextField = (EditText) view.findViewById(R.id.tolerance_edit);
		_timepicker = (EditText) view.findViewById(R.id.timePicker);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.ignore_case_checkbox)
		{
			boolean ignore = _questionModel.isIgnoreCase();
			_questionModel.setIgnoreCase(!ignore);
		}

		if (buttonView.getId() == R.id.ignore_whitespace_checkbox)
		{
			boolean ignore = _questionModel.isIgnoreSpaces();
			_questionModel.setIgnoreSpaces(!ignore);
		}
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
		case R.id.answer_edit:
			// Handle events on answer edit text
			if (!updatedValue.equals(""))
			{
				_questionModel.setAnswer(updatedValue);
			}
			break;

		case R.id.tolerance_edit:
			// Handle events on tolerance edit text
			if (!updatedValue.equals(""))
			{
				_questionModel.setTolerance(Integer.parseInt(updatedValue));
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
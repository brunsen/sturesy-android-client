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
import sturesy.items.TextQuestion;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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
public class TextQuestionFragment extends Fragment implements OnClickListener,
		OnCheckedChangeListener {

	private CheckBox _ignoreCaseCheckbox;
	private CheckBox _ignoreWhiteSpaceCheckbox;
	private TextQuestion _questionModel;
	private Button _questionButton;
	private EditText _answerTextField;
	private EditText _toleranceTextField;
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
		_questionButton.setText(_questionModel.getQuestion());
		_toleranceTextField.setText(tolerance);
		_ignoreCaseCheckbox.setChecked(_questionModel.isIgnoreCase());
		_ignoreWhiteSpaceCheckbox.setChecked(_questionModel.isIgnoreSpaces());
	}

	private void setListeners() {
		_questionButton.setOnClickListener(this);
		_ignoreCaseCheckbox.setOnCheckedChangeListener(this);
		_ignoreWhiteSpaceCheckbox.setOnCheckedChangeListener(this);
		_answerTextField.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String value = "" + s;
				if (!value.equals(""))
				{
					_questionModel.setAnswer(value);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		_toleranceTextField.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				_questionModel.setTolerance(Integer.parseInt("" + s));
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void initComponents(View view) {
		_ignoreCaseCheckbox = (CheckBox) view
				.findViewById(R.id.ignore_case_checkbox);
		_ignoreWhiteSpaceCheckbox = (CheckBox) view
				.findViewById(R.id.ignore_whitespace_checkbox);
		_questionButton = (Button) view.findViewById(R.id.questionButton);
		_answerTextField = (EditText) view.findViewById(R.id.answer_edit);
		_toleranceTextField = (EditText) view.findViewById(R.id.tolerance_edit);
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
		}

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
}
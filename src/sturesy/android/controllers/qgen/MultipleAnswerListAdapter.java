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

import java.util.Collections;
import java.util.List;

import sturesy.items.MultipleChoiceQuestion;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import de.uhh.sturesy_android.R;

public class MultipleAnswerListAdapter extends ArrayAdapter<String>{
	
	private List<Integer> _selectedIndex;
	private List<String> _items;
	private MultipleChoiceQuestion _multipleQuestion;
	private int _layoutResourceId;
	private Context _context;
	
	public MultipleAnswerListAdapter(Context context, int resource, MultipleChoiceQuestion qm) {
		super(context, resource, qm.getAnswers());
		_layoutResourceId = R.layout.multiple_answer_list_item;
		_context = context;
		_multipleQuestion = qm;
		_items = qm.getAnswers();
		_selectedIndex = qm.getCorrectAnswers();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final int currentPosition = position;
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = ((Activity) _context).getLayoutInflater();
			row = inflater.inflate(_layoutResourceId, parent, false);
		}
		String text = _items.get(position);
		TextView answerTextView = (TextView) row.findViewById(R.id.AnswerText);
		if (text != null) {
			if (answerTextView != null) {
				answerTextView.setText((CharSequence) text);
			}
		}

		
		final CheckBox check = (CheckBox) row.findViewById(R.id.correct_anwer_check);

		if (_selectedIndex.contains(position)) {
			check.setChecked(true);
		} else {
			check.setChecked(false);
		}
		check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Integer parsedPosition = Integer.valueOf(position);
				if (_selectedIndex.contains(parsedPosition)) {
					_selectedIndex.remove(parsedPosition);
				} else {
					_selectedIndex.add(parsedPosition);
				}
				notifyDataSetChanged();
			}
		});

		ImageButton up = (ImageButton) row.findViewById(R.id.answerUp);
		up.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				swapItems(currentPosition, -1);
				notifyDataSetChanged();

			}
		});

		ImageButton down = (ImageButton) row.findViewById(R.id.answerDown);
		down.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				swapItems(currentPosition, 1);
				notifyDataSetChanged();

			}
		});
		return row;
	}

	/**
	 * Method to swap elements in the list in a specific direction.
	 * 
	 * @param index
	 *            index inside the list
	 * @param direction
	 *            -1 up; 1 down
	 */
	public void swapItems(int index, int direction) {
		if ((index != 0 && direction == -1)
				|| (index != _items.size() - 1 && direction == 1)) {
			Collections.swap(_items, index, index + direction);

			
			if (getSelectedIndex().contains(index)&& !getSelectedIndex().contains(index + direction)) {
				_selectedIndex.add(index + direction);
				_selectedIndex.remove(index);
			}
			
			else if(!getSelectedIndex().contains(index)&& getSelectedIndex().contains(index + direction)){
				_selectedIndex.add(index);
				_selectedIndex.remove(index+direction);
			}
		}
	}

	public void addSelectedIndex(int index) {
		_selectedIndex.add(index);
		_multipleQuestion.setCorrectAnswers(_selectedIndex);
	}

	public List<Integer> getSelectedIndex() {
		return _selectedIndex;
	}
	
	public void resetCorrectAnswer(){
		_multipleQuestion.getCorrectAnswers().clear();
	}
}

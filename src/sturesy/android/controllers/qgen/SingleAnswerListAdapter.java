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

import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;

import sturesy.items.SingleChoiceQuestion;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import de.uhh.sturesy_android.R;

/**
 * 
 * @author b.brunsen
 *
 */
public class SingleAnswerListAdapter extends ArrayAdapter<String> implements
		UndoAdapter {

	private int _selectedIndex;
	private List<String> _items;
	private SingleChoiceQuestion _singleQuestion;
	private int _layoutResourceId;
	private Context _context;

	public SingleAnswerListAdapter(Context context, int resource,
			SingleChoiceQuestion qm) {
		super(context, resource, qm.getAnswers());
		_layoutResourceId = R.layout.single_answer_list_item;
		_context = context;
		_singleQuestion = qm;
		_items = (List<String>) qm.getAnswers();
		_selectedIndex = qm.getCorrectAnswer();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int currentPosition = position;
		View row = convertView;
		if (row == null)
		{
			LayoutInflater inflater = ((Activity) _context).getLayoutInflater();
			row = inflater.inflate(_layoutResourceId, parent, false);
		}
		String text = _items.get(position);
		TextView answerTextView = (TextView) row.findViewById(R.id.AnswerText);
		if (text != null)
		{
			if (answerTextView != null)
			{
				answerTextView.setText((CharSequence) text);
			}
		}

		RadioButton rb = (RadioButton) row
				.findViewById(R.id.correct_anwer_radio);

		if (getSelectedIndex() == position)
		{
			rb.setChecked(true);
		} else
		{
			rb.setChecked(false);
		}
		rb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedIndex(currentPosition);
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
				|| (index != _items.size() - 1 && direction == 1))
		{
			Collections.swap(_items, index, index + direction);
			if (index == getSelectedIndex())
			{
				setSelectedIndex(index + direction);
			}

			else if (index + direction == getSelectedIndex())
			{
				setSelectedIndex(index);
			}
		}
	}

	public void setSelectedIndex(int index) {
		_selectedIndex = index;
		_singleQuestion.setCorrectAnswer(_selectedIndex);
	}

	public int getSelectedIndex() {
		return _selectedIndex;
	}

	@Override
	public View getUndoClickView(View view) {
		return view.findViewById(R.id.undo_row_undobutton);
	}

	@Override
	public View getUndoView(int arg0, View arg1, ViewGroup parent) {
		View view = arg1;
		if (view == null)
		{
			view = LayoutInflater.from(getContext()).inflate(R.layout.undo_row,
					parent, false);
		}
		return view;
	}
}

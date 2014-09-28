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
package sturesy.android.controllers.votinganalysis;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import sturesy.android.controllers.ErrorDialog;
import sturesy.android.controllers.FileImportDialog;
import sturesy.core.Log;
import sturesy.core.backend.filter.file.LectureFileFilter;
import sturesy.items.QuestionModel;
import sturesy.items.QuestionSet;
import sturesy.items.Vote;
import sturesy.services.deserialization.QuestionImportService;
import sturesy.services.deserialization.VoteImportservice;
import sturesy.util.HTMLLabel;
import sturesy.util.ValidVotePredicate;
import sturesy.util.VoteFilter;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import de.uhh.sturesy_android.R;
/**
 * 
 * @author b.brunsen
 *
 */
public class VotingAnalysisActivity extends Activity {
	private Fragment _currentFragment;
	private TextView _progessTextView;
	private int _currentQuestion;

	private QuestionSet _questionSet;
	private Map<Integer, Set<Vote>> _votes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voting_analysis);
		initComponents();
		_progessTextView.setText("0/0");
		_votes = null;
		_currentQuestion = -1;
	}

	/**
	 * Inits android gui components and loads them into fields.
	 */
	private void initComponents() {
		_progessTextView = (TextView) findViewById(R.id.evaluation_progress);
	}

	/**
	 * Loads the contents of a menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.evaluation, menu);
		return true;
	}

	/**
	 * Specifies what happens, if a menu item has been selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.open_results:
			openResults();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Opens a dialog to choose the voting results from. Triggers file
	 * processing, if a valid file has been selected.
	 */
	private void openResults() {
		String title = getString(R.string.title_load_votes);
		final FileImportDialog dialog = new FileImportDialog(this,
				new LectureFileFilter(), title);
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog2) {
				File f = dialog.getSelectedFile();
				if (f != null)
				{
					readFile(f);
				}
			}
		});
		dialog.show();
	}

	/**
	 * Reads a given file, triggers xml parsing and updates on gui components.
	 * 
	 * @param file
	 */
	public void readFile(File file) {
		VoteImportservice service = new VoteImportservice();
		QuestionImportService questionService = new QuestionImportService();
		try
		{
			_questionSet = questionService.readQuestionSetWithParser(file);
			File votingFile = new File(file.getAbsolutePath().replace(".xml",
					"_voting.xml"));
			_votes = service.readFromFile(votingFile);
			filter(_votes);
			_currentQuestion = 0;
			updateGuiComponents();

		} catch (XmlPullParserException e)
		{
			Log.error(e.getClass().getSimpleName(), e);
			String message = getString(R.string.error_reading_votes_from_file);
			ErrorDialog alert = new ErrorDialog(this,
					getString(R.string.error), message);
			alert.show();
		} catch (IOException e)
		{
			Log.error(e.getClass().getSimpleName(), e);
			String message = getString(R.string.error_reading_file);
			ErrorDialog alert = new ErrorDialog(this,
					getString(R.string.error), message);
			alert.show();
			e.printStackTrace();
		}
	}

	/**
	 * Updates the gui with the results of the previous voting from the given
	 * question set.
	 * 
	 * @param v
	 */
	public void previousButtonAction(View v) {
		boolean hasPreviousAction = _currentQuestion > 0;
		if (hasPreviousAction)
		{
			_currentQuestion--;
			updateGuiComponents();
		}

	}

	/**
	 * Updates the gui with the results of the next voting from the given
	 * question set.
	 * 
	 * @param v
	 */
	public void nextButtonAction(View v) {
		if (_currentQuestion >= 0)
		{
			boolean hasNextQuestion = _currentQuestion < _questionSet.size() - 1;
			if (hasNextQuestion)
			{
				_currentQuestion++;
				updateGuiComponents();
			}
		}
	}

	/**
	 * Updates gui components with data from the the questionset and the voting
	 * results.
	 */
	private void updateGuiComponents() {
		_progessTextView.setText("" + (_currentQuestion + 1) + "/"
				+ _questionSet.size());
		QuestionModel questionModel = _questionSet.getIndex(_currentQuestion);
		Set<Vote> votesToDisplay = _votes.get(_currentQuestion);
		String question = questionModel.getQuestion();
		if (votesToDisplay != null)
		{
			
			FragmentManager manager = getFragmentManager();
			
			if (manager.findFragmentByTag("QuestionFragment") == null)
			{
				FragmentTransaction transaction = manager.beginTransaction();
				_currentFragment = new VotingAnalysisFragment(questionModel, votesToDisplay);
				transaction.add(R.id.voting_analysis_fragment,
						_currentFragment, "QuestionFragment");
				transaction.commit();
			} else
			{
				((VotingAnalysisFragment)_currentFragment).updateQuestion(questionModel, votesToDisplay);
			}
			
		} else
		{
			// TODO: Prompt toast with hint "no data"
			// TODO: Show empty Fragment exists
			question = HTMLLabel.generateHTLMString(question);
		}

	}

	/**
	 * Filters all Votes which are not actually matching the associated question
	 * 
	 * @param votes
	 */
	private void filter(Map<Integer, Set<Vote>> votes) {
		for (Integer index : votes.keySet())
		{
			int upperbound = _questionSet.getIndex(index).getAnswerSize();
			VoteFilter.filterVotes(votes.get(index), new ValidVotePredicate(
					upperbound));
		}
	}

}

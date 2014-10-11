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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import sturesy.android.controllers.ErrorDialog;
import sturesy.android.controllers.FileImportDialog;
import sturesy.android.controllers.settings.SettingsActivity;
import sturesy.core.Log;
import sturesy.core.backend.filter.file.NameXMLFileFilter;
import sturesy.core.backend.filter.file.ZipFileFilter;
import sturesy.core.plugin.Injectable;
import sturesy.core.plugin.QuestionVoteMatcher;
import sturesy.items.LectureID;
import sturesy.items.QuestionModel;
import sturesy.items.QuestionSet;
import sturesy.items.Vote;
import sturesy.items.VotingSet;
import sturesy.services.QTIImportService;
import sturesy.services.TechnicalVotingService;
import sturesy.services.TechnicalVotingServiceImpl;
import sturesy.services.TimeSource;
import sturesy.services.VotingTimeListener;
import sturesy.services.deserialization.QuestionImportService;
import sturesy.services.serialization.VoteExportService;
import sturesy.util.QRCodeGenerator;
import sturesy.util.SturesyManager;
import sturesy.util.web.WebVotingHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.uhh.sturesy_android.R;

/**
 * 
 * @author b.brunsen
 *
 */
public class VotingActivity extends Activity implements Injectable, TimeSource,
		VotingTimeListener {
	// TODO: Write comment for each method
	private boolean _isVotingRunning;
	private QuestionSet _currentQuestionSet;
	private int _currentQuestion;
	private LectureID _lectureID;
	private String _lecturefile;
	private File _currentFile;

	private TechnicalVotingService _votingService;

	private VotingSet _votingSaver;
	private EditText _timeField;
	private Fragment _currentFragment;
	private TextView _votingpanel;
	private TextView _lectureIDPanel;
	private ImageButton _startButton;
	private TextView _progressTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voting);
		initComponents();
		_lecturefile = "";
		_currentQuestion = 0;
		_votingService = new TechnicalVotingServiceImpl(this, this,
				SturesyManager.getLoadedPollPlugins());
		Collection<LectureID> lectureIDs = SturesyManager.getLectureIDs(this);
		if (lectureIDs.isEmpty())
		{
			_lectureID = null;
		}

		else
		{
			_lectureID = lectureIDs.iterator().next();
		}
		_votingService.registerTimeListener(this);
	}

	private void initComponents() {
		_timeField = (EditText) findViewById(R.id.time);
		_startButton = (ImageButton) findViewById(R.id.startVotingButton);
		_isVotingRunning = false;
		_lectureIDPanel = (TextView) findViewById(R.id.voting_lectureID);
		_votingpanel = (TextView) findViewById(R.id.voting_votes);
		_progressTextView = (TextView) findViewById(R.id.questionNumber);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		case R.id.load_question_set:
			loadQuestion();
			return true;
		case R.id.import_QTI:
			importQTI();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		if (_isVotingRunning)
		{
			_votingService.stopVoting();
		}

		if (_votingSaver != null && _votingSaver.containsVotes())
		{
			VoteExportService votingService = new VoteExportService();
			try
			{
				votingService.createAndUpdateVoting(_votingSaver, _lecturefile);
			} catch (IOException e)
			{
				// TODO: Find suitable error handling
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}

	private void loadQuestion() {
		if (!_isVotingRunning)
		{
			String title = getString(R.string.load_question_set);
			final FileImportDialog dialog = new FileImportDialog(this,
					new NameXMLFileFilter(), title);
			dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog2) {
					setCurrentFile(dialog.getSelectedFile());
					if (_currentFile != null)
					{
						readFile(_currentFile);
					}
				}
			});
			dialog.show();
		}
	}

	private void importQTI() {
		if (!_isVotingRunning)
		{
			String title = getString(R.string.title_open_qti);
			final FileImportDialog dialog = new FileImportDialog(this,
					new ZipFileFilter(), title);
			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog2) {
					setCurrentFile(dialog.getSelectedFile());
					if (_currentFile != null)
					{
						QTIImportService qtiService = new QTIImportService();
						QuestionSet questions = qtiService
								.getQuestions(_currentFile);
						setCurrentQuestionSet(questions);
						_votingSaver = new VotingSet();
						setCurrentQuestionModel(0);
					}
				}
			});
			dialog.show();
		}
	}

	/**
	 * Changes the state of the activity to the previous voting. Checks if a
	 * voting takes place.
	 * 
	 * @param v
	 */
	public void previousVoting(View v) {
		if (!_lecturefile.equals(""))
		{
			if (!_isVotingRunning && _currentQuestion > 0
					&& _currentFragment instanceof VotingDataFragment)
			{
				setCurrentQuestionModel(_currentQuestion - 1);
			}
		}
	}

	/**
	 * Changes the state of the activity to the next voting. Checks if a voting
	 * takes place.
	 * 
	 * @param v
	 */
	public void nextVoting(View v) {
		if (!_lecturefile.equals(""))
		{
			if (!_isVotingRunning
					&& _currentQuestionSet.size() != _currentQuestion + 1
					&& _currentFragment instanceof VotingDataFragment)
			{
				setCurrentQuestionModel(_currentQuestion + 1);
			}
		}
	}

	/**
	 * Starts the voting.
	 */
	public void startStopVoting(View v) {
		if (!_lecturefile.equals(""))
		{
			if (_isVotingRunning)
			{
				_votingService.stopVoting();
			} else
			{
				_votingService.startVoting();
			}
		}
	}

	/**
	 * Resets the voting. Clears votes for the current question.
	 * 
	 * @param The
	 *            view that was clicked (voting button)
	 */
	public void resetVoting(View v) {
		if (!_lecturefile.equals("")
				&& _currentFragment instanceof VotingDataFragment)
		{
			_votingSaver.clearVotesFor(_currentQuestion);
			String votes = getString(R.string.votes);
			_votingpanel.setText(votes
					+ _votingSaver.getVotesFor(_currentQuestion).size());
			int duration = _currentQuestionSet.getIndex(_currentQuestion)
					.getDuration();
			_timeField.setText(""
					+ (duration == QuestionModel.UNLIMITED ? "" : duration));
			// re-prepare the Voting
			// for instance: WebPlugin cleans the serverdatabase
			_votingService.prepareVoting(_lectureID, _currentQuestionSet,
					_currentQuestion);
			String toastMessage = getString(R.string.reset_voting_toast);
			Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void injectVote(final Vote vote) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				int size = _currentQuestionSet.getIndex(_currentQuestion)
						.getAnswerSize();

				if (QuestionVoteMatcher.isValidVote(vote, size))
				{
					// Save Votes for Evaluation
					if (_votingSaver.addVote(_currentQuestion, vote))
					{
						String votes = getString(R.string.votes);
						_votingpanel.setText(votes
								+ _votingSaver.getVotesFor(_currentQuestion)
										.size());
					}
				}
			}
		});
	}

	private void setCurrentQuestionModel(QuestionSet currentQuestionSet,
			int currentQuestion) {
		QuestionModel model = currentQuestionSet.getIndex(currentQuestion);
		if (_currentFragment == null)
		{
			_currentFragment = new VotingDataFragment(model);
			FragmentTransaction transaction = getFragmentManager()
					.beginTransaction();
			transaction.add(R.id.votingFragment, _currentFragment);
			transaction.commit();
		} else
		{
			_currentFragment = new VotingDataFragment(model);
			FragmentTransaction transaction = getFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.votingFragment, _currentFragment);
			transaction.commit();
		}
	}

	@Override
	public int getTimeLeft() {
		String textFieldText = _timeField.getText().toString();
		if (textFieldText.equals(""))
		{
			return -1;
		} else
		{
			return Integer.parseInt(textFieldText);
		}
	}

	/**
	 * Sets the current {@link QuestionModel} to the given index, and applies
	 * necessary changes to the graphical and logical components
	 * 
	 * @param index
	 *            index of questionmodel
	 */
	private void setCurrentQuestionModel(int index) {
		_currentQuestion = index;
		setCurrentQuestionModel(_currentQuestionSet, _currentQuestion);

		if (_lectureID != WebVotingHandler.NOLECTUREID && _lectureID != null)
		{
			_lectureIDPanel.setText("LectureID: " + _lectureID.getLectureID());
		} else
		{
			String idName = WebVotingHandler.NOLECTUREID.getLectureID();
			int id = getResources().getIdentifier(idName, "string",
					getPackageName());
			_lectureIDPanel.setText("LectureID: " + getString(id));
		}
		_progressTextView.setText((index + 1) + " / "
				+ _currentQuestionSet.size());
		int duration = _currentQuestionSet.getIndex(index).getDuration();
		_timeField.setText(""
				+ (duration == QuestionModel.UNLIMITED ? "" : duration));
		String votes = getString(R.string.votes);
		if (_votingSaver != null)
		{
			_votingpanel.setText(votes + ""
					+ _votingSaver.getVotesFor(_currentQuestion).size());
		} else
		{
			_votingpanel.setText(votes + "0");
		}
		_votingService.prepareVoting(_lectureID, _currentQuestionSet, index);
	}

	@Override
	public void startedVoting() {
		_isVotingRunning = true;
		_startButton.setImageResource(R.drawable.stop);
		String toastMessage = getString(R.string.voting_started_toast);
		Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void stoppedVoting() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				_isVotingRunning = false;
				_startButton.setImageResource(R.drawable.play);
				String toastMessage = getString(R.string.voting_finished_toast);
				Toast.makeText(getApplicationContext(), toastMessage,
						Toast.LENGTH_SHORT).show();
			}

		});
	}

	@Override
	public void votingTimeChanged(final int timeLeft) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				_timeField.setText("" + timeLeft);
			}
		});
	}

	/**
	 * Displays an undecorated dialogue containing a QRCode-Image. Width and
	 * height are calculated using system properties.
	 */
	public void showQRCode(View v) {

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = size.y;
		int calcSize = height - 100;
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
				v.getContext());
		alertBuilder.setTitle(getString(R.string.QR_Code));

		Bitmap icon = QRCodeGenerator.getQRImageForSavedAdress(
				_lectureID.getLectureID(), calcSize);
		ImageView imgView = new ImageView(v.getContext());
		imgView.setImageBitmap(icon);
		imgView.setMinimumHeight(calcSize);
		imgView.setMinimumWidth(calcSize);
		alertBuilder.setView(imgView);
		alertBuilder.setCancelable(true);
		alertBuilder.show();
	}

	/**
	 * opens a result activity fitting the latest voting.
	 */
	public void showResults(View v) {
		if (_currentFragment instanceof VotingDataFragment
				&& _votingSaver != null)
		{
			Set<Vote> votes = _votingSaver.getVotesFor(_currentQuestion);
			if (votes.size() > 0)
			{
				_currentFragment = new VotingResulstFragment(
						_currentQuestionSet.getIndex(_currentQuestion), votes);
				FragmentTransaction transaction = getFragmentManager()
						.beginTransaction();
				transaction.replace(R.id.votingFragment, _currentFragment);
				transaction.commit();
			}
		} else if (_currentFragment instanceof VotingResulstFragment)
		{
			setCurrentQuestionModel(_currentQuestion);
		}
	}

	/**
	 * Shows the correct answer after a finished voting.
	 * 
	 * @param v
	 */
	public void showCorrectResult(View v) {
		if (_currentFragment instanceof VotingResulstFragment)
		{
			((VotingResulstFragment) _currentFragment).swapAnswerBars(v);
		}
	}

	/**
	 * Loads a questionset from a file and hides the file list, when a list
	 * entry is clicked.
	 * 
	 */
	public void readFile(File f) {
		QuestionImportService qp = new QuestionImportService();
		boolean error = false;
		_lecturefile = f.getAbsolutePath();
		try
		{

			_currentQuestionSet = qp.readQuestionSetWithParser(f);
			if (_currentQuestionSet.size() > 0)
			{
				setCurrentQuestionModel(0);
			}

		} catch (XmlPullParserException e)
		{
			Log.error(e.getClass().getSimpleName(), e);
			error = true;
		} catch (IOException e)
		{
			Log.error(e.getClass().getSimpleName(), e);
			error = true;
		}
		_votingSaver = new VotingSet();
		if (error)
		{
			Dialog alert = new ErrorDialog(this, getString(R.string.error),
					getString(R.string.error_load_questionset));
			alert.show();
		}
	}

	public void setCurrentQuestionSet(QuestionSet set) {
		_currentQuestionSet = set;
	}

	public void setCurrentFile(File f) {
		_currentFile = f;
	}
}

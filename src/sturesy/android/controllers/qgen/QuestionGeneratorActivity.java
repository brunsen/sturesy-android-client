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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import sturesy.android.controllers.ErrorDialog;
import sturesy.android.controllers.FileImportDialog;
import sturesy.core.Log;
import sturesy.core.backend.filter.file.NameXMLFileFilter;
import sturesy.core.backend.filter.file.ZipFileFilter;
import sturesy.items.MultipleChoiceQuestion;
import sturesy.items.QuestionModel;
import sturesy.items.QuestionSet;
import sturesy.items.SingleChoiceQuestion;
import sturesy.items.TextQuestion;
import sturesy.services.QTIImportService;
import sturesy.services.deserialization.QuestionImportService;
import sturesy.services.serialization.QuestionExportService;
import sturesy.util.Folder;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;

import de.uhh.sturesy_android.R;

public class QuestionGeneratorActivity extends Activity {
	// TODO: Write comment for each method
	private int _currentQuestion;
	private String _currentFileName;
	private QuestionListAdapter _questionAdapter;
	private QuestionSet _currentQuestionset;
	private File _currentFile;
	private String _lecturesDirectory;
	private EditText _fileNameEdit;
	private DynamicListView _questionListView;
	private Fragment _currentFragment;

	// required for adding questions with dialog
	private int _questionChoice;
	private File _qtiFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_generator);
		initComponents();
		setQuestionChoice(-1);
		_currentFileName = "temp";
		_lecturesDirectory = Folder.getBaseFolder().getAbsolutePath()
				+ "/lectures/";
		_currentFile = new File(_lecturesDirectory, "/temp.xml");
		_currentQuestionset = new QuestionSet();
		_questionAdapter = new QuestionListAdapter(this,
				R.layout.question_list_item,
				_currentQuestionset.getQuestionModels());
		_questionListView.setAdapter(_questionAdapter);
		_questionListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				_currentQuestion = arg2;

				QuestionModel index = _currentQuestionset.getIndex(arg2);
				setQuestion(index);

			}
		});

		setTouchListener(_questionListView, _questionAdapter);
		_currentQuestion = -1;
		_currentFragment = new NoQuestionFragment();
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.add(R.id._question_fragment, _currentFragment,
				"QuestionFragment");
		transaction.commit();
	}

	private void initComponents() {
		_fileNameEdit = (EditText) findViewById(R.id.catalogueTitle);
		_questionListView = (DynamicListView) findViewById(R.id.questionList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.qgen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_question_set:
			save();
			return true;
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

	/**
	 * This method will add a new entry to the list in the navigationbar.
	 * 
	 * @param v
	 */
	public void addItem(View v) {

		if (_currentFile != null)
		{
			final SelectQuestionDialog dialog = new SelectQuestionDialog(this);
			dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog2) {
					setQuestionChoice(dialog.getChoice());
					QuestionModel qm = null;
					switch (getQuestionChoice()) {
					case 0:
						qm = new SingleChoiceQuestion();
						break;

					case 1:
						qm = new MultipleChoiceQuestion();
						break;

					case 2:
						qm = new TextQuestion();
						break;
					default:
						qm = null;
						break;
					}
					setQuestionChoice(-1);
					if (qm != null)
					{
						qm.setQuestion(getString(R.string.new_question));
						_currentQuestionset.addQuestionModel(qm);
						_questionAdapter.notifyDataSetChanged();
						_currentQuestion = _questionAdapter.getCount() - 1;
						setQuestion(qm);
					}
				}
			});
			dialog.show();

		}

	}

	/**
	 * Sets a swipe to dissmis Listener event for a listview. This enables
	 * removing of items on swipe events.
	 * 
	 * @param listView
	 * @param adapter
	 */
	public <T> void setTouchListener(DynamicListView listView,
			final ArrayAdapter<T> adapter) {
		SimpleSwipeUndoAdapter swipeUndoAdapter = new SimpleSwipeUndoAdapter(
				adapter, this, new OnDismissCallback() {
					@Override
					public void onDismiss(final ViewGroup listView,
							final int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions)
						{
							adapter.remove(adapter.getItem(position));
							if (_currentQuestion == position)
							{
								_currentQuestion = -1;
								replaceFragment(new NoQuestionFragment());
							}
							adapter.notifyDataSetChanged();
						}
					}
				});
		swipeUndoAdapter.setAbsListView(listView);
		listView.setAdapter(swipeUndoAdapter);
		listView.enableSimpleSwipeUndo();
	}

	private void importQTI() {
		String title = getString(R.string.qti_import);
		final FileImportDialog dialog = new FileImportDialog(this,
				new ZipFileFilter(), title);
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog2) {
				setQtiFile(dialog.getSelectedFile());
				if (_qtiFile != null)
				{
					QTIImportService qtiService = new QTIImportService();
					QuestionSet questions = qtiService.getQuestions(_qtiFile);
					for (QuestionModel questionModel : questions)
					{
						_currentQuestionset.addQuestionModel(questionModel);
					}
					_questionAdapter.notifyDataSetChanged();
					checkQTIAfterImport(qtiService, questions);
				}
			}
		});
		dialog.show();
	}

	private void loadQuestion() {
		String title = getString(R.string.load_question_set);
		final FileImportDialog dialog = new FileImportDialog(this,
				new NameXMLFileFilter(), title);
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog2) {
				File f = dialog.getSelectedFile();
				if (f != null)
				{
					setCurrentFile(f);
					readFile(_currentFile);
				}
			}
		});
		dialog.show();
	}

	/**
	 * Loads a question set from a file and inserts it into the list of question
	 * sets in the graphical user interface.
	 * 
	 * @param xml
	 *            Path to the desired file.
	 * @throws IOException
	 */
	public void readFile(File f) {
		_currentFile = f;
		// Check for an empty file.
		if (_currentFile.length() <= 0)
		{
			String currentFilename = _currentFile.getName();
			String displayText = currentFilename;
			_currentFileName = displayText.replace(".xml", "");
			_fileNameEdit.setText(_currentFileName);
			_currentQuestionset = new QuestionSet(
					new ArrayList<QuestionModel>());
			SingleChoiceQuestion empty = new SingleChoiceQuestion();
			empty.setQuestion(getString(R.string.question) + " " + 1);
			List<String> answers = new ArrayList<String>();
			String answerText = getString(R.string.answer);
			answers.add(answerText + " A");
			answers.add(answerText + " B");
			empty.setAnswers(answers);
			_currentQuestionset.addQuestionModel(empty);
			_questionAdapter.notifyDataSetChanged();
			setQuestion(empty);
			_currentQuestion = 0;

		} else
		{
			QuestionImportService qp = new QuestionImportService();

			QuestionSet result = null;
			try
			{

				result = qp.readQuestionSetWithParser(_currentFile);
				List<QuestionModel> questionModels = result.getQuestionModels();
				_questionAdapter = new QuestionListAdapter(this,
						R.layout.question_list_item, questionModels);
				_questionListView.setAdapter(_questionAdapter);
				String displayText = _currentFile.getName();
				_currentFileName = _currentFile.getName();
				_currentFileName = displayText.replace(".xml", "");
				displayText = displayText.replace(".xml", "");
				_fileNameEdit.setText(displayText);
				_currentQuestionset = result;
				setQuestion(_currentQuestionset.getIndex(0));
				_currentQuestion = 0;
			} catch (Exception e)
			{
				Log.error("Error parsing xml file: " + e.getMessage());
				ErrorDialog alert = new ErrorDialog(this,
						getString(R.string.error),
						getString(R.string.error_load_questionset));
				alert.show();
			}
		}
	}

	/**
	 * Inserts the details of a question into the gui.
	 */
	public void setQuestion(QuestionModel qm) {
		if (qm.getAnswers().isEmpty() && !(qm instanceof TextQuestion))
		{
			qm.getAnswers().add("Antwort A");
			qm.getAnswers().add("Antwort B");
		}
		if (qm instanceof SingleChoiceQuestion)
		{
			SingleChoiceQuestion scq = (SingleChoiceQuestion) qm;
			replaceFragment(new SingleQuestionFragment(scq,
					getQuestionAdapter()));
		} else if (qm instanceof MultipleChoiceQuestion)
		{
			MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) qm;
			replaceFragment(new MultipleQuestionFragment(mcq,
					getQuestionAdapter()));
		} else if (qm instanceof TextQuestion)
		{
			TextQuestion tq = (TextQuestion) qm;
			if (tq.getAnswer().equals(""))
			{
				tq.setAnswer("Antwort A");
			}
			replaceFragment(new TextQuestionFragment(tq,
					getQuestionAdapter()));
		}

	}

	public void save() {

		if (_currentFile != null
				&& !_fileNameEdit.getText().toString().equals("")
				&& !_currentQuestionset.getQuestionModels().isEmpty())
		{
			String newFileName = _fileNameEdit.getText().toString();
			QuestionModel currentQuestionModel = _currentQuestionset
					.getIndex(_currentQuestion);

			if (_currentQuestionset == null)
			{
				_currentQuestionset = new QuestionSet();
				_currentQuestionset.addQuestionModel(currentQuestionModel);
			}

			try
			{
				QuestionExportService questionWriter = new QuestionExportService();
				questionWriter.writeToFile(_currentFile, _currentQuestionset);
				renameFile(_currentFile, newFileName, _currentFileName);
				Toast.makeText(this,
						getString(R.string.question_save_successful),
						Toast.LENGTH_SHORT).show();
			} catch (IOException ioe)
			{
				ErrorDialog alert = new ErrorDialog(this,
						getString(R.string.error),
						getString(R.string.error_saving_questions));
				alert.show();
				Log.error("Error saving Questionset", ioe);
			} catch (Exception e)
			{
				_fileNameEdit.setText(_currentFileName);
				String message = getString(R.string.error_file_already_exist);
				ErrorDialog alert = new ErrorDialog(this,
						getString(R.string.error), message);
				alert.show();
			}
		} else
		{
			Toast toast = Toast.makeText(this,
					getString(R.string.error_no_questionset_name),
					Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	/**
	 * Method to rename the file.
	 * 
	 * @param oldFileName
	 * 
	 * @param Current
	 *            file to be renamed
	 * @param New
	 *            name
	 * @throws Exception
	 */
	public void renameFile(File from, String name, String oldFileName)
			throws Exception {
		name = name.replace("/", "");
		name = name.replace(".xml", "");
		name = name.replace(".csv", "");
		name = name.replace(".zip", "");

		if (!from.exists())
		{
			try
			{
				from.createNewFile();
			} catch (IOException e)
			{
				Log.error(e.getMessage());
			}
		}
		if (!name.equals(oldFileName))
		{
			File to = new File(_lecturesDirectory, "/" + name + ".xml");
			if (to.exists())
			{
				throw new Exception();
			} else
			{
				to.createNewFile();
				InputStream in = new FileInputStream(from);
				OutputStream out = new FileOutputStream(to);

				// Copy the bits from instream to outstream
				byte[] buf = new byte[1024];
				int len;

				while ((len = in.read(buf)) > 0)
				{
					out.write(buf, 0, len);
				}

				in.close();
				out.close();
				from.delete();
			}

		}
	}

	public QuestionListAdapter getQuestionAdapter() {
		return _questionAdapter;
	}

	public void swapQuestions(int i, int j) {
		_currentQuestionset.swapElements(i, j);
		if (_currentQuestion == i)
		{
			_currentQuestion = j;
		} else if (_currentQuestion == j)
		{
			_currentQuestion = i;
		}
	}

	public int getQuestionChoice() {
		return _questionChoice;
	}

	public void setQuestionChoice(int _questionChoice) {
		this._questionChoice = _questionChoice;
	}

	public void setQtiFile(File f) {
		_qtiFile = f;
	}

	public void setCurrentFile(File f) {
		if (f != null)
		{
			_currentFile = f;
		}
	}

	private void checkQTIAfterImport(QTIImportService qtiService,
			QuestionSet questions) {
		int questionSize = questions.getQuestionModels().size();
		int qtiTotal = qtiService.getQTIQuestionSize();
		if (questionSize < qtiTotal)
		{
			String message = String.format(
					getString(R.string.error_parsing_qti), questionSize,
					qtiTotal);
			ErrorDialog alert = new ErrorDialog(this,
					getString(R.string.error), message);
			alert.show();
		}
	}

	private void replaceFragment(Fragment fragment) {
		_currentFragment = fragment;
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id._question_fragment, _currentFragment,
				"QuestionFragment");
		transaction.commit();
	}
}
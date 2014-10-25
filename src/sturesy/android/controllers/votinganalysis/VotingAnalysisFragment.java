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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import sturesy.items.QuestionModel;
import sturesy.items.Vote;
import sturesy.util.HTMLLabel;
import sturesy.util.VoteAverage;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.uhh.sturesy_android.R;

/**
 * 
 * @author b.brunsen
 *
 */
public class VotingAnalysisFragment extends Fragment {
	private TextView _questionPanel;
	private QuestionModel _questionModel;
	private Set<Vote> _votesToDisplay;
	private TextView _medianLabel;
	private TextView _meanLabel;
	private LinearLayout _graphArea;


	public VotingAnalysisFragment(QuestionModel qm, Set<Vote> votes) {
		_questionModel = qm;
		_votesToDisplay = votes;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_voting_analysis,
				container, false);
		initComponents(view);
		setData();
		return view;
	}

	private void initComponents(View view) {
		_questionPanel = (TextView) view.findViewById(R.id.evaluation_question);
		_graphArea = (LinearLayout) view.findViewById(R.id.evaluation_graphs);
		_medianLabel = (TextView) view.findViewById(R.id.evaluation_median);
		_meanLabel = (TextView) view.findViewById(R.id.evaluation_mean);
	}

	private void setData() {
		String question = HTMLLabel.generateHTLMString(_questionModel
				.getQuestion());
		_questionPanel.setTextSize((float) 20.0);
		_questionPanel.setText(Html.fromHtml(question));
		setBars(_questionModel, _votesToDisplay);
		setAverageTextsOnWidget(_votesToDisplay);
		setTimeChart(_votesToDisplay);
	}

	/**
	 * :layout_width="fill_parent" android:layout_height="wrap_content"
	 * android:layout_weight="1" android:orientation="horizontal" /> Updates the
	 * bar chart.
	 * 
	 * @param qm
	 * @param results
	 * @param currentQuestion
	 */
	private void setBars(QuestionModel qm, Set<Vote> results) {
		BarChartFactory factory = new BarChartFactory(getActivity());
		GraphicalView barChartView = factory.generateBar(_votesToDisplay,
				_questionModel, false);
		_graphArea.addView(barChartView);
	}

	private void setTimeChart(Set<Vote> votes) {
		if (votes.size() != 0)
		{
			double[] dubble = createArrayOfVotes(votes);
			XYSeriesRenderer renderer = new XYSeriesRenderer();
			renderer.setLineWidth(2);
			renderer.setColor(Color.parseColor("#FF9912"));
			// Include low and max value
			renderer.setDisplayBoundingPoints(true);
			// we add point markers
			renderer.setPointStyle(PointStyle.CIRCLE);
			renderer.setPointStrokeWidth(3);
			renderer.setFillPoints(true);
			// renderer.setFillBelowLine(true);
			// renderer.setFillBelowLineColor(Color.parseColor("#FF9912"));
			XYSeries series = new XYSeries("Votes");
			for (int i = 0; i < dubble.length; i++)
			{
				series.add(i, dubble[i]);
			}
			XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
			dataSet.addSeries(series);

			XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
			multiRenderer.setAxisTitleTextSize(18);
			multiRenderer.setChartTitleTextSize(18);
			multiRenderer.setLabelsTextSize(18);
			multiRenderer.setXTitle(getString(R.string.time_in_seconds));
			multiRenderer.setYTitle(getString(R.string.number_of_votes));
			// Avoid black border
			multiRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent
																				// margins
			// Disable Pan on two axis
			multiRenderer.setPanEnabled(false, false);
			multiRenderer.setYAxisMin(0);
			multiRenderer.setLegendTextSize(18);
			multiRenderer.addSeriesRenderer(renderer);
			GraphicalView lineChartView = ChartFactory.getLineChartView(
					getActivity(), dataSet, multiRenderer);
			lineChartView.setMinimumHeight(550);
			_graphArea.addView(lineChartView);
		}
	}

	/**
	 * Converts a set of Votes to an Array of Votes over Time
	 * 
	 * @param setvotes
	 *            Set of Votes
	 * @return Array of Votes over Time
	 */
	private double[] createArrayOfVotes(Set<Vote> setvotes) {
		ArrayList<Vote> votes = new ArrayList<Vote>(setvotes);
		Collections.sort(votes, new Comparator<Vote>() {

			@Override
			public int compare(Vote o1, Vote o2) {
				Long i1 = o1.getTimeDiff();
				Long i2 = o2.getTimeDiff();
				return i1.compareTo(i2);
			}
		});

		int totalduration = (int) (votes.get(votes.size() - 1).getTimeDiff() / 1000);

		double[] dubble = new double[totalduration + 1];

		for (Vote v : votes)
		{
			int slot = ((int) (v.getTimeDiff() / 1000));
			dubble[slot]++;
		}

		return dubble;
	}

	/**
	 * Calculates mean and median values for the votes. Triggers update of
	 * median and mean labels.
	 * 
	 * @param votesToDisplay
	 */

	private void setAverageTextsOnWidget(Set<Vote> votesToDisplay) {
		VoteAverage average = new VoteAverage(votesToDisplay);
		double timeArithmeticMean = average.getTimeArithmeticMean();
		final double timeMedian = average.getTimeMedian();
		setMedianMean(timeArithmeticMean, timeMedian);
	}

	/**
	 * Sets the values for mean and median text views
	 * 
	 * @param timeArithmeticMean
	 * @param timeMedian
	 */
	private void setMedianMean(double timeArithmeticMean,
			final double timeMedian) {
		String meanText = getString(R.string.mean);
		String medianText = getString(R.string.median);
		medianText = String.format(medianText, timeMedian);
		meanText = String.format(meanText, timeArithmeticMean);
		_medianLabel.setText(medianText);
		_meanLabel.setText(meanText);
	}

	public void updateQuestion(QuestionModel qm, Set<Vote> votes) {
		_questionModel = qm;
		_votesToDisplay = votes;
		_graphArea.removeAllViews();
		setData();
	}

	public void updateQuestion(QuestionModel qm) {
		_questionModel = qm;
		_votesToDisplay = new HashSet<Vote>();
		_graphArea.removeAllViews();
		setData();
	}
}

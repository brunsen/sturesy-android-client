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
	// TODO Write Clear method, which just shows zero data and clears graphs.
	private TextView _questionPanel;
	private QuestionModel _questionModel;
	private Set<Vote> _votesToDisplay;
	private TextView _medianLabel;
	private TextView _meanLabel;
	private LinearLayout _graphArea;
//	private View _barGraph;
//	private View _lineGraph;

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
//		_barGraph = view.findViewById(R.id.evaluation_bargraph);
//		_lineGraph = view.findViewById(R.id.evalutation_line_graph);
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

	/**:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:layout_weight="1"
    android:orientation="horizontal" />
	 * Updates the bar chart.
	 * 
	 * @param qm
	 * @param results
	 * @param currentQuestion
	 */
	private void setBars(QuestionModel qm, Set<Vote> results) {
		BarChartFactory factory = new BarChartFactory(getActivity());
		GraphicalView barChartView = factory.generateBar(_votesToDisplay, _questionModel, false);
		_graphArea.addView(barChartView);
		// _barGraph.clear();
		// if (!(qm instanceof TextQuestion))
		// {
		// for (int i = 0; i < votesarr.length; i++)
		// {
		// String text = "" + (char) ('A' + i);
		// SimpleXYSeries series = new SimpleXYSeries(text);
		// series.addLast(i, votesarr[i]);
		// BarFormatter seriesFormat = new BarFormatter(
		// Color.parseColor("#FFCC11"),
		// Color.parseColor("#FFCC11"));
		// _barGraph.addSeries(series, seriesFormat);
		// }
		// } else
		// {
		// // Loop over votes array and init exactly two bars to display
		// // (correct and others)
		// for (int i = 0; i < votesarr.length; i++)
		// {
		// String text = "";
		// if (i == 0)
		// {
		// text += ((TextQuestion) qm).getAnswer();
		// } else
		// {
		// text += getString(R.string.others);
		// }
		// SimpleXYSeries series = new SimpleXYSeries(text);
		// series.addLast(i, votesarr[i]);
		// BarFormatter seriesFormat = new BarFormatter(
		// Color.parseColor("#FFCC11"),
		// Color.parseColor("#FFCC11"));
		// _barGraph.addSeries(series, seriesFormat);
		// }
		// }
		// _barGraph.setTicksPerRangeLabel(3);
		// BarRenderer<BarFormatter> renderer = (BarRenderer<BarFormatter>)
		// _barGraph.getRenderer(BarRenderer.class);
		// renderer.setBarRenderStyle(BarRenderStyle.SIDE_BY_SIDE);
		// renderer.setBarWidthStyle(BarWidthStyle.FIXED_WIDTH);
		// renderer.setBarWidth(5);
		// renderer.setBarGap(5);
		// _barGraph.redraw();
		// ArrayList<Bar> bars = new ArrayList<Bar>();
		// fillBars(qm, results, answers, bars);
		// _barGraph.setBars(bars);
	}

	/**
	 * Helper method for set bars. This method is for processing of voting data.
	 * 
	 * @param qm
	 * @param results
	 * @param answers
	 * @param bars
	 */
	// private void fillBars(QuestionModel qm, Set<Vote> results,
	// List<String> answers, List<Bar> bar_barGraph.clear();
	// if (!(qm instanceof TextQuestion))
	// {
	// for (int i = 0; i < votesarr.length; i++)
	// {
	// String text = "" + (char) ('A' + i);
	// SimpleXYSeries series = new SimpleXYSeries(text);
	// series.addLast(i, votesarr[i]);
	// BarFormatter seriesFormat = new BarFormatter(
	// Color.parseColor("#FFCC11"),
	// Color.parseColor("#FFCC11"));
	// _barGraph.addSeries(series, seriesFormat);
	// }
	// } else
	// {
	// // Loop over votes array and init exactly two bars to display
	// // (correct and others)
	// for (int i = 0; i < votesarr.length; i++)
	// {
	// String text = "";
	// if (i == 0)
	// {
	// text += ((TextQuestion) qm).getAnswer();
	// } else
	// {
	// text += getString(R.string.others);
	// }
	// SimpleXYSeries series = new SimpleXYSeries(text);
	// series.addLast(i, votesarr[i]);
	// BarFormatter seriesFormat = new BarFormatter(
	// Color.parseColor("#FFCC11"),
	// Color.parseColor("#FFCC11"));
	// _barGraph.addSeries(series, seriesFormat);
	// }
	// }
	// _barGraph.setTicksPerRangeLabel(3);
	// BarRenderer<BarFormatter> renderer = (BarRenderer<BarFormatter>)
	// _barGraph.getRenderer(BarRenderer.class);
	// renderer.setBarRenderStyle(BarRenderStyle.SIDE_BY_SIDE);
	// renderer.setBarWidthStyle(BarWidthStyle.FIXED_WIDTH);
	// renderer.setBarWidth(5);
	// renderer.setBarGap(5);
	// _barGraph.redraw();s) {
	//
	// VotingResultCalculator vrc = new VotingResultCalculator();
	// float[] votesarr = vrc.calculateVotes(qm, results);
	// for (int i = 0; i < votesarr.length; i++)
	// {
	// Bar bar = new Bar();
	// String text = "" + (char) ('A' + i);
	//
	// bar.setColor(Color.parseColor("#FFCC11"));
	//
	// bar.setName(text);
	// bar.setValue(votesarr[i]);
	// bars.add(bar);
	// }
	// }

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
//			renderer.setFillBelowLine(true);
//			renderer.setFillBelowLineColor(Color.parseColor("#FF9912"));
			XYSeries series = new XYSeries("Votes");
			for (int i = 0; i < dubble.length; i++)
			{
				series.add(i, dubble[i]);
			}
			XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
			dataSet.addSeries(series);

			XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
			mRenderer.setAxisTitleTextSize(18);
			mRenderer.setChartTitleTextSize(18);
			mRenderer.setXTitle(getString(R.string.time_in_seconds));
			mRenderer.setYTitle(getString(R.string.number_of_votes));
			// Avoid black border
			mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
			// Disable Pan on two axis
			mRenderer.setPanEnabled(false, false);
			mRenderer.setYAxisMin(0);
			mRenderer.setLegendTextSize(18);
			mRenderer.addSeriesRenderer(renderer);
			GraphicalView lineChartView = ChartFactory.getLineChartView(
					getActivity(), dataSet, mRenderer);
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
}

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

import java.util.List;
import java.util.Set;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import sturesy.items.MultipleChoiceQuestion;
import sturesy.items.QuestionModel;
import sturesy.items.SingleChoiceQuestion;
import sturesy.items.TextQuestion;
import sturesy.items.Vote;
import sturesy.services.VotingResultCalculator;
import sturesy.util.MathUtils;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import de.uhh.sturesy_android.R;
/**
 * 
 * @author b.brunsen
 *
 */
public class BarChartFactory {
	private Context _context;

	public BarChartFactory(Context c) {
		_context = c;
	}

	public GraphicalView generateBar(Set<Vote> votes, QuestionModel qm,
			boolean showCorrect) {
		VotingResultCalculator vrc = new VotingResultCalculator();
		float[] votesarr = vrc.calculateVotes(qm, votes);
		XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
		multiRenderer.setXLabels(0);
		
		int max = 0;
        for (int i = 0; i < votesarr.length; i++)
        {
            max += votesarr[i];
        }
		
		if (!(qm instanceof TextQuestion))
		{
			
			
			for (int i = 0; i < votesarr.length; i++)
			{
				String text = "" + (char) ('A' + i);
				XYSeriesRenderer renderer = new XYSeriesRenderer();
				if(showCorrect)
				{
					tryColorSingleChoice(qm, i, renderer);
					tryColorMultipleChoice(qm, i, renderer);
				}
				else{
					renderer.setColor(Color.parseColor("#FFCC11"));
				}
				addSeries(votesarr,max , dataSet, multiRenderer, i, text, renderer);
			}
		} else
		{
			// Loop over votes array and init exactly two bars to display
			// (correct and others)
			for (int i = 0; i < votesarr.length; i++)
			{
				String text = "";
				XYSeriesRenderer renderer = new XYSeriesRenderer();
				if (i == 0)
				{
					if(showCorrect)
					{
						renderer.setColor(Color.GREEN);
					}
					text += ((TextQuestion) qm).getAnswer();
				} else
				{
					if(showCorrect)
					{
						renderer.setColor(Color.RED);
					}
					text += _context.getString(R.string.others);
				}
				if(!showCorrect)
				{
					renderer.setColor(Color.parseColor("#FFCC11"));
				}
				addSeries(votesarr, max, dataSet, multiRenderer, i, text, renderer);
			}
		}
		multiRenderer.setYTitle(_context.getString(R.string.votes_in_percent));
		multiRenderer.setYLabelsAlign(Align.LEFT);
		multiRenderer.setAxisTitleTextSize(18);
		multiRenderer.setBarWidth(80);
		multiRenderer.setPanEnabled(false);
		multiRenderer.setZoomEnabled(true, true);
		multiRenderer.setYAxisMin(0);
		multiRenderer.setXAxisMin(0);
		multiRenderer.setXAxisMax(votesarr.length + 1);
		multiRenderer.setYAxisMax(100);
		multiRenderer.setLabelsTextSize(18);
		multiRenderer.setShowLegend(false);
		multiRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
		GraphicalView barChartView = ChartFactory.getBarChartView(_context,
				dataSet, multiRenderer, Type.STACKED);
		barChartView.setMinimumHeight(600);
		return barChartView;
	}

	private void addSeries(float[] votesarr, int max, XYMultipleSeriesDataset dataSet,
			XYMultipleSeriesRenderer multiRenderer, int i, String text,
			XYSeriesRenderer renderer) {
		XYSeries series = new XYSeries(text);
		
		// Calculate the value in percent
		float votes = votesarr[i] * 100 / max;
        votes = (float) MathUtils.roundToDecimals(votes, 1);
		
        series.add(i+1, votes);
		renderer.setDisplayChartValues(true);
		renderer.setChartValuesTextSize(20);
		dataSet.addSeries(series);
		multiRenderer.addSeriesRenderer(renderer);
		multiRenderer.addXTextLabel(i+1, text);
	}

	private void tryColorMultipleChoice(QuestionModel qm, int i,
			XYSeriesRenderer renderer) {
		if(qm instanceof MultipleChoiceQuestion){
			List<Integer> correctAnswers = ((MultipleChoiceQuestion)qm).getCorrectAnswers();
			if(correctAnswers.contains(i))
			{
				renderer.setColor(Color.GREEN);
			}
			else{
				renderer.setColor(Color.RED);
			}
		}
	}

	private void tryColorSingleChoice(QuestionModel qm, int i,
			XYSeriesRenderer renderer) {
		if(qm instanceof SingleChoiceQuestion)
		{
			int correct = ((SingleChoiceQuestion)qm).getCorrectAnswer();
			if(correct == i)
			{
				renderer.setColor(Color.GREEN);
			}
			else{
				renderer.setColor(Color.RED);
			}
		}
	}
}

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
package sturesy.services.serialization;

import java.io.File;
import java.io.IOException;
import java.util.List;

import sturesy.core.backend.services.FileIOService;
import sturesy.items.MultipleChoiceQuestion;
import sturesy.items.QuestionModel;
import sturesy.items.QuestionSet;
import sturesy.items.SingleChoiceQuestion;
import sturesy.items.TextQuestion;
/**
 * 
 * @author b.brunsen
 *
 */
public class QuestionExportService {

	FileIOService _fileService;
	
	public QuestionExportService()
	{
		_fileService = new FileIOService();
	}
	
	/**
	 * Parses a Questionset to xml and stores it inside a provided file.
	 * @param file
	 * @param questionSet
	 * @throws IOException
	 */
	public void writeToFile(File file, QuestionSet questionSet) throws IOException
	{
		String xml = QuestionSetToXml(questionSet);
		_fileService.writeToFileCreateIfNotExist(file, xml);
	}
	
	/**
	 * Helper method for writeToFile. Builds xml structure and parses information to string.
	 * @param questionSet
	 * @return Xml String
	 */
	private String QuestionSetToXml(QuestionSet questionSet)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<questionset>" + "\n");
		for (QuestionModel questionModel : questionSet) {
			if (questionModel instanceof SingleChoiceQuestion) {
				sb.append("<questionmodel>" + "\n");
				parseCommonInformation(sb, questionModel);
				SingleChoiceQuestion qm = (SingleChoiceQuestion) questionModel;
				List<String> answers = qm.getAnswers();
				
				// Storing all available answers
				for (int i = 0; i< answers.size(); i++){
					sb.append("<answer>").append(answers.get(i)).append("</answer>").append("\n");
				}
				sb.append("<correct>").append(qm.getCorrectAnswer()).append("</correct>").append("\n");
				sb.append("</questionmodel>" + "\n");
			}
			
			else if(questionModel instanceof MultipleChoiceQuestion){
				sb.append("<multiplechoice>" + "\n");
				parseCommonInformation(sb, questionModel);
				MultipleChoiceQuestion qm = (MultipleChoiceQuestion) questionModel;
				List<String> answers = qm.getAnswers();
				List<Integer> correctAnswers = qm.getCorrectAnswers();
				
				// Storing all available answers
				for (int i = 0; i< answers.size(); i++){
					sb.append("<answers>").append(answers.get(i)).append("</answers>").append("\n");
				}
				// Storing all available correct answers
				for (int i = 0; i< correctAnswers.size(); i++){
					sb.append("<correctAnswers>").append(correctAnswers.get(i)).append("</correctAnswers>").append("\n");
				}
				sb.append("</multiplechoice>" + "\n");
			}
			
			else if(questionModel instanceof TextQuestion){
				sb.append("<textquestion>" + "\n");
				parseCommonInformation(sb, questionModel);
				TextQuestion qm = (TextQuestion) questionModel;
				sb.append("<answer>").append(qm.getAnswer()).append("</answer>").append("\n");
				sb.append("<tolerance>").append(qm.getTolerance()).append("</tolerance>").append("\n");
				sb.append("<ignorecase>").append(qm.isIgnoreCase()).append("</ignorecase>").append("\n");
				sb.append("<ignorespaces>").append(qm.isIgnoreSpaces()).append("</ignorespaces>").append("\n");
				sb.append("</textquestion>" + "\n");
			}
		}
		sb.append("<questionset>");
		return sb.toString();
	}
	
	/**
	 * Helper method for QuestionSetToXml. Parses information common to QuestionModels.
	 * @param sb Stringbuilder used to store information
	 * @param qm Questionmodel to be parsed
	 */
	private void parseCommonInformation(StringBuilder sb, QuestionModel qm)
	{
		sb.append("<question>").append(qm.getQuestion()).append("</question>").append("\n");
		sb.append("<afont>").append(qm.getAnswerFont()).append("</afont>").append("\n");
		sb.append("<qfont>").append(qm.getQuestionFont()).append("</qfont>").append("\n");
		sb.append("<duration>").append(qm.getDuration()).append("</duration>").append("\n");
	}
}
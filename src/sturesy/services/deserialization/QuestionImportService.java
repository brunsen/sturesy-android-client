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
package sturesy.services.deserialization;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import sturesy.core.backend.services.XMLEscapeService;
import sturesy.items.MultipleChoiceQuestion;
import sturesy.items.QuestionModel;
import sturesy.items.QuestionSet;
import sturesy.items.SingleChoiceQuestion;
import sturesy.items.TextQuestion;
/**
 * Class responsible for deserialization of QuestionModel Objects.
 * @author b.brunsen
 *
 */
public class QuestionImportService {
	/**
	 * Parses a file into a questionSet.
	 * @param xmlFile FileObject of xml file to be parsed.
	 * @return Object of QuestionSet
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public QuestionSet readQuestionSetWithParser(File xmlFile) throws XmlPullParserException, IOException
    {
        QuestionSet quesSet = new QuestionSet();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        
        FileReader reader = new FileReader(xmlFile);
        parser.setInput(reader);

        processXML(parser, quesSet);

        reader.close();
        return quesSet;
    }

	/**
	 * Helper method of readQuestionSetWithParser. Object initiation for each QuestionModel.
	 * @param xpp
	 * @param set
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
    private void processXML(XmlPullParser xpp, QuestionSet set) throws XmlPullParserException, IOException
    {
        int eventType = xpp.getEventType();

        String lastStartTag = "";
        QuestionModel quest;
        String question = "";
        int duration = 0;
        List<String> answers = new ArrayList<String>();
        List<Integer> correctAnswers = new ArrayList<Integer>();
        int tolerance = 0;
        boolean ignoreCase = false;
        boolean ignoreWhiteSpace = false;
        boolean reset = false;
        
        do
        {
            if (eventType == XmlPullParser.START_TAG)
            {
                // process start
                lastStartTag = xpp.getName();
                if(reset)
                {
                	answers = new ArrayList<String>();
                	correctAnswers = new ArrayList<Integer>();
                    question = "";
                    duration = 0;
                    tolerance = 0;
                    ignoreCase = false;
                    ignoreWhiteSpace = false;
                    reset = false;
                }
                if(lastStartTag.equals("question"))
                {
                	question = XMLEscapeService.unescapeString(xpp.nextText());
                }
                else if(lastStartTag.equals("duration"))
                {
                	duration = Integer.parseInt(xpp.nextText());
                }
                else if(lastStartTag.equals("answer") || lastStartTag.equals("answers"))
                {
                	answers.add(XMLEscapeService.unescapeString(xpp.nextText()));
                }
                else if(lastStartTag.equals("correct") || lastStartTag.equals("correctAnswers"))
                {
                	correctAnswers.add(Integer.parseInt(xpp.nextText()));
                }
                else if(lastStartTag.equals("tolerance"))
                {
                	tolerance = Integer.parseInt(xpp.nextText());
                }
                else if(lastStartTag.equals("ignorecase"))
                {
                	ignoreCase = Boolean.parseBoolean(xpp.nextText());
                }
                else if(lastStartTag.equals("ignorespaces"))
                {
                	ignoreWhiteSpace = Boolean.parseBoolean(xpp.nextText());
                }
            }
            else if (eventType == XmlPullParser.END_TAG)
            {
                // process end
            	String endTag = xpp.getName();
            	
            	// XMLEscapeService.unescapeString(xpp.nextText())
            	// Integer.parseInt(xpp.nextText())
                if (endTag.equals("questionmodel"))
                {
                	quest = new SingleChoiceQuestion(question, answers, correctAnswers.get(0), duration);
                    set.addQuestionModel(quest);
                    reset = true;
                }
                else if(endTag.equals("multiplechoice"))
                {
                	quest = new MultipleChoiceQuestion(question, duration, answers, correctAnswers);
                	set.addQuestionModel(quest);
                	reset = true;
                }
                else if(endTag.equals("textquestion"))
                {
                	quest = new TextQuestion(question, answers.get(0), tolerance, ignoreCase, ignoreWhiteSpace, duration);
                	set.addQuestionModel(quest);
                	reset = true;
                }
            }
            eventType = xpp.next();
        }
        while (eventType != XmlPullParser.END_DOCUMENT);
    }
    
}
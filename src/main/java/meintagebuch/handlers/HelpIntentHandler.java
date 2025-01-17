/*
     Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

     Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file
     except in compliance with the License. A copy of the License is located at

         http://aws.amazon.com/apache2.0/

     or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
     the specific language governing permissions and limitations under the License.
*/

package meintagebuch.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import meintagebuch.PhrasesAndConstants;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

/**
 * Handler for help-Intent
 */
public class HelpIntentHandler implements RequestHandler {

    /**
     * returns true if class can handle the HandlerInput -> else: false
     * @param input is the voice input as HandlerInput
     * @return bool value: true if voice input contains the intent "AMAZON.HelpIntent", else false
     */
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.HelpIntent"));
    }

    /**
     * Answers with help to the Alexaskill.
     * (details to output in PhrasesAndConstants.java under HELP and HELP_REPROMPT)
     * @param input
     * @return
     */
    @Override
    public Optional<Response> handle(HandlerInput input) {
        return input.getResponseBuilder()
                .withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.HELP)
                .withSpeech(PhrasesAndConstants.HELP)
                .withReprompt(PhrasesAndConstants.HELP_REPROMPT)
                .withShouldEndSession(false)
                .build();
    }
}

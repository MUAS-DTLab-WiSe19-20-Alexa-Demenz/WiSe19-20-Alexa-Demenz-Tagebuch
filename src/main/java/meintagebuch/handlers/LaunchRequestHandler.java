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

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.response.ResponseBuilder;
import meintagebuch.PhrasesAndConstants;
import meintagebuch.model.DiaryEntry;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.amazon.ask.request.Predicates.requestType;

/**
 * responses with greeting and asks user to enter an Entry.
 */
public class LaunchRequestHandler implements RequestHandler {

    /**
     * returns true if class can handle the HandlerInput -> else: false.
     *
     * @param input is the voice input as HandlerInput
     * @return bool value: true if input matches LaunchRequest
     */
    @Override
    public boolean canHandle(final HandlerInput input) {
        return input.matches(requestType(LaunchRequest.class));
    }


    /**
     * Method for launching the Alexaskill "Mein Tagebuch".
     *
     * @param input voice/text as HandlerInput
     * @return response with a welcome message and help instructions.
     */
    @Override
    public Optional<Response> handle(final HandlerInput input) {
        // remove old entries
        AttributesManager attributesManager = input.getAttributesManager();
        Map<String, Object> persistentAttributes = attributesManager.getPersistentAttributes();
        List<String> entryIdsToDelete = new LinkedList<>();
        // get all tags that are actually a date
        List<String> dateTags = persistentAttributes.keySet().stream()
                .filter(e -> e.matches("\\d{2}.\\d{2}.\\d{4}")).collect(Collectors.toList());
        for (String key : dateTags) {
            LocalDate date = LocalDate.parse(key, PhrasesAndConstants.FORMATTER);
            // if date is older than one week remove its entries
            if (date.isBefore(LocalDate.now().minusDays(PhrasesAndConstants.DAYS_TO_SAVE_DATA))) {
                List<String> list = (List<String>) persistentAttributes.get(key);
                entryIdsToDelete.addAll(list);
            }
        }
        // check if entry has tag "wichtig"
        // get entryIds of Tag "wichtig"
        try {
            List<String> importantTags = (List<String>) persistentAttributes.get(PhrasesAndConstants.SUBJECT_IMPORTANT_KEY);
            for (String entryId : entryIdsToDelete) {
                if (!importantTags.contains(entryId)) {
                    PhrasesAndConstants.deleteEntryFromDB(input, entryId);
                }
            }
        } catch (NullPointerException e) {
            for (String entryId : entryIdsToDelete) {
                PhrasesAndConstants.deleteEntryFromDB(input, entryId);
            }
        }


        // synchronize id counter from persistant attributes
        // see if there is already an entryIDCounter in persistent attributes
        // if yes synchronize
        // store new EntryIdCounter in persitent attributes
        try {
            DiaryEntry.setDiaryIdCounter(Integer.parseInt((String) persistentAttributes.get(PhrasesAndConstants.ENTRYID_KEY_PERSISTANT)));
        } catch (NullPointerException | NumberFormatException e) {
            persistentAttributes.put(PhrasesAndConstants.ENTRYID_KEY_PERSISTANT, String.valueOf(DiaryEntry.getDiaryIdCounter()));
            attributesManager.setPersistentAttributes(persistentAttributes);
            attributesManager.savePersistentAttributes();
        }
        // build response
        ResponseBuilder responseBuilder = input.getResponseBuilder();
        responseBuilder.withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.WELCOME)
                .withSpeech(PhrasesAndConstants.WELCOME)
                .withReprompt(PhrasesAndConstants.HELP_REPROMPT);

        return responseBuilder.build();
    }

}

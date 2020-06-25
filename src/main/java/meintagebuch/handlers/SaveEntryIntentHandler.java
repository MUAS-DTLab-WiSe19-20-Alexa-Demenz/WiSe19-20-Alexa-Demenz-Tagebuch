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
import com.amazon.ask.model.*;
import com.amazon.ask.response.ResponseBuilder;
import meintagebuch.PhrasesAndConstants;
import meintagebuch.model.DiaryEntry;
import meintagebuch.model.DiarySubject;

import java.util.*;

import static com.amazon.ask.request.Predicates.intentName;

/**
 * saves the Entry with or without additional subjects in the persistantAttributes.
 * tell the user to make an entry first if no entrytext is in the sessionAttributes
 */
public class SaveEntryIntentHandler implements RequestHandler {

  /**
   * returns true if class can handle the HandlerInput -> else: false.
   *
   * @param input is the voice input as HandlerInput
   * @return bool value: true if voice input contains the intent "Eintrag_Einsprechen_Speichern", else false
   */
  @Override
  public boolean canHandle(final HandlerInput input) {
    return input.matches(intentName("Eintrag_Einsprechen_Speichern"));
  }

  /**
   * Creates a new DiaryEntry object by asking back and and saves the entry.
   *
   * @param input voice input as HandlerInput
   * @return responseBuilder
   */
  @Override
  public Optional<Response> handle(final HandlerInput input) {
    AttributesManager attributesManager = input.getAttributesManager();
    Map<String, Object> persistentAttributes = attributesManager.getPersistentAttributes();
    try {

      // synchronize id counter from persistent attributes
      DiaryEntry.setDiaryIdCounter(Integer.parseInt((String) persistentAttributes.get(PhrasesAndConstants.ENTRYID_KEY_PERSISTANT)));
    } catch (NullPointerException | NumberFormatException e) {
      persistentAttributes.put(PhrasesAndConstants.ENTRYID_KEY_PERSISTANT, String.valueOf(DiaryEntry.getDiaryIdCounter()));
      attributesManager.setPersistentAttributes(persistentAttributes);
      attributesManager.savePersistentAttributes();
    }
    // end review
    ResponseBuilder responseBuilder = input.getResponseBuilder();
    try {
      // get entryText from sessionAttributes -> Nullpointer if no entry was made in this session

      Map<String, Object> sessionAttributes = attributesManager.getSessionAttributes();
      // get entryObject
      String newEntryText = sessionAttributes.get(PhrasesAndConstants.ENTRYTEXT_KEY_SESSION).toString();
      // create new entryObj
      DiaryEntry newEntryObj = new DiaryEntry(newEntryText);
      // get slots from request
      Request request = input.getRequestEnvelope().getRequest();
      IntentRequest intentRequest = (IntentRequest) request;
      Intent intent = intentRequest.getIntent();
      Map<String, Slot> slots = intent.getSlots();
      // see if confirmationstatus is CONFIRMED (does user want to add tags?)
      // if true -> add tags to entry
      // else save entry without tags
      if (intent.getConfirmationStatus().toString().
          equals(PhrasesAndConstants.CONFIRMATION_STATUS_SLOT_CONFIRMED)) {

        Slot newSubjectsSlot = slots.get(PhrasesAndConstants.SUBJECT_SLOT);
        String newSubjectSlotVal = newSubjectsSlot.getValue();
        // create new DiaryEntry with subjects
        Set<String> subjects = new HashSet<>();
        Collections.addAll(subjects, newSubjectSlotVal.split(" "));
        newEntryObj.addNewSubjects(subjects);
        // response
        responseBuilder.withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.SAY_MAKE_ENTRY)
            .withSpeech(PhrasesAndConstants.SAY_MAKE_ENTRY + PhrasesAndConstants.YOUR_NEW_TAGS + newSubjectSlotVal + PhrasesAndConstants.YOUR_ENTRY_WAS + newEntryObj.getEntryText())
            .withShouldEndSession(true);
      } else {
        // say canceled subjects
        responseBuilder.withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.SAY_MAKE_ENTRY + PhrasesAndConstants.SAY_CANCEL_SUBJECTS)
            .withSpeech(PhrasesAndConstants.SAY_MAKE_ENTRY + PhrasesAndConstants.SAY_CANCEL_SUBJECTS + PhrasesAndConstants.YOUR_ENTRY_WAS + newEntryObj.getEntryText())
            .withShouldEndSession(true);
      }
      // add this entry to db
      // in db for every tag there is a List with every entryId from the entry that is using this tag
      // than there is the entry map stored in db like (ENTRYKEY + ENTRYID, ENTRYTEXT
      // see if there is already a tag in db
      // if yes add this entryId to the subject
      // else add new List with this entryId to the subject
      persistentAttributes = attributesManager.getPersistentAttributes();
      for (DiarySubject subject : newEntryObj.getSubjects()) {
        if (persistentAttributes.containsKey(subject.toString())) {
          //noinspection unchecked
          List<String> entryIds = (List) persistentAttributes.get(subject.toString());
          entryIds.add(newEntryObj.getDiaryEntryIdAsString());
          persistentAttributes.put(subject.toString(), entryIds);
        } else {
          List<String> entryIds = new ArrayList<>();
          entryIds.add(newEntryObj.getDiaryEntryIdAsString());
          persistentAttributes.put(subject.toString(), entryIds);
        }
      }

      // add this entry to persistentAttributes
      persistentAttributes.put(PhrasesAndConstants.ENTRY_KEY_PERSISTANT + newEntryObj.getDiaryEntryId(), newEntryObj.getEntryText());
      // save current EntryIDCounter to persistent attributes
      persistentAttributes.put(PhrasesAndConstants.ENTRYID_KEY_PERSISTANT, String.valueOf(DiaryEntry.getDiaryIdCounter()));
      // save entry in db
      attributesManager.setPersistentAttributes(persistentAttributes);
      attributesManager.savePersistentAttributes();
    } catch (NullPointerException e) {
      // say make entry first
      responseBuilder.withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.SAY_MAKE_ENTRY_FIRST)
          .withSpeech(PhrasesAndConstants.SAY_MAKE_ENTRY_FIRST)
          .withShouldEndSession(false);
    }
    return responseBuilder.build();
  }
}

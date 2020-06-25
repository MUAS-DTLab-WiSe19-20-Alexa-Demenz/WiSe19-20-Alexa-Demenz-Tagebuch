package meintagebuch.handlers;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.response.ResponseBuilder;
import meintagebuch.PhrasesAndConstants;

import java.util.*;

import static com.amazon.ask.request.Predicates.intentName;

public class AddTagsToEntryIntentHandler implements RequestHandler {

  @Override
  public boolean canHandle(final HandlerInput input) {
    return input.matches(intentName("Eintrag_Aendern"));
  }

  /**
   * Deletes an existing entry
   *
   * @param input is the voice input
   * @return Response as card and speech -> 'entry deleted'
   */
  @Override
  public Optional<Response> handle(final HandlerInput input) {
    ResponseBuilder builder = input.getResponseBuilder();
    AttributesManager attributesManager = input.getAttributesManager();
    try {
      IntentRequest intentRequest = (IntentRequest) input.getRequest();
      Intent intent = intentRequest.getIntent();
      Map<String, Slot> slots = intent.getSlots();
      List<String> ids;
      // check if there are entryIds in the session
      ids = (List<String>) attributesManager.getSessionAttributes().get(PhrasesAndConstants.ENTRY_TO_EDIT_KEY);
      // if so check if it matches userInput
      int entryNr = Integer.parseInt(slots.get(PhrasesAndConstants.ENTRY_NR_SLOT).getValue());
      String entryId;
      if (ids.size() >= entryNr) {
        Slot newSubjectsSlot = slots.get(PhrasesAndConstants.SUBJECT_SLOT);
        String newSubjectSlotVal = newSubjectsSlot.getValue();
        Set<String> subjects = new HashSet<>();
        Collections.addAll(subjects, newSubjectSlotVal.split(" "));
        entryId = ids.get(entryNr - 1);
        Map<String, Object> persistentAttributes = attributesManager.getPersistentAttributes();
        for (String subject : subjects) {
          if (persistentAttributes.containsKey(subject)) {
            //noinspection unchecked
            List<String> idsToSave = (List) persistentAttributes.get(subject);
            idsToSave.add(entryId);
            persistentAttributes.put(subject, idsToSave);
          } else {
            List<String> entryIds = new ArrayList<>();
            entryIds.add(entryId);
            persistentAttributes.put(subject, entryIds);
          }
          attributesManager.setPersistentAttributes(persistentAttributes);
          attributesManager.savePersistentAttributes();
        }
      }
      builder.withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.ADDED_NEW_SUBJECTS)
          .withSpeech(PhrasesAndConstants.ADDED_NEW_SUBJECTS)
          .withShouldEndSession(true);
    } catch (NullPointerException e) {
      builder.withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.ASK_FOR_ENTRIES_FIRST)
          .withSpeech(PhrasesAndConstants.ASK_FOR_ENTRIES_FIRST)
          .withShouldEndSession(false);
    } catch (ClassCastException e) {
      builder.withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.SAY_YOU_CAN_CHANGE_ENTRY_1)
              .withSpeech(PhrasesAndConstants.SAY_YOU_CAN_CHANGE_ENTRY_1)
              .withShouldEndSession(false);
    }
    return builder.build();
  }
}

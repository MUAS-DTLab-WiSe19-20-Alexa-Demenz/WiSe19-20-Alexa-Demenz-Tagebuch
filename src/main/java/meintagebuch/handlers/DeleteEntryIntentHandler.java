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

public class DeleteEntryIntentHandler implements RequestHandler {

    @Override
    public boolean canHandle(final HandlerInput input) {
        return input.matches(intentName("Eintrag_Loeschen"));
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
        IntentRequest intentRequest = (IntentRequest) input.getRequest();
        Intent intent = intentRequest.getIntent();
        if (intent.getConfirmationStatus().toString().
                equals(PhrasesAndConstants.CONFIRMATION_STATUS_SLOT_CONFIRMED)) {
            Map<String, Slot> slots = intent.getSlots();
            List<String> ids;
            String entryText;
            try {
                // check if there are entryIds in the session
                ids = (List<String>) attributesManager.getSessionAttributes().get(PhrasesAndConstants.ENTRY_TO_EDIT_KEY);
                int entryNr = Integer.parseInt(slots.get(PhrasesAndConstants.ENTRY_NR_SLOT).getValue());
                // if so check if it matches userInput
                String entryId;
                entryId = ids.get(entryNr - 1);
                entryText = (String) attributesManager.getPersistentAttributes().get(PhrasesAndConstants.ENTRY_KEY_PERSISTANT + entryId);
                PhrasesAndConstants.deleteEntryFromDB(input, entryId);
                builder.withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.DELETED_ENTRY + entryText)
                        .withSpeech(PhrasesAndConstants.DELETED_ENTRY + entryText)
                        .withShouldEndSession(true);
            } catch (NullPointerException e) {
                builder.withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.ASK_FOR_ENTRIES_FIRST)
                        .withSpeech(PhrasesAndConstants.ASK_FOR_ENTRIES_FIRST)
                        .withShouldEndSession(false);
            }
        } else {
            builder.withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.NOT_DELETED)
                    .withSpeech(PhrasesAndConstants.NOT_DELETED)
                    .withShouldEndSession(false);
        }

        return builder.build();
    }
}

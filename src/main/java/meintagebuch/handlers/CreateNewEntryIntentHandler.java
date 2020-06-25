package meintagebuch.handlers;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.*;
import com.amazon.ask.response.ResponseBuilder;
import meintagebuch.PhrasesAndConstants;

import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

/**
 * puts the entrytext in the sessionattributes, redirect user to SaveEntry Intent or CancelEntry Intent
 */
public class CreateNewEntryIntentHandler implements RequestHandler {
    /**
     * returns true if class can handle the HandlerInput -> else: false
     * @param handlerInput is the voice input as HandlerInput
     * @return bool value: true if voice input contains the intent "Eintrag_Erstellen", else false
     */
    @Override
    public boolean canHandle(final HandlerInput handlerInput) {
        return handlerInput.matches(intentName("Eintrag_Erstellen"));
    }

    /**
     * Method that waits for the content of the diary entry using HandlerInput,
     * repeats it and then asks whether the entry should be saved or discarded.
     * @param input voice/text as HandlerInput
     * @return Response as card and speech -> 'say your story'
     */
    @Override
    public Optional<Response> handle(final HandlerInput input) {

        // get entry from requestSlot
        Request request = input.getRequestEnvelope().getRequest();
        IntentRequest intentRequest = (IntentRequest) request;
        Intent intent = intentRequest.getIntent();
        Map<String, Slot> slots = intent.getSlots();

        // Get the entry slot from the list of slots.
        Slot newEntrySlot = slots.get(PhrasesAndConstants.ENTRY_SLOT);
        String newEntrySlotVal = newEntrySlot.getValue();
        // save entryText in session attributes
        AttributesManager attributesManager = input.getAttributesManager();
        Map<String, Object> sessionAttributes = attributesManager.getSessionAttributes();


        sessionAttributes.put(PhrasesAndConstants.ENTRYTEXT_KEY_SESSION, newEntrySlotVal);
        attributesManager.setSessionAttributes(sessionAttributes);
        ResponseBuilder responseBuilder = input.getResponseBuilder();
        responseBuilder.withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.SAVE_OR_CANCEL)
                .withSpeech(PhrasesAndConstants.SAVE_OR_CANCEL)
                .withReprompt(PhrasesAndConstants.SAVE_OR_CANCEL)
                .withShouldEndSession(false);
        return responseBuilder.build();

    }
}

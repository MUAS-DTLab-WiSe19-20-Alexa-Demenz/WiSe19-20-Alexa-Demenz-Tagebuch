package meintagebuch.handlers;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import meintagebuch.PhrasesAndConstants;

import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

/**
 * RequestHandler that can handle the intent entry Cancel.
 */
public class CancelNewEntryIntentHandler implements RequestHandler {

  /**
   * returns true if class can handle the HandlerInput -> else: false.
   *
   * @param input is the voice input as HandlerInput
   * @return bool value: true if voice input contains the intent "Eintag_Einsprechen_Abbruch", else false
   */
  @Override
  public boolean canHandle(final HandlerInput input) {
    return input.matches(intentName("Eintrag_Einsprechen_Abbruch"));
  }


  /**
   * Deletes previously spoken entry, and gives response as card and as speech.
   *
   * @param input is the voice input
   * @return Response as card and speech -> 'entry canceled'
   */
  @Override
  public Optional<Response> handle(final HandlerInput input) {
    // delete entry from sessionAttributes
    AttributesManager attributesManager = input.getAttributesManager();
    Map<String, Object> sessionAttributes = attributesManager.getSessionAttributes();
    sessionAttributes.remove(PhrasesAndConstants.ENTRYTEXT_KEY_SESSION);
    return input.getResponseBuilder()
        .withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.SAY_CANCEL_ENTRY)
        .withSpeech(PhrasesAndConstants.SAY_CANCEL_ENTRY)
        .withShouldEndSession(false)
        .build();
  }
}

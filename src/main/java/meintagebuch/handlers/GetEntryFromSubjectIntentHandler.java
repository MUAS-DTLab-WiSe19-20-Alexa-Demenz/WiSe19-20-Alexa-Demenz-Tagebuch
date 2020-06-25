package meintagebuch.handlers;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.*;
import com.amazon.ask.response.ResponseBuilder;
import meintagebuch.PhrasesAndConstants;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.amazon.ask.request.Predicates.intentName;

/**
 * ReqeustHandler that handles.
 */
public class GetEntryFromSubjectIntentHandler implements RequestHandler {

    /**
     * Can it handle?
     *
     * @param input .
     * @return boolean.
     */
    @Override
    public boolean canHandle(final HandlerInput input) {
        return input.matches(intentName("Eintrag_Abrufen"));
    }

    /**
     * @param input HandlerInput of Alexa.
     * @return reponse as Optional
     */
    @Override
    public Optional<Response> handle(final HandlerInput input) {
        ResponseBuilder responseBuilder = input.getResponseBuilder();
        // get subject from requesty
        Request request = input.getRequestEnvelope().getRequest();
        IntentRequest intentRequest = (IntentRequest) request;
        Intent intent = intentRequest.getIntent();
        Map<String, Slot> slots = intent.getSlots();
        // Get tagSlot from slots
        Slot tagSlot = slots.get(PhrasesAndConstants.TAG_SLOT);
        Slot dateSlot = slots.get(PhrasesAndConstants.DATE_SLOT);

        // if slots are filled add it to a list with the tags that should match an entry
        List<String> subjectsToSearchFor = new ArrayList<>();
        try {
            // replace today,yesterday with the date
            String tagVal = tagSlot.getValue().toLowerCase();
            if (tagVal.contains(PhrasesAndConstants.TODAY)
                    || tagVal.contains(PhrasesAndConstants.YESTERDAY)
                    || tagVal.contains(PhrasesAndConstants.BEFOREYESTERDAY)) {
                LocalDate todayDate = LocalDate.now();
                tagVal = tagVal.replace(PhrasesAndConstants.TODAY, PhrasesAndConstants.formatDateToString(todayDate))
                        .replace(PhrasesAndConstants.BEFOREYESTERDAY, PhrasesAndConstants.formatDateToString(todayDate.minusDays(2)))
                        .replace(PhrasesAndConstants.YESTERDAY, PhrasesAndConstants.formatDateToString(todayDate.minusDays(1)));

            }
            // split value of tagSlot as there could be more than one tag to search for
            subjectsToSearchFor.addAll(Arrays.asList(tagVal.split(" ")));
        } catch (NullPointerException e) {
            // ignore
        }
        try {
            // convert AMAZON.Date to our date format ->
            String date = PhrasesAndConstants.formatDateToString(LocalDate.parse(dateSlot.getValue()));
            subjectsToSearchFor.add(date);
        } catch (NullPointerException e) {
            // ignore
        }


        // search for the subjects in persistent attributes
        AttributesManager attributesManager = input.getAttributesManager();
        Map<String, Object> persistentAttributes = attributesManager.getPersistentAttributes();

        List<String> possibleEntries = new ArrayList<>();
        // collect the entrysIds that could match
        for (String subject : subjectsToSearchFor) {
            List<String> entryIds = (List) persistentAttributes.getOrDefault(subject, new ArrayList());
            if (!entryIds.isEmpty()) {
                possibleEntries.addAll(entryIds);
            }
        }
        // check if there is an entry with all of this tags
        // if so there must be subjectsToSearchFor.size() duplicates of this entryId in the list
        final List<String> entries = possibleEntries;
        possibleEntries = possibleEntries.stream().filter(entryId -> Collections.frequency(entries, entryId) >= subjectsToSearchFor.size()).collect(Collectors.toList());
        // if there are no tags in persistent attributes that matches the input respond with no entry with tags are found
        try {
            if (possibleEntries.isEmpty()) {
                throw new NullPointerException("No possible entries");
            } else if (possibleEntries.size() == 1) {
                // if there is just one matching entry
                // get the entryText from persistent attributes
                String entryText = (String) persistentAttributes
                        .getOrDefault(PhrasesAndConstants.ENTRY_KEY_PERSISTANT + possibleEntries.get(0), "default");
                if (entryText.equals("default")) {
                    throw new NullPointerException("error in the persistant Attributes cant find an entry that is assigned to a Subject");
                } else {
                    attributesManager.getSessionAttributes().put(PhrasesAndConstants.ENTRY_TO_EDIT_KEY, possibleEntries);
                    responseBuilder
                            .withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.THE_ENTRY_IS)
                            .withSpeech(PhrasesAndConstants.THE_ENTRY_IS + entryText)
                            .withShouldEndSession(false);
                }
            } else {
                // tell user there are multiple entries
                StringBuilder builder = new StringBuilder();
                // now remove all duplicates in possible entries
                Set<String> entriesSet = new TreeSet<>(possibleEntries);
                entriesSet.forEach(e -> builder.append(persistentAttributes.get(PhrasesAndConstants.ENTRY_KEY_PERSISTANT + e)).append(". "));
                String response = builder.toString();
                responseBuilder
                        .withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.FOUND_MULTIPLE_ENTRIES)
                        .withSpeech(PhrasesAndConstants.FOUND_MULTIPLE_ENTRIES + response)
                        .withShouldEndSession(false);
                // store entryIds in session_Attributes
                Comparator<String> idComparator
                        = Comparator.comparing(
                        Integer::parseInt, Integer::compareTo);

                TreeSet<String> treeSet = new TreeSet<>(idComparator);
                treeSet.addAll(entriesSet);
                attributesManager.getSessionAttributes().put(PhrasesAndConstants.ENTRY_TO_EDIT_KEY, treeSet);
            }

        } catch (NullPointerException e) {
            responseBuilder
                    .withSimpleCard(PhrasesAndConstants.CARD_TITLE, PhrasesAndConstants.NO_ENTRY_WITH_THIS_TAGS)
                    .withSpeech(PhrasesAndConstants.NO_ENTRY_WITH_THIS_TAGS)
                    .withShouldEndSession(false);
        }
        return responseBuilder.build();
    }


}

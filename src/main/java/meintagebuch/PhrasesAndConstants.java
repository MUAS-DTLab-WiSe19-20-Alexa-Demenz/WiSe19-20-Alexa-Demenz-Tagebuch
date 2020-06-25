package meintagebuch;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class PhrasesAndConstants {

    private PhrasesAndConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static final long DAYS_TO_SAVE_DATA = 7;
    public static final String CONFIRMATION_STATUS_SLOT = "confirmationStatus";
    public static final String CONFIRMATION_STATUS_SLOT_CONFIRMED = "CONFIRMED";
    public static final String CONFIRMATION_STATUS_SLOT_DENIED = "DENIED";
    public static final String ASK_FOR_ENTRIES_FIRST = "Du musst zuerst Einträge abrufen, bevor du sie löschen oder Schlagwörter hinzufügen kannst";
    public static final String CARD_TITLE = "MeinTagebuch";
    public static final String DELETED_ENTRY = "Ich habe folgenden Eintrag verworfen: ";
    public static final String ADDED_NEW_SUBJECTS = "Ich habe die neuen Schlagwörter hinzugefügt";
    public static final String ENTRY_KEY_PERSISTANT = "ENTRY_MAP";
    public static final String ENTRYID_KEY_PERSISTANT = "ENTRY_ID";
    public static final String ENTRY_TO_EDIT_KEY = "ENTRY_TO_EDIT";
    public static final String SUBJECT_SLOT = "subject";
    public static final String ENTRY_SLOT = "new_entry";
    public static final String ENTRY_NR_SLOT = "entry_nr";
    public static final String TAG_SLOT = "Tag";
    public static final String DATE_SLOT = "Datum";
    public static final String ENTRYTEXT_KEY_SESSION = "NEW_ENTRY_SESSION";
    public static final String SUBJECT_IMPORTANT_KEY = "wichtig";
    public static final String HELP_REPROMPT = "Du kannst einen Eintrag erstellen in dem du sagst Eintrag erstellen und danach diese Abrufen. Probier es doch einfach aus.";
    public static final String HELP = "Du kannst mir deine Erlebnisse mitteilen. " + HELP_REPROMPT;
    public static final String WELCOME = "Hallo! Ich bin dein Tagebuch.";
    public static final String NOT_DELETED = "Löschen abgebrochen";
    public static final String YOUR_NEW_TAGS = " du hast folgende Schlagwörter gesetzt: ";
    public static final String YOUR_ENTRY_WAS = ". dein eintrag war: ";
    public static final String SAY_CANCEL_ENTRY = "Ich habe die Aufnahme verworfen. Sage erneut Eintrag erstellen um neu anzufangen.";
    public static final String SAY_CANCEL_SUBJECTS = "Ich habe die Schlagwörter verworfen. Sage erneut Eintrag speichern um die Schlagwörter neu zu setzen. ";
    public static final String SAY_MAKE_ENTRY_FIRST = "Du musst zuerst einen Eintrag erstellen, bevor du Schlagwörter hinzufügen kannst. " + HELP_REPROMPT;
    public static final String SAY_MAKE_ENTRY = "Ich erstelle einen neuen Eintrag! ";
    public static final String SAY_YOU_CAN_CHANGE_ENTRY_1 = "Sage Ändere Eintrag eins um neue Schlagwörter hinzuzufügen. Sage Lösche Eintrag eins um diesen Eintrag zu löschen";
    public static final String SAY_YOU_CAN_CHANGE_ENTRY_2 = "Sage Ändere Eintrag eins oder zwei oder drei etc. um neue Schlagwörter hinzuzufügen. Sage Lösche Eintrag eins oder zwei oder drei um diesen Eintrag zu löschen";
    public static final String SAY_TAGS_CHOSEN = " du hast folgende Schlagwörter gesetzt: ";
    public static final String SAY_ENTRY_WAS = ". dein eintrag war: ";
    public static final String SAVE_OR_CANCEL = "Eintrag speichern oder Eintrag abbrechen?";
    public static final String NO_ENTRY_WITH_THIS_TAGS = "Ich habe leider keine Einträge zu diesen Schlagwörtern gefunden.";
    public static final String THE_ENTRY_IS = "Ihr gesuchter Eintrag lautet: ";
    public static final String FOUND_MULTIPLE_ENTRIES = "Ich habe folgende Einträge gefunden: ";
    public static final String YESTERDAY = "gestern";
    public static final String TODAY = "heute";
    public static final String BEFOREYESTERDAY = "vorgestern";


    public static String formatDateToString(LocalDate date) {
        return date.getDayOfMonth() + "." + date.getMonthValue() + "." + date.getYear();
    }

    /**
     * removes the entry and the references to it from persistent attributes
     *
     * @param handlerInput current handlerInput
     * @param entryId      entryId that should be deleted
     */
    public static void deleteEntryFromDB(HandlerInput handlerInput, String entryId) {
        // get persistentAttributes
        AttributesManager attributesManager = handlerInput.getAttributesManager();
        Map<String, Object> persistentAttributes = attributesManager.getPersistentAttributes();
        // go through tags and delete
        List<String> tagsToEdit = persistentAttributes.entrySet().stream()
                .filter(e -> e.getValue() instanceof java.util.ArrayList)
                .filter(e ->
                        {
                            List<String> list = (List<String>) e.getValue();
                            return (list.isEmpty() || list.contains(entryId));
                        }
                ).map(Map.Entry::getKey).collect(Collectors.toList());
        // remove entry
        persistentAttributes.remove(PhrasesAndConstants.ENTRY_KEY_PERSISTANT + entryId);
        // if list of entries for a tag is empty -> delete it
        for (String tag : tagsToEdit) {
            List<String> ids = (List<String>) persistentAttributes.get(tag);
            if (ids.isEmpty() || ids.size() == 1) {
                persistentAttributes.remove(tag);
            } else {
                // just remove the entryId from the tagslist
                ids.remove(entryId);
                persistentAttributes.put(tag, ids);
            }
        }
        // now save it
        attributesManager.setPersistentAttributes(persistentAttributes);
        attributesManager.savePersistentAttributes();
        persistentAttributes.remove(PhrasesAndConstants.ENTRYID_KEY_PERSISTANT + entryId);

    }

}

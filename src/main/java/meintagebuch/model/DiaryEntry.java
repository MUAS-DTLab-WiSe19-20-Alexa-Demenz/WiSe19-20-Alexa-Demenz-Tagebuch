package meintagebuch.model;

import meintagebuch.PhrasesAndConstants;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * holds data of one entry.
 */
public class DiaryEntry {
  /**
   * counts ids.
   */
  private static int diaryIdCounter = 0;

  /**
   * timestamp of the local date and time.
   */
  private final LocalDateTime timestamp;

  /**
   * id of the entry.
   */
  private final int diaryEntryId;

  /**
   * text of entry.
   */
  private String entryText;

  /**
   * subjects.
   */
  private Set<DiarySubject> subjects;

  /**
   * calls the other constructor with subject = null and entrytext = entrytext.
   *
   * @param paramEntryText the diary entry
   */
  public DiaryEntry(final String paramEntryText) {
    timestamp = LocalDateTime.now(); // get the time and date right now
    this.diaryEntryId = diaryIdCounter++;
    this.entryText = paramEntryText;
    subjects = new HashSet<>();
    subjects.add(new DiarySubject(getDate()));
    subjects.add(new DiarySubject(getDay()));
    subjects.add(new DiarySubject(getDayTime()));
  }

  /**
   * Constructor for testing with possibility to manipulate the time/date.
   *
   * @param paramEntryText .
   * @param clock          .
   */
  public DiaryEntry(final String paramEntryText, final Clock clock) {
    timestamp = LocalDateTime.now(clock); // get the time and date right now
    this.diaryEntryId = diaryIdCounter++;
    this.entryText = paramEntryText;
    subjects = new HashSet<>();
    subjects.add(new DiarySubject(getDate()));
    subjects.add(new DiarySubject(getDay()));
    subjects.add(new DiarySubject(getDayTime()));
  }

  /**
   * .
   *
   * @return .
   */
  public static int getDiaryIdCounter() {
    return diaryIdCounter;
  }

  /**
   * .
   *
   * @param paramDiaryIdCounter .
   */
  public static void setDiaryIdCounter(final int paramDiaryIdCounter) {
    DiaryEntry.diaryIdCounter = paramDiaryIdCounter;
  }

  /**
   * Adds new subject as DiarySubject to Set subject.
   *
   * @param subject .
   */
  public void addNewSubject(final String subject) {
    subjects.add(new DiarySubject(subject));
  }

  /**
   * Adds subject Set (each subject as String) to Set subject.
   *
   * @param paramSubjects is a Set of subjects. (each subject as String)
   */
  public void addNewSubjects(final Set<String> paramSubjects) {
    for (String subject : paramSubjects) {
      addNewSubject(subject);
    }
  }

  /**
   * .
   *
   * @return entryText as String.
   */
  public String getEntryText() {
    return entryText;
  }

  /**
   * sets new entry text as String.
   *
   * @param newEntryText .
   */
  public void setEntryText(final String newEntryText) {
    entryText = newEntryText;
  }

  /**
   * .
   *
   * @return timestamp as LocalDateTime
   */
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  /**
   * .
   *
   * @return entry id as int
   */
  public int getDiaryEntryId() {
    return diaryEntryId;
  }

  /**
   * .
   *
   * @return entry id as String (for output).
   */
  public String getDiaryEntryIdAsString() {
    return String.valueOf(getDiaryEntryId());
  }

  /**
   * .
   *
   * @return time Stamp as String (format: DD.MM.YYYY)
   */
  public String getDate() {
    return PhrasesAndConstants.formatDateToString(timestamp.toLocalDate());
  }

  /**
   * .
   *
   * @return weekday of the Week -> Montag/Dienstag ...
   */
  public String getDay() {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE", Locale.GERMAN);
    return timestamp.format(formatter);
  }

  /**
   * .
   *
   * @return daytime as Text -> "Vormittag, Nachmittag ...".
   */
  public String getDayTime() {
    int tmp = timestamp.getHour();
    String retValue;
    if (tmp >= 6 && tmp < 8) {
      retValue = "Morgen";
    } else if (tmp >= 8 && tmp < 11) {
      retValue = "Vormittag";
    } else if (tmp >= 11 && tmp < 13) {
      retValue = "Mittag";
    } else if (tmp >= 13 && tmp < 17) {
      retValue = "Nachmittag";
    } else if (tmp >= 17 && tmp < 21) {
      retValue = "Abend";
    } else {
      retValue = "Nacht";
    }
    return retValue;
  }

  /**
   * .
   *
   * @return set of Subjects
   */
  public Set<DiarySubject> getSubjects() {
    return subjects;
  }

  /**
   * .
   *
   * @param paramSubjects .
   */
  public void setSubjects(final Set<DiarySubject> paramSubjects) {
    this.subjects = paramSubjects;
  }

  /**
   * For testing.
   *
   * @return .
   */
  public boolean isValid() {
    return (entryText != null && !entryText.isEmpty());
  }


}



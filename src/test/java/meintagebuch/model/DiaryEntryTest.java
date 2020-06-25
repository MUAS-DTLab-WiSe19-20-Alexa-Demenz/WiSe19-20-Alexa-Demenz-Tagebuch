package meintagebuch.model;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class DiaryEntryTest {
  @Test
  public void validateSettersAndGetters() throws Exception {

    PojoClass activityPojo = PojoClassFactory.getPojoClass(DiaryEntry.class);

    Validator validator = ValidatorBuilder.create()
        // Lets make sure that we have a getter and a setter for every field defined.
        .with(new SetterMustExistRule()).with(new GetterMustExistRule())

        // Lets also validate that they are behaving as expected
        .with(new SetterTester()).with(new GetterTester()).build();

    // Start the Test
    validator.validate(activityPojo);
  }

  @Test
  public void validateAddSubjects() {
    Set<String> stringSet = new HashSet<String>();
    stringSet.addAll(Arrays.asList(new String[]{"test1", "test2"}));
    DiaryEntry diaryEntry = new DiaryEntry("schwimmen");
    diaryEntry.addNewSubjects(stringSet);
    assertTrue(diaryEntry.getSubjects().toString().contains("test1"));
    assertTrue(diaryEntry.getSubjects().toString().contains("test2"));
  }

  @Test
  public void validateEntryIdToString() {
    DiaryEntry diaryEntry = new DiaryEntry("schwimmen");
    assertEquals(String.valueOf(DiaryEntry.getDiaryIdCounter() - 1), diaryEntry.getDiaryEntryIdAsString());
  }

  @Test
  public void validateDate() {
    DiaryEntry diaryEntry = new DiaryEntry("schwimmen");
    assertTrue(diaryEntry.getDate().contains(String.valueOf(LocalDate.now().getMonthValue())));
    assertTrue(diaryEntry.getDate().contains(String.valueOf(LocalDate.now().getDayOfMonth())));
    assertTrue(diaryEntry.getDate().contains(String.valueOf(LocalDate.now().getYear())));
  }

  private Clock manipulateDayTime(int hour) {
    hour -= 1;
    String h = String.valueOf(hour);
    if (h.length() == 1)
      h = "0" + h;
    return Clock.fixed(Instant.parse("2014-12-22T" + (h) + ":12:30.00Z"), ZoneId.systemDefault());
  }

  @Test
  public void validateDayTime() {
    DiaryEntry diaryEntry;
    ArrayList<String> daytimes = new ArrayList<>();
    daytimes.addAll(Arrays.asList(new String[]{"Nacht", "Abend", "Morgen", "Nachmittag", "Vormittag", "Mittag"}));
    for (int i = 1; i <= 24; i++) {
      diaryEntry = new DiaryEntry("schwimmen", manipulateDayTime(i));
      assertTrue(daytimes.contains(diaryEntry.getDayTime()));
    }
  }

  @Test
  public void validateIsValid() throws Exception {
    DiaryEntry diaryEntry_null = new DiaryEntry(null);
    assertEquals("isNotValid_null", false, diaryEntry_null.isValid());
    DiaryEntry diaryEntry_empty = new DiaryEntry("");
    assertEquals("isNotValid_empty", false, diaryEntry_empty.isValid());
    DiaryEntry diaryEntry = new DiaryEntry("schwimmen");
    assertEquals("isValid", true, diaryEntry.isValid());
    assertFalse(diaryEntry.getSubjects().isEmpty());
  }
}

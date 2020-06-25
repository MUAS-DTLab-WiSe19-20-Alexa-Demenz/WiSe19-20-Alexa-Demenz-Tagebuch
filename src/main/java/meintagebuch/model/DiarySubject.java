package meintagebuch.model;

public class DiarySubject {

  /**
   * String of the subject.
   */
  private final String subject;


  /**
   * Constructor for a new DiarySubject.
   *
   * @param paramSubject represents the spoken keyword as String
   */
  public DiarySubject(final String paramSubject) {
    this.subject = paramSubject;
  }

  /**
   * @return stored subject as String
   */
  public String toString() {
    return subject.toLowerCase();
  }

}

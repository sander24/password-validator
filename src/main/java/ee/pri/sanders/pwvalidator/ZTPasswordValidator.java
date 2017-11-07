package ee.pri.sanders.pwvalidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.passay.AllowedCharacterRule;
import org.passay.EnglishSequenceData;
import org.passay.IllegalCharacterRule;
import org.passay.IllegalSequenceRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RepeatCharacterRegexRule;
import org.passay.Rule;
import org.passay.RuleResult;
import org.passay.UsernameRule;

public class ZTPasswordValidator {
  
  public static void main(String[] args) {
    List<Rule> rules = Arrays.asList(
        new LengthRule(8, 64),
        //new AllowedCharacterRule(getPrintableAsciiCharacters()),
        //new IllegalCharacterRule(new char[] {'@', '$'}),
        new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 4, true, false),
        new IllegalSequenceRule(EnglishSequenceData.Numerical, 4, true, false),
        new IllegalSequenceRule(EnglishSequenceData.USQwerty, 4, true, false),
        new RepeatCharacterRegexRule(4),
        new UsernameRule()
    );
    
    List<PasswordDTO> testPasswords = Arrays.asList(
        new PasswordDTO("correcthorsebatterystaple", true),
        new PasswordDTO("olga", false), // too short
        new PasswordDTO("anatooliõäü", true),
        new PasswordDTO("purple cow", true),
        new PasswordDTO("asgsadga@fsagasga", true),
        new PasswordDTO("ABC asdgkhas", true),
        new PasswordDTO("ABCD asdgkhas", false),  // too long sequence!
        new PasswordDTO("agasgasdg", true),
        new PasswordDTO("qwertyuio", false),  // too long qwerty sequence 
        new PasswordDTO("12345678", false),  // too long alphanumeric sequence
        new PasswordDTO("987654321", false),  // too long alphanumeric sequence
        new PasswordDTO("öööbikud", true),
        new PasswordDTO("ööööbik", false), // too long sequence
        new PasswordDTO("sander_pw", false) // this contains my username
    );
    
    PasswordValidator validator = new PasswordValidator(rules);
    testPasswords.stream()
      .forEach(testPassword -> {
        String password = testPassword.password;
        System.out.printf("=== VALIDATING PASSWORD '%s' \n", password);
        
        // My username is always "sander"
        PasswordData pwd = new PasswordData("sander", password);
        
        RuleResult result = validator.validate(pwd);
        if (result.isValid()) {
          System.out.println("- OK!");
        }
        else {
          validator.getMessages(result).stream().forEach(System.out::println);
          System.out.println(result.toString());
        }
        if (result.isValid() != testPassword.shouldBeCorrect) {
          throw new RuntimeException("!!!! TEST FAILED !!!");
        }
        System.out.println("\n");
      });
    
  }
  
  /**
   * Return an array of printable ASCII characters.
   */
  public static char[] getPrintableAsciiCharacters() {
    List<Character> printableChars = new ArrayList<Character>();
    IntStream.range(32, 127)
      .forEach(i -> printableChars.add(new Character((char) i)));
    
    char[] charArray = new char[printableChars.size()];
    IntStream.range(0, charArray.length)
      .forEach(i -> charArray[i] = printableChars.get(i));
    
    System.out.println("Allowed characters : ");
    System.out.println(charArray);
    System.out.println();
    
    return charArray;
  }
  
  private static class PasswordDTO {
    
    public String password;
    
    public boolean shouldBeCorrect;
    
    public PasswordDTO(String password, boolean shouldBeCorrect) {
      this.password = password;
      this.shouldBeCorrect = shouldBeCorrect;
    } 
  }
  
}
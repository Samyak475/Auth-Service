package authservice.utills;

public class ValidationUtils {

    public boolean checkEmailValidation(String email){
       String[]parts = email.split("@");
       if(parts.length>2||parts.length<2){
           return false;
       }
       String startPart= parts[0];
       String domainPart= parts[1];
       String[] domainPartSplit = domainPart.split("\\.");
       if(domainPartSplit.length == 2){
           return true;
       }
       else return false;
    }


}

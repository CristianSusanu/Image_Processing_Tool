//This application extracts valid email addresses from a corrupted database

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class EmailAddressFinder {

    private static ArrayList<String> emailAddresses;

    public static void main(String[] args) {
        emailAddresses = new ArrayList<String>();
        EmailAddressFinder eaf = new EmailAddressFinder();
        eaf.run();
        System.out.println("Email addresses found: " + emailAddresses.size());
    }

    public void run() {

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader("corrupteddb"));

            String input = "";

            PrintWriter pw = new PrintWriter("eaf");

            while ((input = reader.readLine()) != null) {

                input = input.trim();

                ArrayList<String> temp = new ArrayList<String>();

                temp = findEmailAddresses(input);

                for (String t: temp) {
                    emailAddresses.add(t);
                }
            }

            pw.close();
            reader.close();
        }

        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> findEmailAddresses(String input) {

        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> backupList = new ArrayList<String>(); //stores values to be checked for validity

        int atCount = 0;
		int secondLast = 0;
		String firstS = "";

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c  == '@') {
				atCount++;
			}
		}

		while (input.indexOf('@') != -1) {
			int pos = lastDomain(input);
			String t = input.substring(0, pos - 3);
			secondLast = lastDomain(t);
			firstS = input.substring(secondLast, pos);
			backupList.add(firstS);
			input = input.substring(0, secondLast);
		}

		 for (int j = 0; j < backupList.size(); j++) {
 			String email = backupList.get(j);
			if (validLocal(email) == true && validDomain(email) == true && topLevelDomain(email) == true) {
				list.add(email);
			}
		}
		return list;
    }

	public static int lastDomain(String input) {
		String domain = "";
		int position = 0;
		while(input.length() > 2){
			domain = "";
			for (int i = input.length() - 1; i >= input.length() - 3; i--) {
				domain += input.charAt(i);
			}
			if (domain.equals("or.")) {
				position = 	input.length();
				input = input.substring(0, 2);
			} else if(domain.equals("ku.")) {
						position = 	input.length();
						input = input.substring(0, 2);
					} else if(domain.equals("ed.")) {
									position = 	input.length();
									input = input.substring(0, 2);
								} else if(domain.equals("pj.")) {
											position = 	input.length();
											input = input.substring(0, 2);
										} else if(domain.equals("moc")) {
													position = 	input.length();
													input = input.substring(0, 2);
												} else if(domain.equals("ten")) {
															position = 	input.length();
															input = input.substring(0, 2);
														}
			input = input.substring(0, input.length() - 1);
		}
		return position;
	}

	public static boolean validLocal(String input) {
     	boolean valid = false;
     	int counter = 0;
     	int count = 0; //occurances of the period character
     	int position = input.indexOf('@');
     	for (int i = 0; i < position; i++) {
    		char c = input.charAt(i);
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c <= '0' && c <= '9') || c == '_') {
				counter++;
			}
			if (c == '.') {
     			count++;
     		}
		}
		String local = input.substring(0, position + 1);
		if (input.indexOf('.') != 0 && (local.lastIndexOf('.') != (position - 1)) &&(((counter + count) == position && count == 1) || counter == position)) {
			valid = true;
		}
		return valid;
     }

     public static boolean validDomain(String input) {
     	boolean valid = false;
     	int counter = 0;
     	int count = 0;
     	int position = input.lastIndexOf('@');
     	for (int i = position; i < input.length(); i++) {
     		char c = input.charAt(i);
     		if (c >= 'a' && c <= 'z') {
     			counter++;
     		}
     		if (c == '.') {
     			count++;
     		}
     	}
     	String domain = input.substring(position + 1);
     	if ((counter + count == domain.length()) && (count == 1 || count == 2)) {
     		valid = true;
     	}
     	return valid;
     }

     public static boolean topLevelDomain(String input) {
     	boolean valid = false;
     	if (input.endsWith(".net") == true) {
     		valid = true;
     	} else if (input.endsWith(".com") == true) {
     				valid = true;
     			} else if (input.endsWith(".uk") == true) {
     						valid = true;
     					} else if (input.endsWith(".de") == true) {
         							valid = true;
         						} else if (input.endsWith(".jp") == true) {
         									valid =true;
         								} else if (input.endsWith(".ro") == true) {
         											valid = true;
         										}
		return valid;
     }

     public static String restrictedCh(String input) {
		String email = "";
		String output = "";
		String convertedEmail = "";
		int pos = input.lastIndexOf('@');
		if (pos != -1) {
			String t = input.substring(0, pos);
			for (int i = t.length() - 1; i >= 0; i--) {
				char character = t.charAt(i);
				email += character;
			}
			char c = email.charAt(0);
			if ((Character.isLetter(c)) == true || (Character.isDigit(c)) == true || c == '_') {
					convertedEmail += c;
			} else {
						convertedEmail += " ";
			     	 }
			while(email.length() > 1) {
				char c2 = email.charAt(1);
				if (Character.isLetter(c2) == true || Character.isDigit(c2) == true || c2 == '_' || c2 == '.') {
					convertedEmail += c2;
				}
				else {
						email = email.substring(0, 1);
					  }
				email = email.substring(1, email.length());
			}
			for (int j = convertedEmail.length() - 1; j >= 0; j--) {
				output += convertedEmail.charAt(j);
			}
			output += input.substring(input.indexOf('@'));
		} else {
					output = "";
			   }
		return output;
	}
}

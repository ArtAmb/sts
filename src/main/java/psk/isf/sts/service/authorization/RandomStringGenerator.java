package psk.isf.sts.service.authorization;

import java.util.Random;

public class RandomStringGenerator {

	private Integer howManyChars;
	private static String alphabet = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123456789";
	private Random random;

	public RandomStringGenerator(Integer howManyChars) {
		this.howManyChars = howManyChars;
		random = new Random(alphabet.length() - 1);
	}

	public String rand() {
		StringBuffer randomString = new StringBuffer();
		for (int i = 0; i < howManyChars; ++i) {
			randomString.append(alphabet.charAt(random.nextInt()));
		}

		return randomString.toString();
	}

}

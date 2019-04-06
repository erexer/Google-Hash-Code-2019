package GoogleHash1;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Photo {

	public static void main(String[] args) throws IOException {
		Photos();
	}

	public static void Photos() throws IOException {

		List < String > list = Files.readAllLines(Paths.get("a_example.txt"), StandardCharsets.UTF_8);

		String[] arrayOfInputs = list.toArray(new String[list.size()]);

		// create arraylist of sets
		ArrayList < Set < String >> photos = new ArrayList < Set < String >> ();

		// add each set to array list
		int calculatedSlides, h = 0, v = 0, numberSlides = 0, currentIndex = 0;
		for (int i = 1; i < arrayOfInputs.length; i++) {
			// parse total String to get all tags
			Set < String > set = new HashSet < String > ();

			// create array to split string 
			String[] splitStr = arrayOfInputs[i].split("\\s+");

			// determine if horizontal or vertical photo
			if (splitStr[0] == "H") {
				h++;
			} else {
				v++;
			}

			// add each element in split string array to the set
			for (int j = 2; j < splitStr.length; j++) {
				set.add(splitStr[j]);
			}

			photos.add(set);
		}

		calculatedSlides = (h + v) / 2;
		//System.out.println(Arrays.toString(photos.toArray()));

		// set up 2D array of the values of the 'min' function between row i and column j in minArray
		// note this array is symmetric and we don't care about the diagonal
		int[][] minArray = new int[photos.size()][photos.size()];
		for (int i = 0; i < photos.size(); i++) {
			for (int j = i; j < photos.size(); j++) {
				minArray[i][j] = minFunction(photos.get(i), photos.get(j));
				minArray[j][i] = minArray[i][j];
			}
		}

		Queue < String > q = new LinkedList < > ();

		do {
			String[] splitStr = arrayOfInputs[currentIndex].split("\\s+");

			// deal with vertical photos
			if (splitStr[0] == "V") {
				int min = minArray[currentIndex][0];
				int max = min;
				int minIndex = 0;
				int maxIndex = 0;
				for (int i = 0; i < photos.size(); i++) {
					if (minArray[currentIndex][i] <= min && minArray[currentIndex][i] >= 0) {
						min = minArray[currentIndex][i];
						minIndex = i;
					}
					if (minArray[currentIndex][i] > max) {
						max = minArray[currentIndex][i];
						maxIndex = i;
					}
				}

				// find another vertical photo to glom onto the first vertical photo
				// aka lots of 'temp' variables
				int tempCurrentIndex = minIndex;

				int maxTemp = minArray[tempCurrentIndex][0];
				int maxTempIndex = 0;
				for (int i = 0; i < photos.size(); i++) {
					if (minArray[tempCurrentIndex][i] > max) {
						maxTemp = minArray[tempCurrentIndex][i];
						maxTempIndex = i;
					}
				}

				int trueMax = Math.max(max, maxTemp);

				q.add("" + currentIndex + " " + tempCurrentIndex);

				// blank out the column of the vertical photo added to the photobook
				for (int i = 0; i < photos.size(); i++) {
					minArray[i][maxIndex] = -1;
					minArray[i][currentIndex] = -1;
					minArray[i][tempCurrentIndex] = -1;
				}

				currentIndex = trueMax;

				numberSlides++;
			} else {	// deal with horizontal photos
				String temp = "" + currentIndex;
				q.add(temp);
				int max = minArray[currentIndex][0];
				int maxIndex = 0;

				for (int i = 0; i < photos.size(); i++) {
					if (minArray[currentIndex][i] > max) {
						max = minArray[currentIndex][i];
						maxIndex = i;
					}
				}

				// blank out the column of the horizontal photo added to the photobook
				for (int i = 0; i < photos.size(); i++) {
					minArray[i][maxIndex] = -1;
					minArray[i][currentIndex] = -1;
				}

				currentIndex = maxIndex;
				numberSlides++;
			}

			if (numberSlides == calculatedSlides) {
				break;
			}
		}
		while (true);

		PrintWriter writer = new PrintWriter("output.txt", "UTF-8");

		// loop for dequeueing
		for (int i = 0; i < q.size(); i++) {
			writer.println(q.remove());
		}
		writer.close();
	}

	public static int minFunction(Set < String > A, Set < String > B) {

		Set < String > intersection = new HashSet < String > (A);
		intersection.retainAll(B);

		int tagsCommon = intersection.size();
		int tagsA = A.size() - tagsCommon;
		int tagsB = B.size() - tagsCommon;

		int min = Math.min(tagsA, Math.min(tagsB, tagsCommon));

		return min;
	}
}
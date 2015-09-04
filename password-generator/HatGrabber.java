/*
	Class that is used for drawing a random value
	from a given pool of values. (drawing from a hat)
*/
import java.util.Random;
public class HatGrabber {

	private static int[] markers;

	private static Random generator;

	public HatGrabber(){
		generator = new Random();
	}
	private void msg(String p){
		Passwords.msg(p);
	}
	public int getMostLikelyIndex(int[] values){
		// get the total number of entries
		msg("fetching letter\n");
		int total = 0;
		int entries = 0;
		for (int i : values){
			total+=i;
			if (i>0)
				entries++;
		}
		if (total <=0)
			return generator.nextInt(values.length);
		// populate the array of probabilities
		markers = new int[values.length];
		int j = 0;
		int count = 0;
		for (int i : values){
			if (i>0){
				count +=i;
				markers[j]=count;
			}
			else{
				markers[j]=-1;
			}
			msg(""+markers[j]+" ");
			j++;
		}
		msg("\n");
		int randomNumber = generator.nextInt(total);
		msg("random number = "+randomNumber+"\n");
		int index = iterativeSearch(randomNumber,0);
		msg("selected "+index+"\n");
		return index;
	}
	public int iterativeSearch(int value, int pos){
		// method for finding the index of the value
		if (value<=markers[pos])
			return pos;
		return iterativeSearch(value,++pos);
	}
	public int binarySearch(int value, int low, int high){
		int mid = ((int)(high+low)/2);
		if (value == markers[mid])
			return mid;
		else if (value < markers[mid]){
			if (value <= markers[0])
				return 0;
			else if (value > markers[mid-1])
				return mid;
			return binarySearch(value,low,mid);
		}
		return binarySearch(value,mid,high);
	}
}

public class Block {
	private final int[][] block;
	private final boolean debug = false;

	/* General use methods */
	private void msg(String p){
		/*
			Method to print debugging statements. Toggled on/off by the debug boolean
		*/
		if (debug)
			System.out.print(p);
	}
	public void print(){
		/*
			Prints the block. Used for debugging
		*/
		for (int i = 0; i<4; i++)
			System.out.printf("0x%02x 0x%02x 0x%02x 0x%02x\n",block[0][i],block[1][i],block[2][i],block[3][i]);
	}
	public int[][] getBlocks(){
		return block;
	}

	/* Encryption/decryption processes */
	public void subBytes(){
		/*
			Used for the subBytes() phase of encryption
		*/
		msg("Starting subBytes...");
		for (int i = 0; i<4; i++){
			for (int j = 0; j< 4; j++){
				block[i][j] = SBox.getSub(block[i][j]);
			}
		}
		msg("done!\n");
	}
	public void subBytesInverse(){
		/*
			The inverse of subBytes(), used in decryption
		*/
		msg("Starting subBytes...");
		for (int i = 0; i<4; i++){
			for (int j = 0; j< 4; j++){
				block[i][j] = SBox.getSubInverse(block[i][j]);
			}
		}
		msg("done!\n");
	}
	public void shiftRows(){
		/*
			Used for the shiftRows() phase of encryption
		*/
		msg("Starting shiftRows...");
		for (int row = 0; row<4; row++){
			// shift the row
			for (int times = 0; times<row; times++){
				int temp = block[0][row];
				block[0][row] = block[1][row];
				block[1][row] = block[2][row];
				block[2][row] = block[3][row];
				block[3][row] = temp;
			}
		}
		msg("done!\n");
	}
	public void shiftRowsInverse(){
		/*
			The inverse of shiftRows(), used in decryption
		*/
		msg("Starting shiftRows...");
		for (int row = 0; row<4; row++){
			// shift the row
			for (int times = 0; times<row; times++){
				int temp = block[3][row];
				block[3][row] = block[2][row];
				block[2][row] = block[1][row];
				block[1][row] = block[0][row];
				block[0][row] = temp;
			}
		}
		msg("done!\n");
	}
	public void mixColumns(){
		/*
			Used for the mixColumns() phase of encryption
		*/
		msg("Starting mixColumns\n");

		// create a copy of the array for use in row-major order
		int copy[][] = new int[4][4];
		for (int i = 0; i<4; i++)
			for (int j = 0; j<4; j++)
				copy[i][j] = block[j][i];

		// mix the columns
		for (int i = 0; i<4; i++)
			Matrix.mixColumn2 (copy, i);

		// convert the array back to column-major order
		for (int i = 0; i <4; i++)
			for (int j = 0; j<4; j++)
				block[i][j] = copy[j][i];
		msg("done! (thank god!)\n");
	}
	public void mixColumnsInverse(){
		/*
			The inverse of mixColumns(), used in decryption
		*/
		msg("Starting mixColumns\n");

		// create a copy of the array for use in row-major order
		int copy[][] = new int[4][4];
		for (int i = 0; i<4; i++)
			for (int j = 0; j<4; j++)
				copy[i][j] = block[j][i];

		// mix the columns
		for (int i = 0; i<4; i++)
			Matrix.invMixColumn2 (copy, i);

		// convert the array back to column-major order
		for (int i = 0; i <4; i++)
			for (int j = 0; j<4; j++)
				block[i][j] = copy[j][i];
		msg("done! (thank god!)\n");
	}
	public void addRoundKey(Block key){
		/*
			Adds the specified key to the state.
			Used in decryption and encryption
		*/
		msg("adding round key...");
		int[][] keyBlocks = key.getBlocks();
		for (int i = 0; i<4; i++){
			for (int j = 0; j<4; j++){
				block[i][j] ^= keyBlocks[i][j];
			}
		}
		msg("done!\n");
	}

	public Block(int[] array){
		/*
			Constructor for the data structure
		*/
		block = new int[][]{new int[4],new int[4], new int[4], new int[4]};
		int count = 0;
		for (int i = 0; i<4; i++){
			for (int j = 0; j<4; j++){
				block[i][j] = array[count++];
			}
		}
	}
}
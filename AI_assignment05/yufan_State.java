public class yufan_State {

	private Block currentBlock;
	private int[][] board;
	private final static int transitedStableNum = 1;

	public yufan_State(Block block, int[][] board) {

		this.currentBlock = transitBlock(block);
		this.board = transitBoard(board);
	}

	public Block getCurrentBlock() {
		return currentBlock;
	}

	public void setCurrentBlock(Block currentBlock) {
		this.currentBlock = currentBlock;
	}

	public int[][] getBoard() {
		return board;
	}

	public void setBoard(int[][] board) {
		this.board = board;
	}
	
	public boolean equalState(yufan_State s)
	{
		return equalState(s.getCurrentBlock(),s.getBoard());
	}

	public boolean equalState(Block currentBlock2, int[][] board2) {
		// TODO Auto-generated method stub
		if (equalBlock(currentBlock2) && equalBoard(board2))
			return true;
		return false;
	}

	private boolean equalBoard(int[][] board2) {
		int[][] transitedBoard = transitBoard(board2);
		for (int i = 0; i < transitedBoard.length; i++) {
			for (int j = 0; j < transitedBoard[i].length; j++) {
				if (transitedBoard[i][j] != board[i][j])
					return false;
			}
		}
		return true;
	}

	private int[][] transitBoard(int[][] board2) {
		int[][] transitedBoard = new int[board2.length][board2[0].length];
		for (int i = 0; i < board2.length; i++) {
			for (int j = 0; j < board2[i].length; j++) {
				transitedBoard[i][j] = board2[i][j];
				if (transitedBoard[i][j] != 0) {
					transitedBoard[i][j] = this.transitedStableNum;
				}
			}
		}
		return transitedBoard;
	}

	private boolean equalBlock(Block currentBlock2) {
		Block transitedBlock = transitBlock(currentBlock2);
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if (currentBlock.getPart(i, j) != transitedBlock.getPart(i, j))
					return false;
			}
		}
		return true;
	}

	private Block transitBlock(Block currentBlock2) {
		Block transitedBlock = new Block(currentBlock2.getType(),
				currentBlock2.getX(), currentBlock2.getY());
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if (transitedBlock.getPart(i, j) != 0) {
					transitedBlock.getPartsArray()[i][j] = transitedStableNum;
				}
			}
		}
		return transitedBlock;
	}

}

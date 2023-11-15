package giaoDien;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class TicTacToeGUI extends JFrame {
	private JButton[][] buttons;
	private char currentPlayerMark;
	private boolean isAgainstAI; // Chế đội chơi với máy hay không

	public TicTacToeGUI() {
		setTitle("Tic-Tac-Toe");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(300, 300);
		setLayout(new GridLayout(5, 5));

		buttons = new JButton[5][5];
		currentPlayerMark = 'X';

		// Hiện thị hộp thoại lựa chọn chế độ chơi khi khởi động game
		ShowGameModeDialog();
		initializeButtons();
	}

	// Hiển thị hộp thoại lựa chọn chế độ chơi
	private void ShowGameModeDialog() {
		String[] options = { "Chơi một mình", "Chơi với máy" };
		int selectedOption = JOptionPane.showOptionDialog(null, "Chọn chế độ chơi: ", "Chọn chế độ",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		isAgainstAI = (selectedOption == 1); // Chọn chế độ chơi với máy nếu selectedOption = 1
	}

	// Khởi tạo các nút và xử lí sự kiện click
	private void initializeButtons() {
		for (int row = 0; row < 5; row++) {
			for (int col = 0; col < 5; col++) {
				buttons[row][col] = new JButton();
				buttons[row][col].setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
				buttons[row][col].addActionListener(new ButtonClickListener(row, col));
				add(buttons[row][col]);
			}
		}
	}

	// Xử lý sự kiện khi người chơi click vào ô
	private class ButtonClickListener implements ActionListener {
		private int row;
		private int col;

		public ButtonClickListener(int row, int col) {
			this.row = row;
			this.col = col;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (buttons[row][col].getText().isEmpty()) {
				buttons[row][col].setText(String.valueOf(currentPlayerMark));

				if (checkForWin()) {
					JOptionPane.showMessageDialog(null, "Người chơi " + currentPlayerMark + " thắng!");
					resetBoard();
				} else if (isBoardFull()) {
					JOptionPane.showMessageDialog(null, "Hòa");
					resetBoard();
				} else {
					changePlayer();
					if (isAgainstAI && currentPlayerMark == 'O') {
						makeAIMove();
					}
				}
			}
		}

		// Ktra xem có ai đã thắng chưa
		private boolean checkForWin() {
			return (checkRowsForWin() || checkColumnsForWin() || checkDiagonalsForWin());
		}

		private boolean checkRowsForWin() {
			for (int i = 0; i < 3; i++) {
				if (checkRowCol(buttons[i][0].getText(), buttons[i][1].getText(), buttons[i][2].getText(),
						buttons[i][3].getText(), buttons[i][4].getText())) {
					return true;
				}
			}
			return false;
		}

		private boolean checkColumnsForWin() {
			for (int i = 0; i < 3; i++) {
				if (checkRowCol(buttons[0][i].getText(), buttons[1][i].getText(), buttons[2][i].getText(),
						buttons[3][i].getText(), buttons[4][i].getText())) {
					return true;
				}
			}
			return false;
		}

		private boolean checkDiagonalsForWin() {
			return (checkRowCol(buttons[0][0].getText(), buttons[1][1].getText(), buttons[2][2].getText(),
					buttons[3][3].getText(), buttons[4][4].getText())
					|| checkRowCol(buttons[0][4].getText(), buttons[1][3].getText(), buttons[2][2].getText(),
							buttons[3][1].getText(), buttons[4][0].getText()));
		}

		private boolean checkRowCol(String c1, String c2, String c3, String c4, String c5) {
			return (!c1.isEmpty() && c1.equals(c2) && c2.equals(c3) && c3.equals(c4) && c4.equals(c5));
		}

		// Ktra bảng đã đầy chưa
		private boolean isBoardFull() {
			for (int row = 0; row < 5; row++) {
				for (int col = 0; col < 5; col++) {
					if (buttons[row][col].getText().isEmpty()) {
						return false;
					}
				}
			}
			return true;
		}

		// Đổi lượt chơi
		private void changePlayer() {
			currentPlayerMark = (currentPlayerMark == 'X') ? 'O' : 'X';
		}

		// Đặt lại bảng
		private void resetBoard() {
			for (int row = 0; row < 5; row++) {
				for (int col = 0; col < 5; col++) {
					buttons[row][col].setText("");
				}
			}
		}

		private void makeAIMove() {
			int[] move = getBestMove();
			int row = move[0];
			int col = move[1];
			buttons[row][col].setText(String.valueOf(currentPlayerMark));

			if (checkForWin()) {
				JOptionPane.showMessageDialog(null, "Máy Thắng!");
				resetBoard();
			} else if (isBoardFull()) {
				JOptionPane.showMessageDialog(null, "Hòa");
				resetBoard();
			} else {
				changePlayer();
			}
		}

		// Lấy nước đi tốt nhất cho máy tính sử dụng thuật toán Min Max
		private int[] getBestMove() {
			int[] bestMove = new int[] { -1, -1 };
			int bestScore = Integer.MIN_VALUE;

			for (int row = 0; row < 5; row++) {
				for (int col = 0; col < 5; col++) {
					if (buttons[row][col].getText().isEmpty()) {
						buttons[row][col].setText(String.valueOf(currentPlayerMark));
						int score = minimax(0, false);
						buttons[row][col].setText("");

						if (score > bestScore) {
							bestScore = score;
							bestMove[0] = row;
							bestMove[1] = col;
						}
					}
				}
			}
			return bestMove;
		}

		// Thuật toán minimax
		private int minimax(int dept, boolean isMaximizingPlayer) {
			if (checkForWin()) {
				return isMaximizingPlayer ? -1 : 1;
			} else if (isBoardFull()) {
				return 0;
			}
			int bestScore = isMaximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
			for (int row = 0; row < 5; row++) {
				for (int col = 0; col < 5; col++) {
					if (buttons[row][col].getText().isEmpty()) {
						buttons[row][col].setText(String.valueOf(isMaximizingPlayer ? 'O' : 'X'));
						int score = minimax(dept + 1, isMaximizingPlayer);
						buttons[row][col].setText("");

						if (isMaximizingPlayer) {
							bestScore = Math.max(bestScore, score);
						} else {
							bestScore = Math.min(bestScore, score);
						}

					}
				}
			}
			return bestScore;
		}

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				TicTacToeGUI game = new TicTacToeGUI();
				game.setVisible(true);
			}
		});
	}
}

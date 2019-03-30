import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;

public class DFA {

	public static void main(String[] args) throws IOException {

		FileReader F = new FileReader("./in.in");
		FileWriter Fw = new FileWriter("./out.txt");
		BufferedReader br = new BufferedReader(F);
		BufferedWriter bw = new BufferedWriter(Fw);
		String line;

		while ((line = br.readLine()) != null) {

			// read one DFA
			String[] Q = line.split(",");
			String[] acceptStates = br.readLine().split(",");
			String[] Symbols = br.readLine().split(",");
			String startState = br.readLine();
			String[] transFuncts = br.readLine().split("#");
			String[] inputStream = br.readLine().split("#");

			String DFA_status = "";
			ArrayList<String> result = new ArrayList<>();
			String presentState = "";

			// check Start state
			if (!(checkStartState(startState, Q))) {
				DFA_status = "Invalid start state";
				ignoredDFA(result, inputStream);
			}

			else {

				// check transition states
				String x = checkState(transFuncts, Q);
				if (!(x.equals("allGood"))) {
					DFA_status = "state " + x + " does not exists";
					ignoredDFA(result, inputStream);
				}

				else {

					// checkAcceptStates
					String x1 = checkAcceptStates(acceptStates, Q);
					if (!(x1.equals("allGood"))) {
						DFA_status = "invalid accept state " + x1;
						ignoredDFA(result, inputStream);
					}

					else {

						// check incomplete transition
						String x2 = checkIncompleteTransition(transFuncts);
						if (!(x2.equals("allGood"))) {
							DFA_status = "Incomplete Transition " + x2;
							ignoredDFA(result, inputStream);
						}

						else {

							// check transition input
							String x3 = checkTransitionInput(transFuncts, Symbols);
							if (!(x3.equals("allGood"))) {
								DFA_status = x3 + " is not in the alphabet";
								ignoredDFA(result, inputStream);
							}

							else {

								// check input String
								String x4 = checkInputString(inputStream, Symbols);
								if (!(x4.equals("allGood"))) {
									DFA_status = "invalid input string at " + x4;
								}

								else {
									DFA_status = "DFA constructed";
								}

								// loop over input stream blocks
								for (int k = 0; k < inputStream.length; k++) {

									// Start with the start state
									presentState = startState;

									// loop over each block of inputs
									for (int i = 0; i < inputStream[k].split(",").length; i++) {

										// loop over transition function block
										for (int j = 0; j < transFuncts.length; j++) {

											String[] transition = transFuncts[j].split(",");

											// search for transition which contains the current presentState and current
											// input
											if (transition[0].equals(presentState)
													&& transition[2].equals(inputStream[k].split(",")[i])) {

												// move to the next state
												presentState = transition[1];
											}
										}
									}

									// Check if the presentState is an acceptState
									boolean found = false;
									for (int i = 0; i < acceptStates.length; i++) {
										if (presentState.equals(acceptStates[i])) {
											found = true;
										}
									}
									if (found) {
										result.add("Accepted");
									} else {
										result.add("Rejected");
									}

								}
							}
						}
					}
				}
			}

			// print the result
			bw.write(DFA_status);
			bw.write("\n");
			for (int i = 0; i < result.size(); i++) {
				bw.write(result.get(i));
				bw.write("\n");
			}

			bw.write("\n");

			br.readLine();
		}
		bw.close();
	}

	// check if startState exists in Q
	public static boolean checkStartState(String startState, String[] Q) {

		boolean found = false;

		for (int i = 0; i < Q.length; i++) {

			if (startState.equals(Q[i])) {
				found = true;
			}
		}

		return found;
	}

	// check valid input of transFuncts in Symbols
	public static String checkTransitionInput(String[] transFuncts, String[] Symbols) {

		// loop over transFunct blocks
		for (int i = 0; i < transFuncts.length; i++) {
			boolean found = false;
			String[] transition = transFuncts[i].split(",");

			// loop over giver symbols
			for (int j = 0; j < Symbols.length; j++) {
				if (transition[2].equals(Symbols[j])) {
					found = true;
				}
			}
			// return the input that is not found
			if (!found) {
				return transition[2];
			}

			found = false;
		}

		// return if all inputs are available
		return "allGood";
	}

	// check state exist in Q
	public static String checkState(String[] transFuncts, String[] Q) {

		// loop over the whole transition functions given
		for (int i = 0; i < transFuncts.length; i++) {
			boolean found = false;
			String[] transition = transFuncts[i].split(",");

			if (transition.length == 3) {
				// check existence of presentState
				for (int j = 0; j < Q.length; j++) {
					if (transition[0].equals(Q[j])) {
						found = true;
					}
				}

				// return the state that does not exists
				if (!found) {
					return transition[0];
				}

				found = false;
				// check existence of nextState
				for (int j2 = 0; j2 < Q.length; j2++) {
					if (transition[1].equals(Q[j2])) {
						found = true;
					}
				}

				// return the state that does not exists
				if (!found) {
					return transition[1];
				}
			}

		}

		// return it if all states in transFuncts exists in Q
		return "allGood";
	}

	// check acceptStates exists in transFuncts
	public static String checkAcceptStates(String[] acceptStates, String[] Q) {

		// loop over acceptStates
		for (int i = 0; i < acceptStates.length; i++) {
			boolean found = false;

			// check each acceptState in Q
			for (int j = 0; j < Q.length; j++) {
				if (acceptStates[i].equals(Q[j])) {
					found = true;
				}
			}

			// return the state if not found
			if (!found) {
				return acceptStates[i];
			}

			found = false;
		}

		// return it if all accept states are valid
		return "allGood";
	}

	// check valid input String
	public static String checkInputString(String[] inputStream, String[] Symbols) {

		// loop over inputStream
		for (int i = 0; i < inputStream.length; i++) {
			boolean found = false;
			String[] S = inputStream[i].split(",");

			for (int j = 0; j < S.length; j++) {

				// loop over set of Symbols
				for (int j2 = 0; j2 < Symbols.length; j2++) {
					if (S[j].equals(Symbols[j2])) {
						found = true;
					}
				}

				// return the invalid input String
				if (!found) {
					return S[j];
				}

				found = false;
			}

		}

		// return if all inputs are valid
		return "allGood";
	}

	public static String checkIncompleteTransition(String[] transFuncts) {

		// loop over transition functions
		for (int i = 0; i < transFuncts.length; i++) {
			String[] TF = transFuncts[i].split(",");

			// return incomplete transition
			if (TF.length < 3) {
				return transFuncts[i];
			}
		}

		// return if all transitions are valid
		return "allGood";
	}

	// output of ignored DFA
	public static void ignoredDFA(ArrayList<String> result, String[] inputStream) {

		for (int i = 0; i < inputStream.length; i++) {
			result.add("ignored");
		}
	}
}

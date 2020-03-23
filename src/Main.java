import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Main {
	// AANTAL STEDEN
	private static int n;
	// VEREISTEN
	private static int M;
	// GEGEVENS VAN 1 STAD
	private static int N_k, N_s, K_i, K_j, S_ij;
	// OTHER
	private static ArrayList<int[]> wp_sel;
	private static int[] indexArray;
	private static int omvang;

	public static void main(String[] args) {
		// UTILITIES
		Scanner sc = new Scanner(System.in);
		int geval, Kr, s1;
		int straat, R, K;
		int selectie[];
		// STAD
		int[][] graaf;
		// AFSTANDEN TUSSEN KRUISPUNTEN
		int[][] kruispunten;
		int source, dest, weight;

		// ZOEKEN NAAR DE BESTE SELECTIES
		int lengte_maximaal, lengte_totaal, lengte, lengte_kleinst;

		ArrayList<int[]> M_wachtpostselecties;
		ArrayList<String> uitkomst;

		int MAX = 999999;

		// INLEZE VAN DE INVOER
		n = sc.nextInt();
		for (geval = 0; geval < n; geval++) {
			// *_*_*_*_*_*_*_*_*_*_*_*_*_* INLEZEN INVOER *_*_*_*_*_*_*_*_*_*_*_*_*_*
			// GEGEVENS VAN HUIDIGE STAD ________________________________________________
			M = sc.nextInt();
			N_k = sc.nextInt();
			N_s = sc.nextInt();
			M_wachtpostselecties = new ArrayList<int[]>();
			omvang = 0;
			// GEBRUIK BIJ GENEREREN VAN SELECTIES
			indexArray = new int[N_k];
			for (Kr = 0; Kr < N_k; Kr++) {
				indexArray[Kr] = Kr;
			}

			// GRAAF STELT EEN STAD VOOR (N_s aantal rijen) _____________________________
			graaf = new int[N_s][];

			// N_s AANTAL STRATEN (K_i, K_j, S_ij) ______________________________________
			for (straat = 0; straat < N_s; straat++) {
				// K loopt in het programma tussen [0..N_k-1] en niet [1..N_k]
				K_i = sc.nextInt() - 1;
				K_j = sc.nextInt() - 1;
				S_ij = sc.nextInt();

				// STAD BESTAAT UIT STRATEN {K_i,K_j,S_ij} [N_s*3]
				graaf[straat] = new int[] { Math.min(K_i, K_j), Math.max(K_i, K_j), S_ij };
			}
			// *_*_*_*_*_*_*_*_*_*_*_*_*_* VERWERKEN INVOER *_*_*_*_*_*_*_*_*_*_*_*_*_*
			// INITIALISEER MATRIX MET ALLE AFSTANDEN (S_ij) ____________________________
			kruispunten = new int[N_k][N_k];
			for (R = 0; R < N_k; R++) {
				for (K = 0; K < N_k; K++) {
					kruispunten[R][K] = MAX;
					kruispunten[R][R] = 0; // diagonaal
				}
			}
			// GEGEVENS VAN STRAAT R ... INVULLEN IN KRUISPUNTEN ________________________
			for (R = 0; R < N_s; R++) {
				// GEGEVENS VAN STRAAT R
				source = graaf[R][0];
				dest = graaf[R][1];
				weight = graaf[R][2];
				// KRUISPUNTEN
				kruispunten[source][dest] = kruispunten[dest][source] = weight;
			}
			// MINIMALISEREN VAN AFSTANDEN (S_ij --> D_ij) ______________________________
			// OVERLOOP VOOR ELK KRUISPUNT Kr -->
			for (Kr = 0; Kr < N_k; Kr++) {
				// --> VOOR ALLE ANDERE KRUISPUNTEN -->
				for (K_i = 0; K_i < N_k; K_i++) {
					for (K_j = 0; K_j < N_k; K_j++) {
						// --> OF DE AFSTAND VAN K_i TOT K_j KLEINER KAN VIA Kr -->
						if (kruispunten[K_i][K_j] > kruispunten[K_i][Kr] + kruispunten[Kr][K_j]) {
							// --> PAS DE AFSTAND DAN AAN
							kruispunten[K_i][K_j] = kruispunten[K_i][Kr] + kruispunten[Kr][K_j];
						}
					}
				}
			}
			// ZOEK DE JUISTE UITKOMST
			while (M_wachtpostselecties.size() == 0) {
				// GENEREER ALLE SELECTIES
				omvang++;
				selectie = new int[omvang];
				wp_sel = new ArrayList<int[]>();
				genereerWachtPostSelecties(0, 0, selectie);

				// ZOEK OPTIMALE SELECTIES
				lengte_kleinst = MAX;
				for (int[] wachtpostselectie : wp_sel) {
					lengte_maximaal = 0;
					lengte_totaal = 0;

					// ZOEK NAAR DE LANGSTE LENGTE DIE AFGELEGD MOET WORDEN
					for (Kr = 0; Kr < N_k; Kr++) {
						lengte = MAX;
						// ZOEK LENGTE VAN DICHSTE WACHTPOST TOT Kr
						for (s1 = 0; s1 < omvang; s1++) {
							lengte = Math.min(kruispunten[Kr][wachtpostselectie[s1]], lengte);
						}
						// MAXIMALE LENGTE VERHOOGT INDIEN EEN NIEUWE LENGTE GROTER IS
						lengte_maximaal = Math.max(lengte_maximaal, lengte);
						lengte_totaal += lengte;
					}
					// INDIEN DE SELECTIE OVERAL OPTIJD GERAAKT
					if (lengte_maximaal <= M) {
						// INDIEN DE HUIDGE LENGTE HOORT BIJ DE GROEP MET KLEINSTE LENGTE
						if (lengte_totaal == lengte_kleinst) {
							M_wachtpostselecties.add(wachtpostselectie);
						}
						// INDIEN DE HUDIGE TOTALE LENGTE KLEINER IS DAN DE REEDS KLEINSTE TOTALE
						else if (lengte_totaal < lengte_kleinst) {
							M_wachtpostselecties.clear();
							M_wachtpostselecties.add(wachtpostselectie);
							lengte_kleinst = lengte_totaal;
						}
					}
				}
			}

			// *_*_*_*_*_*_*_*_*_*_*_*_*_*_*_* UITVOER *_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*
			uitkomst = new ArrayList<String>();
			for (int[] wachtpostselectie : M_wachtpostselecties) {
				// K loop in de realiteit tussen [1..N_k] en niet [0..N_k-1]
				for (Kr = 0; Kr < wachtpostselectie.length; Kr++) {
					wachtpostselectie[Kr]++;
				}
				uitkomst.add(Arrays.toString(wachtpostselectie).replaceAll("\\[|\\]|,", ""));
			}
			// ALFABETISCH ______________________________________________________________
			Collections.sort(uitkomst);
			// UITVOER __________________________________________________________________
			for (String str : uitkomst) {
				System.out.println((geval + 1) + " " + str);
			}

		}
		sc.close();

	}

	// METHODE GEVONDEN OP HET INTERNET
	public static void genereerWachtPostSelecties(int i1, int i2, int selectie[]) {
		if (i1 == omvang) {
			wp_sel.add(selectie.clone());
			return;
		}
		if (i2 >= N_k) {
			return;
		}
		selectie[i1] = indexArray[i2];
		genereerWachtPostSelecties(i1 + 1, i2 + 1, selectie);
		genereerWachtPostSelecties(i1, i2 + 1, selectie);
	}

}

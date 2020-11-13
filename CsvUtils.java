/**
 *Application about priority queues, external merge sorting, CSV files and 
 *file handling.
**/
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

import org.apache.log4j.Logger;


/**
 * A class containing only static methods to externally sort simplified CSV
 * files
 */
public class CsvUtils
{
	private static final Logger logger  = Logger.getLogger(CsvUtils.class);

	private static final Path   dataDir = Paths.get("data");

	/**
	 * An accessor method to return the path of your data directory
	 *
	 * @return the path to your data directory
	 */
	public static Path getDataDir()
	{
		return dataDir;
	}

	/**
	 * A sample method to show the basic mechanism for reading and writing CSV
	 * files using the CsvFormatter class. This just copies the input file to
	 * the output file with no changes. However it has to make sure that the
	 * output file is created with the correct CSV header.
	 *
	 * @param fromPath
	 *            The path of the CSV file to read from
	 * @param toPath
	 *            The path of the CSV file to write to
	 * @return true if it manages to complete without throwing exceptions (if
	 *         this were an empty method that you had to implement as part of
	 *         this assignment, you should leave the return value as false until
	 *         you had completed it to avoid unnecessary testing of an
	 *         unimplemented method
	 * @throws Exception
	 *             if anything goes wrong, e.g. if you can't open either file,
	 *             can't read from the fromPath file, can't write to the toPath
	 *             file, if the from file does not match the requirements of the
	 *             simplified CSV file format, etc.
	 */
	public static boolean copyCsv(Path fromPath, Path toPath)
		throws Exception
	{
		// Open both the from and the to files using a "try-with-resource" pattern
		// This ensures that, no matter what happens in terms of returns or exceptions,
		// both files will be correctly closed automatically
		try (Scanner from = new Scanner(fromPath); PrintWriter to = new PrintWriter(toPath.toFile()))
		{
			// Setup the CSV format from the "from" file
			CsvFormatter formatter = new CsvFormatter(from);

			// Output the CSV header row to the "to" file
			formatter.writeHeader(to);

			// copy each non-header row from the "from" file to the "to" file
			String[] row;
			while ((row = formatter.readRow(from)) != null)
				formatter.writeRow(to, row);
		}
		return true;
	}

	/**
	 * Split an (unordered) CSV file into separate smaller CSV files (runs)
	 * containing sorted runs of row, where the rows are sorted in ascending
	 * order of the column identified by the <code>columnName</code> parameter.
	 * This is intended to be the first stage of a merge sort which produces
	 * sorted runs that can then be merged together.
	 * <p>
	 * This code should work on truly huge files: far larger than we can hold in
	 * memory at the same time. To simulate this without using huge files, we
	 * impose a limit on the size of each run, given by the
	 * <code>numRowLimit</code> parameter. Further, NO internal sort algorithms
	 * should be used: e.g. Arrays.sort, Collections.sort, SortedList etc.
	 * Instead a {@link PriorityQueue} must be used to generate the sorted runs:
	 * Have a loop. Inside the loop, read in a maximum of
	 * <code>numRowLimit</code> rows from the input and insert them into the
	 * priority queue and then extract them in order and write them out to a new
	 * split file.
	 * </p>
	 * <p>
	 * The split file should be a sibling (i.e. in the same directory) as the
	 * input file and have a name which is "temp_00000_" followed by the name of
	 * the input file, where the "00000" is replace by a sequence number:
	 * "00000" for the first split file, "00001" for the second etc.
	 * </p>
	 *
	 * @param fromPath
	 *            The relative path where the input file is
	 * @param columnName
	 *            The header name of the column used for sorting
	 * @param numRowLimit
	 *            The maximum number of value rows (not including the header
	 *            row) that can be written into each split file
	 * @return the <code>Path[]</code> of paths for the full list of split files created
	 * @throws Exception
	 *             If anything goes wrong with opening, reading or writing the
	 *             files, or if the input file does not match the simplified CSV
	 *             requirements.
	 */
	public static Path[] splitSortCsv(Path fromPath, String columnName, int numRowLimit)
		throws Exception
	{
		Deque<Path> pathDeque = new LinkedList<>();
		int tempNum = 0;
		int counter = 1;
		String[] row;

		try (Scanner from = new Scanner(fromPath)) {
			CsvFormatter formatter = new CsvFormatter(from);
			CsvFormatter.RowComparator comparator = formatter.new RowComparator(columnName);
			PriorityQueue<String[]> pq = new PriorityQueue<>(comparator);

			while ((row = formatter.readRow(from)) != null) {
				if(counter < numRowLimit) {
					pq.add(row);
					counter++;
				} else {
					Path tmpPath = fromPath.resolveSibling(String.format("temp_%05d_%s", tempNum, fromPath.getFileName()));
					pq.add(row);
					pathDeque.add(tmpPath);
					try (PrintWriter to = new PrintWriter(tmpPath.toFile())) {
						formatter.writeHeader(to);
						do {
							formatter.writeRow(to, pq.poll());
							counter = 1;
						} while(pq.isEmpty() == false);
					}
					tempNum++;
				}
			}

			if(pq.isEmpty() == false) {
				Path tmpPath = fromPath.resolveSibling(String.format("temp_%05d_%s", tempNum, fromPath.getFileName()));
				pathDeque.add(tmpPath);
				try (PrintWriter to = new PrintWriter(tmpPath.toFile())) {
					formatter.writeHeader(to);
					do {
						formatter.writeRow(to, pq.poll());
					} while(pq.isEmpty() == false);
				}
			}
		}
		return pathDeque.toArray(new Path[0]);
	}

	/**
	 * Merge two ordered input CSV files into a single ordered output CSV file
	 *
	 * The two input CSV files must be already ordered on the column specified
	 * by <code>columnName</code> and must have the same CSV format (same number
	 * of columns, same headers in the same order) The output file must
	 * similarly be of the same CSV format and ordered on the same column.
	 *
	 * @param file1Path
	 *            The relative path of the first input file
	 * @param file2Path
	 *            The relative path of the second input file
	 * @param columnName
	 *            The column to order the output file on and, upon which, both
	 *            input files are ordered
	 * @param outputPath
	 *            The relative path of the output file
	 * @return true, if this method has been implemented. If it has not yet been
	 *         implemented, then it returns false and this is used to cause the
	 *         unit test to fail early without doing a lot of unnecessary work
	 * @throws Exception
	 *             If anything goes wrong with opening, reading or writing the
	 *             files, or if the input files do not match the simplified CSV
	 *             requirements or have different CSV formats
	 */
	public static boolean mergePairCsv(Path file1Path, Path file2Path, String columnName, Path outputPath)
		throws Exception
	{
		String[] row1;
		String[] row2;

		try (Scanner file1 = new Scanner(file1Path) ; Scanner file2 = new Scanner(file2Path)) {
			CsvFormatter formatter1 = new CsvFormatter(file1);
			CsvFormatter.RowComparator comparator = formatter1.new RowComparator(columnName);
			CsvFormatter formatter2 = new CsvFormatter(file2);
			row1 = formatter1.readRow(file1);
			row2 = formatter2.readRow(file2);

			try (PrintWriter to = new PrintWriter(outputPath.toFile())) {
				formatter1.writeHeader(to);
				if(row1 != null && row2 != null) {
					while(row1 != null && row2 != null) {
						if(comparator.compare(row1, row2) < 0) {
								formatter1.writeRow(to, row1);
								row1 = formatter1.readRow(file1);
						} else if(comparator.compare(row1, row2) > 0) {
										formatter2.writeRow(to, row2);
										row2 = formatter2.readRow(file2);
						} else if(comparator.compare(row1, row2) == 0) {
							formatter2.writeRow(to, row2);
							row2 = formatter2.readRow(file2);
						}
					}
				}
				if(row1 == null && row2 != null){
					while(row2 != null) {
						formatter2.writeRow(to, row2);
						row2 = formatter2.readRow(file2);
					}
				}
				if(row1 != null && row2 == null){
					while(row1 != null) {
						formatter1.writeRow(to, row1);
						row1 = formatter1.readRow(file1);
					}
				}
			}
		}
		return true;
	}

	/**
	 * Merge a list of ordered input CSV files into a single ordered output CSV
	 * file
	 * <p>
	 * The input CSV files must be already ordered on the column specified by
	 * <code>columnName</code> and must have the same CSV format (same number of
	 * columns, same headers in the same order) The output file must similarly
	 * be of the same CSV format and ordered on the same column.
	 * </p>
	 * <p>
	 * This method should merge all the files together by calling
	 * <code>mergePairCsv(...)</code> on pairs of files, starting with those on
	 * <code>pathList</code>, producing larger and larger intermediate file
	 * until the last pair-wise merge is used to produce the output file.
	 * </p>
	 *
	 * @param pathList
	 *            An array of relative paths of the input files
	 * @param columnName
	 *            The column to order the output file on and, upon which, both
	 *            input files are ordered
	 * @param outputPath
	 *            The relative path of the output file
	 * @return true, if this method has been implemented. If it has not yet been
	 *         implemented, then it returns false and this is used to cause the
	 *         unit test to fail early without doing a lot of unnecessary work
	 * @throws Exception
	 *             If anything goes wrong with opening, reading or writing the
	 *             files, or if the input files do not match the simplified CSV
	 *             requirements or have different CSV formats
	 */
	public static boolean mergeListCsv(Path[] pathList, String columnName, Path outputPath)
		throws Exception
	{
		Deque<Path> paths = new LinkedList<>(Arrays.asList(pathList));
		Path firstFile = pathList[0];
		int i = 1;
		do {
			mergePairCsv(firstFile, pathList[i], columnName, outputPath);
			firstFile = outputPath;
			i++;
		} while(i < pathList.length);
	return true;
	}
}

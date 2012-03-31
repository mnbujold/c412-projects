package helper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Bulk "management" of text files.
 */
public class TextFileHelper
{
    /**
     * Opens a text file for buffered writing.
     */
    public BufferedWriter openTextFile(String fileName)
    throws IOException
    {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        openedTextFiles.add(bw);
        return bw;
    }

    /**
     * Closes all previously opened text files on this helper.
     */
    public void closeTextFiles()
    {
        for (BufferedWriter bw : openedTextFiles)
        {
            try
            {
                bw.flush();
                bw.close();
            }
            catch (IOException e)
            {
                e.printStackTrace(System.err);
            }
        }        
    }

    //--------------------------------------------------------------------------

    /**
     * Stores the currently opened text files.
     */
    private List<BufferedWriter> openedTextFiles =
                                 new LinkedList<BufferedWriter>();
}

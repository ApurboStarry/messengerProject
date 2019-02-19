package TransferFile;

/* Program to copy a src file to destination.
   The name of src file and dest file must be
   provided using command line arguments where
   args[0] is the name of source file and
   args[1] is name of destination file */

import java.io.*;
class src2dest
{
    public static void main(String args[])
            throws FileNotFoundException,IOException
    {

        /* If file doesnot exist FileInputStream throws
           FileNotFoundException and read() write() throws
           IOException if I/O error occurs */
        FileInputStream fis = new FileInputStream("C:\\Users\\ASUS\\Desktop\\test.docx");

        /* assuming that the file exists and need not to be
           checked */
        FileOutputStream fos = new FileOutputStream("C:\\Users\\ASUS\\Desktop\\copiiusd.docx");

        int b;
        while  ((b=fis.read()) != -1)
            fos.write(b);

        /* read() will readonly next int so we used while
           loop here in order to read up to end of file and
           keep writing the read int into dest file */
        fis.close();
        fos.close();
    }
}
